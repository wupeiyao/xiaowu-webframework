package org.xiaowu.wpywebframework.object.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import io.minio.*;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xiaowu.wpywebframework.object.bucket.Bucket;
import org.xiaowu.wpywebframework.object.model.ObjectMetadataResponse;
import org.xiaowu.wpywebframework.object.model.ObjectReadResponse;
import org.xiaowu.wpywebframework.object.properties.MinIOProperties;
import org.xiaowu.wpywebframework.object.service.ObjectStorageService;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MinIOServiceImpl implements ObjectStorageService {

    private final MinioClient minioClient;
    private final MinIOProperties minIOProperties;

    private final Map<String, Map<Integer, byte[]>> uploadCache = new ConcurrentHashMap<>();

    public MinIOServiceImpl(MinioClient minioClient, MinIOProperties minIOProperties) {
        this.minioClient = minioClient;
        this.minIOProperties = minIOProperties;
    }

    @Override
    public String getType() {
        return "minio";
    }

    @Override
    public ObjectWriteResponse putObject(MultipartFile filePart) {
        return putObject(getDefaultBucket(), filePart);
    }

    @Override
    public ObjectWriteResponse putObject(Bucket bucket, MultipartFile filePart) {
        try {
            String originalFilename = filePart.getOriginalFilename();
            String objectId = IdUtil.fastSimpleUUID();
            String suffix = getFileSuffix(originalFilename);

            return putObject(bucket, objectId + "." + suffix,
                    null, null, filePart.getInputStream());
        } catch (IOException e) {
            log.error("上传文件失败", e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    @Override
    public ObjectWriteResponse putObject(String name, InputStream stream) {
        return putObject(getDefaultBucket(), name, null, null, stream);
    }

    @Override
    public ObjectWriteResponse putObject(Bucket bucket, String name, InputStream stream) {
        return putObject(bucket, name, null, null, stream);
    }

    @Override
    public ObjectWriteResponse putObject(Bucket bucket, String name,
                                         Map<String, String> headers,
                                         Map<String, String> metadata,
                                         byte[] bytes) {
        return putObject(bucket, name, headers, metadata, new ByteArrayInputStream(bytes));
    }

    @Override
    public ObjectWriteResponse putObject(Bucket bucket, String name,
                                         Map<String, String> headers,
                                         Map<String, String> metadata,
                                         InputStream stream) {
        try {
            ensureBucketExists(bucket.getName());
            String objectPath = buildObjectPath(bucket, name);
            PutObjectArgs.Builder builder = PutObjectArgs.builder()
                    .bucket(bucket.getName())
                    .object(objectPath)
                    .stream(stream, -1, 10485760); // 10MB part size
            if (headers != null && !headers.isEmpty()) {
                builder.headers(headers);
            }
            if (metadata != null && !metadata.isEmpty()) {
                builder.userMetadata(metadata);
            }
            io.minio.ObjectWriteResponse response = minioClient.putObject(builder.build());

            log.info("文件上传成功: bucket={}, object={}", bucket.getName(), objectPath);
            return response;
        } catch (Exception e) {
            log.error("上传文件到MinIO失败", e);
            throw new RuntimeException("上传文件失败", e);
        }
    }

    @Override
    public ObjectReadResponse getObject(String path) {
        return getObject(path, null, null);
    }

    @Override
    public ObjectReadResponse getObject(String path, Long offset, Long length) {
        try {
            String[] parts = parsePath(path);
            String bucketName = parts[0];
            String objectName = parts[1];

            GetObjectArgs.Builder builder = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName);
            if (offset != null && length != null) {
                builder.offset(offset).length(length);
            }

            try (InputStream stream = minioClient.getObject(builder.build())) {
                ObjectReadResponse response = new ObjectReadResponse();
                response.setBytes(stream.readAllBytes());
                response.setStorage(getType());
                response.setName(getFileName(objectName));
                response.setSuffix(getFileSuffix(objectName));
                response.setObjectId(getObjectId(objectName));
                return response;
            }

        } catch (Exception e) {
            log.error("获取文件失败: path={}", path, e);
            throw new RuntimeException("获取文件失败", e);
        }
    }

    @Override
    public ObjectMetadataResponse getObjectMetadata(String path) {
        try {
            String[] parts = parsePath(path);
            String bucketName = parts[0];
            String objectName = parts[1];
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );

            ObjectMetadataResponse response = new ObjectMetadataResponse();
            response.setStorage(getType());
            response.setBucket(bucketName);
            response.setPath(path);
            response.setName(getFileName(objectName));
            response.setSuffix(getFileSuffix(objectName));
            response.setObjectId(getObjectId(objectName));
            response.setSize((int) stat.size());
            response.setHeaders(convertMapToString(stat.headers().toMultimap()));
            response.setMetadata(convertMapToString(stat.userMetadata()));
            response.setCreateTime(LocalDateTime.now());
            return response;

        } catch (Exception e) {
            log.error("获取文件元数据失败: path={}", path, e);
            throw new RuntimeException("获取文件元数据失败", e);
        }
    }

    @Override
    public void delete(String path) {
        try {
            String[] parts = parsePath(path);
            String bucketName = parts[0];
            String objectName = parts[1];
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            log.info("删除文件成功: path={}", path);
        } catch (Exception e) {
            log.error("删除文件失败: path={}", path, e);
            throw new RuntimeException("删除文件失败", e);
        }
    }

    @Override
    public String uploadFilePart(String fileId, String fileName,
                                 MultipartFile filePart,
                                 Integer chunkIndex,
                                 Integer totalChunks,
                                 String bucketName) throws IOException {
        try {
            String uploadKey = fileId + "_" + fileName;
            uploadCache.putIfAbsent(uploadKey, new ConcurrentHashMap<>());
            Map<Integer, byte[]> chunks = uploadCache.get(uploadKey);
            chunks.put(chunkIndex, filePart.getBytes());

            log.info("接收文件分片: fileId={}, chunkIndex={}/{}", fileId, chunkIndex + 1, totalChunks);
            if (chunks.size() == totalChunks) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for (int i = 0; i < totalChunks; i++) {
                    byte[] chunk = chunks.get(i);
                    if (chunk == null) {
                        throw new RuntimeException("分片缺失: index=" + i);
                    }
                    outputStream.write(chunk);
                }
                String suffix = getFileSuffix(fileName);
                String objectName = fileId + "." + suffix;
                Bucket bucket = createBucket(bucketName);
                putObject(bucket, objectName, null, null, outputStream.toByteArray());
                uploadCache.remove(uploadKey);
                log.info("文件分片上传完成: fileId={}, fileName={}", fileId, fileName);

                return buildObjectPath(bucket, objectName);
            }
            return null;
        } catch (Exception e) {
            log.error("上传文件分片失败", e);
            throw new IOException("上传文件分片失败", e);
        }
    }

    /**
     * 确保桶存在
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );
        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
            );
            log.info("创建桶: {}", bucketName);
        }
    }

    /**
     * 构建对象路径
     */
    private String buildObjectPath(Bucket bucket, String name) {
        if (StrUtil.isNotBlank(bucket.getFolder())) {
            return bucket.getFolder() + "/" + name;
        }
        return name;
    }

    /**
     * 解析路径
     */
    private String[] parsePath(String path) {
        String[] parts = path.split("/", 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException("无效的路径格式: " + path);
        }
        return parts;
    }

    /**
     * 获取文件后缀
     */
    private String getFileSuffix(String filename) {
        if (StrUtil.isBlank(filename)) {
            return "";
        }
        int lastDot = filename.lastIndexOf(".");
        return lastDot > 0 ? filename.substring(lastDot + 1) : "";
    }

    /**
     * 获取文件名（不含后缀）
     */
    private String getFileName(String objectName) {
        String name = objectName;
        if (objectName.contains("/")) {
            name = objectName.substring(objectName.lastIndexOf("/") + 1);
        }
        int lastDot = name.lastIndexOf(".");
        return lastDot > 0 ? name.substring(0, lastDot) : name;
    }

    /**
     * 获取对象ID
     */
    private String getObjectId(String objectName) {
        return getFileName(objectName);
    }

    /**
     * 转换Map为String
     */
    private String convertMapToString(Map<String, ?> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.toString();
    }

    /**
     * 获取默认桶
     */
    private Bucket getDefaultBucket() {
        return createBucket(minIOProperties.getDefaultBucket());
    }

    /**
     * 创建桶对象
     */
    private Bucket createBucket(String bucketName) {
        return new Bucket() {
            @Override
            public String getName() {
                return bucketName;
            }
        };
    }
}

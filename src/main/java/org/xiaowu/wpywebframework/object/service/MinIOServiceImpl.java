package org.xiaowu.wpywebframework.object.service;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.xiaowu.wpywebframework.object.bucket.SimpleBucket;
import org.xiaowu.wpywebframework.object.exception.MinIOException;
import org.xiaowu.wpywebframework.object.properties.MinIOProperties;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinIOServiceImpl implements MinIOService {

    private final MinioClient minioClient;
    private final MinIOProperties minIOProperties;

    public MinIOServiceImpl(MinioClient minioClient, MinIOProperties minIOProperties) {
        this.minioClient = minioClient;
        this.minIOProperties = minIOProperties;
    }


    @Override
    public void initBuckets() {
        try {
            // 创建默认存储桶
            if (StringUtils.hasText(minIOProperties.getDefaultBucket())) {
                if (!bucketExists(minIOProperties.getDefaultBucket())) {
                    createBucket(minIOProperties.getDefaultBucket());
                    log.info("创建默认存储桶成功: {}", minIOProperties.getDefaultBucket());
                }
            }

            // 创建配置的存储桶列表
            List<SimpleBucket> buckets = minIOProperties.getBuckets();
            if (!CollectionUtils.isEmpty(buckets)) {
                for (SimpleBucket bucket : buckets) {
                    if (!bucketExists(bucket.getName())) {
                        createBucket(bucket.getName());
                        log.info("创建存储桶成功: {}", bucket.getName());
                    }
                }
            }

            // 创建业务映射中的存储桶
            if (minIOProperties.getBucketMapping() != null) {
                for (String bucketName : minIOProperties.getBucketMapping().values()) {
                    if (StringUtils.hasText(bucketName) && !bucketExists(bucketName)) {
                        createBucket(bucketName);
                        log.info("创建映射存储桶成功: {}", bucketName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("初始化存储桶失败", e);
        }
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("存储桶创建成功: {}", bucketName);
        } catch (Exception e) {
            log.error("创建存储桶失败: {}", bucketName, e);
            throw new MinIOException("创建存储桶失败: " + bucketName, e);
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            log.error("检查存储桶是否存在失败: {}", bucketName, e);
            throw new MinIOException("检查存储桶是否存在失败: " + bucketName, e);
        }
    }

    @Override
    public void removeBucket(String bucketName) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
            log.info("删除存储桶成功: {}", bucketName);
        } catch (Exception e) {
            log.error("删除存储桶失败: {}", bucketName, e);
            throw new MinIOException("删除存储桶失败: " + bucketName, e);
        }
    }

    @Override
    public String uploadFile(String fileName, InputStream inputStream, String contentType) {
        return uploadFile(minIOProperties.getBucketName(), fileName, inputStream, contentType);
    }

    public String uploadFile(String bucketName, String fileName, InputStream inputStream, String contentType) {
        try {
            if (!this.bucketExists(bucketName)) {
                this.createBucket(bucketName);
            }
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(StringUtils.hasText(contentType) ? contentType : "application/octet-stream")
                            .build()
            );
            log.info("文件上传成功: {} -> {}", fileName, bucketName);
            // 拼接完整URL
            String endpoint = minIOProperties.getEndpoint();
            if (endpoint.endsWith("/")) {
                endpoint = endpoint.substring(0, endpoint.length() - 1);
            }
            return String.format("%s/%s/%s", endpoint, bucketName, fileName);

        } catch (Exception e) {
            log.error("上传文件失败: {} -> {}", fileName, bucketName, e);
            throw new MinIOException("上传文件失败: " + fileName, e);
        }
    }


    @Override
    public String uploadFileByBusinessType(String businessType, String fileName, InputStream inputStream, String contentType) {
        String bucketName = minIOProperties.getBucketName(businessType);
        return uploadFile(bucketName, fileName, inputStream, contentType);
    }

    @Override
    public InputStream downloadFile(String fileName) {
        return downloadFile(minIOProperties.getBucketName(), fileName);
    }

    @Override
    public InputStream downloadFile(String bucketName, String fileName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("下载文件失败: {} <- {}", fileName, bucketName, e);
            throw new MinIOException("下载文件失败: " + fileName, e);
        }
    }

    @Override
    public InputStream downloadFileByBusinessType(String businessType, String fileName) {
        String bucketName = minIOProperties.getBucketName(businessType);
        return downloadFile(bucketName, fileName);
    }

    @Override
    public void deleteFile(String fileName) {
        deleteFile(minIOProperties.getBucketName(), fileName);
    }

    @Override
    public void deleteFile(String bucketName, String fileName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
            log.info("删除文件成功: {} <- {}", fileName, bucketName);
        } catch (Exception e) {
            log.error("删除文件失败: {} <- {}", fileName, bucketName, e);
            throw new MinIOException("删除文件失败: " + fileName, e);
        }
    }

    @Override
    public void deleteFileByBusinessType(String businessType, String fileName) {
        String bucketName = minIOProperties.getBucketName(businessType);
        deleteFile(bucketName, fileName);
    }

    @Override
    public String getPresignedUrl(String fileName, int expiry) {
        return getPresignedUrl(minIOProperties.getBucketName(), fileName, expiry);
    }

    @Override
    public String getPresignedUrl(String bucketName, String fileName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .expiry(expiry, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            log.error("获取预签名URL失败: {} <- {}", fileName, bucketName, e);
            throw new MinIOException("获取预签名URL失败: " + fileName, e);
        }
    }

    @Override
    public String getPresignedUrlByBusinessType(String businessType, String fileName, int expiry) {
        String bucketName = minIOProperties.getBucketName(businessType);
        return getPresignedUrl(bucketName, fileName, expiry);
    }

    @Override
    public List<String> listFiles(String bucketName) {
        try {
            List<String> files = new ArrayList<>();
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .build());

            for (Result<Item> result : results) {
                files.add(result.get().objectName());
            }
            return files;
        } catch (Exception e) {
            log.error("列出文件失败: {}", bucketName, e);
            throw new MinIOException("列出文件失败: " + bucketName, e);
        }
    }

    @Override
    public List<String> listFiles() {
        return listFiles(minIOProperties.getBucketName());
    }

    @Override
    public List<String> listFilesByBusinessType(String businessType) {
        String bucketName = minIOProperties.getBucketName(businessType);
        return listFiles(bucketName);
    }

    @Override
    public String getBucketNameByBusinessType(String businessType) {
        return minIOProperties.getBucketName(businessType);
    }
}

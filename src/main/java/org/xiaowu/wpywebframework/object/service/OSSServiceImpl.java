package org.xiaowu.wpywebframework.object.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.xiaowu.wpywebframework.object.properties.OSSProperties;

import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * OSS服务实现类
 */
@Slf4j
public class OSSServiceImpl implements OSSService {

    private final OSS ossClient;
    private final OSSProperties ossProperties;

    public OSSServiceImpl(OSS ossClient, OSSProperties ossProperties) {
        this.ossClient = ossClient;
        this.ossProperties = ossProperties;
    }

    /**
     * 上传文件（字节数组）
     */
    @Override
    public String uploadFile(byte[] content, String fileName) {
        return uploadFile(new ByteArrayInputStream(content), fileName);
    }

    /**
     * 上传文件（输入流）
     */
    @Override
    public String uploadFile(InputStream inputStream, String fileName) {
        String objectName = buildObjectName(fileName);
        try {
            PutObjectRequest request = new PutObjectRequest(
                ossProperties.getBucketName(),
                objectName,
                inputStream
            );
            ossClient.putObject(request);
            log.info("文件上传成功: {}", objectName);
            return getFileUrl(objectName);
        } catch (Exception e) {
            log.error("文件上传失败: {}", fileName, e);
            throw new RuntimeException("文件上传失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭流失败", e);
                }
            }
        }
    }

    /**
     * 上传本地文件
     */
    @Override
    public String uploadFile(File file) {
        try {
            return uploadFile(new FileInputStream(file), file.getName());
        } catch (FileNotFoundException e) {
            log.error("文件不存在: {}", file.getPath(), e);
            throw new RuntimeException("文件不存在", e);
        }
    }

    /**
     * 下载文件到本地
     */
    @Override
    public void downloadFile(String objectName, String localFilePath) {
        try {
            ossClient.getObject(
                new GetObjectRequest(ossProperties.getBucketName(), objectName),
                new File(localFilePath)
            );
            log.info("文件下载成功: {} -> {}", objectName, localFilePath);
        } catch (Exception e) {
            log.error("文件下载失败: {}", objectName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 下载文件（返回字节数组）
     */
    @Override
    public byte[] downloadFileBytes(String objectName) {
        try (OSSObject ossObject = ossClient.getObject(ossProperties.getBucketName(), objectName);
             InputStream inputStream = ossObject.getObjectContent();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            log.info("文件下载成功: {}", objectName);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("文件下载失败: {}", objectName, e);
            throw new RuntimeException("文件下载失败", e);
        }
    }

    /**
     * 删除文件
     */
    @Override
    public void deleteFile(String objectName) {
        try {
            ossClient.deleteObject(ossProperties.getBucketName(), objectName);
            log.info("文件删除成功: {}", objectName);
        } catch (Exception e) {
            log.error("文件删除失败: {}", objectName, e);
            throw new RuntimeException("文件删除失败", e);
        }
    }

    /**
     * 批量删除文件
     */
    @Override
    public void deleteFiles(List<String> objectNames) {
        try {
            DeleteObjectsRequest request = new DeleteObjectsRequest(ossProperties.getBucketName());
            request.setKeys(objectNames);
            ossClient.deleteObjects(request);
            log.info("批量删除文件成功，数量: {}", objectNames.size());
        } catch (Exception e) {
            log.error("批量删除文件失败", e);
            throw new RuntimeException("批量删除文件失败", e);
        }
    }

    /**
     * 判断文件是否存在
     */
    @Override
    public boolean doesFileExist(String objectName) {
        try {
            return ossClient.doesObjectExist(ossProperties.getBucketName(), objectName);
        } catch (Exception e) {
            log.error("检查文件是否存在失败: {}", objectName, e);
            return false;
        }
    }

    /**
     * 列出指定前缀的文件
     */
    @Override
    public List<String> listFiles(String prefix) {
        try {
            ObjectListing listing = ossClient.listObjects(
                ossProperties.getBucketName(),
                prefix
            );
            return listing.getObjectSummaries().stream()
                .map(OSSObjectSummary::getKey)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("列出文件失败: {}", prefix, e);
            throw new RuntimeException("列出文件失败", e);
        }
    }

    /**
     * 生成带签名的临时访问URL
     */
    @Override
    public String generatePresignedUrl(String objectName, long expireSeconds) {
        try {
            Date expiration = new Date(System.currentTimeMillis() + expireSeconds * 1000);
            URL url = ossClient.generatePresignedUrl(
                ossProperties.getBucketName(),
                objectName,
                expiration
            );
            return url.toString();
        } catch (Exception e) {
            log.error("生成签名URL失败: {}", objectName, e);
            throw new RuntimeException("生成签名URL失败", e);
        }
    }

    /**
     * 复制文件
     */
    @Override
    public void copyFile(String sourceKey, String destinationKey) {
        try {
            ossClient.copyObject(
                ossProperties.getBucketName(),
                sourceKey,
                ossProperties.getBucketName(),
                destinationKey
            );
            log.info("文件复制成功: {} -> {}", sourceKey, destinationKey);
        } catch (Exception e) {
            log.error("文件复制失败: {} -> {}", sourceKey, destinationKey, e);
            throw new RuntimeException("文件复制失败", e);
        }
    }

    /**
     * 获取文件访问URL
     */
    @Override
    public String getFileUrl(String objectName) {
        if (StringUtils.hasText(ossProperties.getCustomDomain())) {
            return ossProperties.getCustomDomain() + "/" + objectName;
        }
        String protocol = ossProperties.isUseHttps() ? "https" : "http";
        return String.format("%s://%s.%s/%s",
            protocol,
            ossProperties.getBucketName(),
            ossProperties.getEndpoint().replace("http://", "").replace("https://", ""),
            objectName
        );
    }

    /**
     * 构建对象名称（带路径前缀和UUID）
     */
    private String buildObjectName(String originalFilename) {
        String prefix = ossProperties.getPathPrefix();
        if (StringUtils.hasText(prefix) && !prefix.endsWith("/")) {
            prefix += "/";
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        String uuid = UUID.randomUUID().toString().replace("-", "");
        return prefix + uuid + extension;
    }

    /**
     * 获取OSS客户端（供高级操作使用）
     */
    @Override
    public OSS getOssClient() {
        return ossClient;
    }
}

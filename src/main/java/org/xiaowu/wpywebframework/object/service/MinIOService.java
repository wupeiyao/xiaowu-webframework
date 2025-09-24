package org.xiaowu.wpywebframework.object.service;

import java.io.InputStream;
import java.util.List;

public interface MinIOService {

    /**
     * 创建存储桶
     */
    void createBucket(String bucketName);

    /**
     * 判断存储桶是否存在
     */
    boolean bucketExists(String bucketName);

    /**
     * 删除存储桶
     */
    void removeBucket(String bucketName);

    /**
     * 初始化所有配置的存储桶
     */
    void initBuckets();

    /**
     * 上传文件到默认存储桶
     */
    String uploadFile(String fileName, InputStream inputStream, String contentType);

    /**
     * 上传文件到指定存储桶
     */
    String uploadFile(String bucketName, String fileName, InputStream inputStream, String contentType);

    /**
     * 上传文件到指定业务类型的存储桶
     */
    String uploadFileByBusinessType(String businessType, String fileName, InputStream inputStream, String contentType);

    /**
     * 下载默认存储桶的文件
     */
    InputStream downloadFile(String fileName);

    /**
     * 下载指定存储桶中的文件
     */
    InputStream downloadFile(String bucketName, String fileName);

    /**
     * 通过业务类型下载文件
     */
    InputStream downloadFileByBusinessType(String businessType, String fileName);

    /**
     * 删除默认存储桶的文件
     */
    void deleteFile(String fileName);

    /**
     * 删除指定存储桶中的文件
     */
    void deleteFile(String bucketName, String fileName);

    /**
     * 通过业务类型删除文件
     */
    void deleteFileByBusinessType(String businessType, String fileName);

    /**
     * 获取默认存储桶文件的预签名URL
     */
    String getPresignedUrl(String fileName, int expiry);

    /**
     * 获取指定存储桶中文件的预签名URL
     */
    String getPresignedUrl(String bucketName, String fileName, int expiry);

    /**
     * 通过业务类型获取预签名URL
     */
    String getPresignedUrlByBusinessType(String businessType, String fileName, int expiry);

    /**
     * 列出存储桶中的所有文件
     */
    List<String> listFiles(String bucketName);

    /**
     * 列出默认存储桶中的所有文件
     */
    List<String> listFiles();

    /**
     * 通过业务类型列出文件
     */
    List<String> listFilesByBusinessType(String businessType);

    /**
     * 获取存储桶名称（通过业务类型）
     */
    String getBucketNameByBusinessType(String businessType);
}
package org.xiaowu.wpywebframework.object.service;

import com.aliyun.oss.OSS;

/**
 * OSS服务类
 */

import com.aliyun.oss.OSS;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * OSS服务接口
 */
public interface OSSService {

    /**
     * 上传文件（字节数组）
     *
     * @param content  文件内容
     * @param fileName 文件名
     * @return 文件访问URL
     */
    String uploadFile(byte[] content, String fileName);

    /**
     * 上传文件（输入流）
     *
     * @param inputStream 文件输入流
     * @param fileName    文件名
     * @return 文件访问URL
     */
    String uploadFile(InputStream inputStream, String fileName);

    /**
     * 上传本地文件
     *
     * @param file 本地文件
     * @return 文件访问URL
     */
    String uploadFile(File file);

    /**
     * 下载文件到本地
     *
     * @param objectName    OSS对象名称
     * @param localFilePath 本地文件路径
     */
    void downloadFile(String objectName, String localFilePath);

    /**
     * 下载文件（返回字节数组）
     *
     * @param objectName OSS对象名称
     * @return 文件字节数组
     */
    byte[] downloadFileBytes(String objectName);

    /**
     * 删除文件
     *
     * @param objectName OSS对象名称
     */
    void deleteFile(String objectName);

    /**
     * 批量删除文件
     *
     * @param objectNames OSS对象名称列表
     */
    void deleteFiles(List<String> objectNames);

    /**
     * 判断文件是否存在
     *
     * @param objectName OSS对象名称
     * @return true-存在，false-不存在
     */
    boolean doesFileExist(String objectName);

    /**
     * 列出指定前缀的文件
     *
     * @param prefix 文件前缀
     * @return 文件名列表
     */
    List<String> listFiles(String prefix);

    /**
     * 生成带签名的临时访问URL
     *
     * @param objectName    OSS对象名称
     * @param expireSeconds 过期时间（秒）
     * @return 签名URL
     */
    String generatePresignedUrl(String objectName, long expireSeconds);

    /**
     * 复制文件
     *
     * @param sourceKey      源文件名
     * @param destinationKey 目标文件名
     */
    void copyFile(String sourceKey, String destinationKey);

    /**
     * 获取文件访问URL
     *
     * @param objectName OSS对象名称
     * @return 文件访问URL
     */
    String getFileUrl(String objectName);

    /**
     * 获取OSS客户端（供高级操作使用）
     *
     * @return OSS客户端实例
     */
    OSS getOssClient();
}

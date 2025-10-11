package org.xiaowu.wpywebframework.object.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云OSS配置属性
 */
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class OSSProperties {

    /**
     * 是否启用OSS
     */
    private boolean enabled = true;

    /**
     * OSS访问端点
     */
    private String endpoint;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * Bucket名称
     */
    private String bucketName;

    /**
     * 自定义域名（可选）
     */
    private String customDomain;

    /**
     * 文件访问路径前缀
     */
    private String pathPrefix = "";

    /**
     * 连接超时时间（毫秒）
     */
    private int connectionTimeout = 5000;

    /**
     * Socket超时时间（毫秒）
     */
    private int socketTimeout = 10000;

    /**
     * 最大连接数
     */
    private int maxConnections = 1024;

    /**
     * 是否使用HTTPS
     */
    private boolean useHttps = true;
}

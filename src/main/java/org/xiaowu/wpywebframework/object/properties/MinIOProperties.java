package org.xiaowu.wpywebframework.object.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.xiaowu.wpywebframework.object.bucket.SimpleBucket;

import java.util.List;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "minio")
@Setter
@Getter
public class MinIOProperties {

    /**
     * MinIO服务器地址
     */
    private String endpoint = "http://localhost:9001";

    /**
     * 访问密钥
     */
    private String accessKey;

    /**
     * 秘密密钥
     */
    private String secretKey;

    /**
     * 默认存储桶名称
     */
    private String defaultBucket = "default";

    /**
     * 存储桶配置列表
     */
    private List<SimpleBucket> buckets;

    /**
     * 存储桶映射配置 (key: 业务类型, value: 存储桶名称)
     */
    private Map<String, String> bucketMapping;

    /**
     * 连接超时时间（毫秒）
     */
    private long connectTimeout = 10000;

    /**
     * 写超时时间（毫秒）
     */
    private long writeTimeout = 60000;

    /**
     * 读超时时间（毫秒）
     */
    private long readTimeout = 10000;

    /**
     * 是否启用MinIO
     */
    private boolean enabled = true;

    /**
     * 获取指定业务类型的存储桶名称
     * @param businessType 业务类型
     * @return 存储桶名称
     */
    public String getBucketName(String businessType) {
        if (bucketMapping != null && bucketMapping.containsKey(businessType)) {
            return bucketMapping.get(businessType);
        }
        return defaultBucket;
    }

    /**
     * 获取默认存储桶名称
     */
    public String getBucketName() {
        return defaultBucket;
    }
}
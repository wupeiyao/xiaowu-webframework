package org.xiaowu.wpywebframework.object.enums;

/**
 * 存储桶枚举
 */
public enum BucketEnum {
    /**
     * 默认存储桶
     */
    DEFAULT("default"),
    
    /**
     * 文档存储桶
     */
    DOCUMENT("document"),
    
    /**
     * 图片存储桶
     */
    IMAGE("image"),
    
    /**
     * 视频存储桶
     */
    VIDEO("video"),
    
    /**
     * 音频存储桶
     */
    AUDIO("audio");
    
    private final String bucketName;
    
    BucketEnum(String bucketName) {
        this.bucketName = bucketName;
    }
    
    public String getBucketName() {
        return bucketName;
    }
    
    public static BucketEnum fromBucketName(String bucketName) {
        for (BucketEnum bucket : values()) {
            if (bucket.getBucketName().equals(bucketName)) {
                return bucket;
            }
        }
        throw new IllegalArgumentException("未找到对应的存储桶: " + bucketName);
    }
}
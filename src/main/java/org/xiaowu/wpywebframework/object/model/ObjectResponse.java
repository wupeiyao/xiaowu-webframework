package org.xiaowu.wpywebframework.object.model;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.xiaowu.wpywebframework.object.bucket.Bucket;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * @author wupy
 */
@Getter
@Setter
public class ObjectResponse implements Serializable {

    /**
     * 存储类型 local:本地 minio:Minio文件服务
     */
    @Schema(description = "存储类型 local:本地 minio:Minio文件服务")
    private String storage;
    
    /**
     * 空间名
     */
    @Schema(description = "空间名")
    private Bucket bucket;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    public String suffix;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String name;
    /**
     * 文件id
     */
    @Schema(description = "文件id")
    private String objectId;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    public String getPath() {
        String object = objectId.concat(".").concat(suffix);
        if (Objects.isNull(bucket.getFolder())) {
            return bucket.getName().concat("/").concat(object);
        }
        return bucket.getName().concat("/").concat(bucket.getFolder()).concat("/").concat(object);
    }

    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Integer available;

    /**
     * 文件头部信息
     */
    @Schema(description = "文件头部信息")
    private Map<String, String> headers;
    /**
     * 文件自定义用户元素
     */
    @Schema(description = "文件自定义用户元素")
    private Map<String, String> metadata;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

package org.xiaowu.wpywebframework.object.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wupy
 */
@Getter
@Setter
public class ObjectMetadataResponse implements Serializable {

    /**
     * 文件id
     */
    @Schema(description = "文件id")
    private String objectId;

    /**
     * 存储类型 local:本地 minio:Minio文件服务
     */
    @Schema(description = "存储类型 local:本地 minio:Minio文件服务")
    private String storage;

    /**
     * 空间名
     */
    @Schema(description = "空间名")
    private String bucket;

    /**
     * 文件类型
     */
    @Schema(description = "文件类型")
    private String suffix;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String name;

    /**
     * 文件路径
     */
    @Schema(description = "文件路径")
    private String path;


    /**
     * 文件大小
     */
    @Schema(description = "文件大小")
    private Integer size;

    /**
     * 文件头部名称
     */
    @Schema(description = "文件头部名称")
    private String headers;

    /**
     * 文件自定义用户元素
     */
    @Schema(description = "文件自定义用户元素")
    private String metadata;

    /**
     * 过期时间
     */
    @Schema(description = "过期时间")
    private boolean overdueTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建者id")
    private String creatorId;

    @Schema(description = "创建人")
    private String creatorName;
}

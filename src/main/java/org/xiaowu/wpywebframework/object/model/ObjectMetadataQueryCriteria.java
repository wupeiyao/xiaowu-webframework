package org.xiaowu.wpywebframework.object.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author wupy
 */
@Getter
@Setter
public class ObjectMetadataQueryCriteria {

    @Parameter(description = "多字段模糊搜索")
    private String blurry;

    @Parameter(description = "存储类型 local:本地 minio:Minio文件服务 oss: oss文件服务")
    private String storage;

    @Parameter(description = "创建时间")
    private List<String> createTime;
}

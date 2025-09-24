package org.xiaowu.wpywebframework.authorization.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.xiaowu.wpywebframework.core.generic.GenericEntity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public abstract class BasePermission extends GenericEntity<String> implements Serializable {


    @Override
    public String getId() {
        return super.getId();
    }

    @TableField("name")
    private String name;

    @TableField("code")
    private String code;

    @TableField("type")
    private String type;

    @TableField("parent_id")
    private Long parentId;

    @TableField("path")
    private String path;

    @TableField("component")
    private String component;

    @TableField("icon")
    private String icon;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("description")
    private String description;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

}

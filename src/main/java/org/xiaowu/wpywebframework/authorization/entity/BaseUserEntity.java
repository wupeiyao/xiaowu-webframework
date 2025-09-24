package org.xiaowu.wpywebframework.authorization.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.xiaowu.wpywebframework.core.generic.GenericEntity;


import java.io.Serial;
import java.io.Serializable;
import java.security.Permission;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public abstract class BaseUserEntity extends GenericEntity<String> implements Serializable {


    @Override
    public String getId() {
        return super.getId();
    }

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("nickname")
    private String nickname;

    @TableField("avatar")
    private String avatar;

    @TableField(value = "enabled", fill = FieldFill.INSERT)
    private Boolean enabled;

    @TableField("account_non_expired")
    private Boolean accountNonExpired;

    @TableField("account_non_locked")
    private Boolean accountNonLocked;

    @TableField("credentials_non_expired")
    private Boolean credentialsNonExpired;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

}

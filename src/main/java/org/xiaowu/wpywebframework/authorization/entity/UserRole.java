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
public abstract class UserRole extends GenericEntity<String> implements Serializable {


    @Override
    public String getId() {
        return super.getId();
    }

    @TableField("user_id")
    private String userId;

    @TableField("role_id")
    private String roleId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

}

package org.xiaowu.wpywebframework.authorization.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;
import org.xiaowu.wpywebframework.core.generic.GenericEntity;

import java.util.Date;

/**
 * @description:
 * @author: xiaowu
 * @time: 2025/10/10 22:46
 */
@Setter
@Getter
public class RolePermission extends GenericEntity<String> {

    @Override
    public String getId() {
        return super.getId();
    }

    @TableField(value = "role_id")
    private String roleId;

    @TableField(value = "permission_id")
    private String permissionId;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
}

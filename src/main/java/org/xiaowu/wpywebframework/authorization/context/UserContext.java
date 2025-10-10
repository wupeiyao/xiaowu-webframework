package org.xiaowu.wpywebframework.authorization.context;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContext implements Serializable {

    private String userId;

    private String username;

    private String nickname;

    private List<String> roles;

    private List<String> permissions;

    private String token;

    /**
     * 判断当前用户是否具备指定角色
     */
    public boolean hasRole(String roleCode) {
        return roles != null && roles.contains(roleCode);
    }

    /**
     * 判断当前用户是否具备指定权限
     */
    public boolean hasPermission(String permissionCode) {
        return permissions != null && permissions.contains(permissionCode);
    }
}

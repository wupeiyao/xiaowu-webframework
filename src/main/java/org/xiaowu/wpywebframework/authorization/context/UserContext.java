package org.xiaowu.wpywebframework.authorization.context;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserContext {
    private String userId;
    private String username;

    private List<String> role;
    private String token;
}

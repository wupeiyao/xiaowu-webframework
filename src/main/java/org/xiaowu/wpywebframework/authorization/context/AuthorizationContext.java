package org.xiaowu.wpywebframework.authorization.context;

public class AuthorizationContext {

    private static final ThreadLocal<UserContext> userContextHolder = new ThreadLocal<>();

    public static void setContext(UserContext context) {
        userContextHolder.set(context);
    }

    public static UserContext getContext() {
        return userContextHolder.get();
    }

    public static void clear() {
        userContextHolder.remove();
    }

}

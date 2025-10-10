package org.xiaowu.wpywebframework.authorization.context;


/**
 * 全局用户上下文（线程安全）
 * 用于在一次请求中保存和获取当前登录用户信息。
 *
 * 通常在 JWT 过滤器解析完 token 后注入，
 * 在 Controller 或 Service 层可通过 AuthorizationContext.getContext() 获取。
 */
public final class AuthorizationContext {

    private static final ThreadLocal<UserContext> USER_CONTEXT_HOLDER = new InheritableThreadLocal<>();

    private AuthorizationContext() {}

    /**
     * 设置当前线程用户上下文
     */
    public static void setContext(UserContext context) {
        USER_CONTEXT_HOLDER.set(context);
    }

    /**
     * 获取当前线程用户上下文
     */
    public static UserContext getContext() {
        return USER_CONTEXT_HOLDER.get();
    }

    /**
     * 清理当前线程用户上下文（防止内存泄漏）
     */
    public static void clear() {
        USER_CONTEXT_HOLDER.remove();
    }

    /**
     * 判断当前是否已登录
     */
    public static boolean isAuthenticated() {
        return getContext() != null && getContext().getUserId() != null;
    }
}

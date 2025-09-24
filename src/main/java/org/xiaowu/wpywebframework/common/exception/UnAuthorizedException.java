package org.xiaowu.wpywebframework.common.exception;

public class UnAuthorizedException extends RuntimeException {
    private final String message;

    public UnAuthorizedException() {
        this("登录信息已过期,请重新登录!");
    }

    public UnAuthorizedException(String message) {
        super(message);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

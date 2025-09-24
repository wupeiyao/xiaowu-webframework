package org.xiaowu.wpywebframework.common.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException() {
        super("无法找到资源异常");
    }
}

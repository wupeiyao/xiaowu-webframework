package org.xiaowu.wpywebframework.common.exception;

/**
 * WebSocket相关异常
 */
public class WebSocketException extends RuntimeException {
    
    private String errorCode;
    
    public WebSocketException(String message) {
        super(message);
    }
    
    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WebSocketException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public WebSocketException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
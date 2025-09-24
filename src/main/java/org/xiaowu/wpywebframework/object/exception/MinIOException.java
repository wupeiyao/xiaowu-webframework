package org.xiaowu.wpywebframework.object.exception;

public class MinIOException extends RuntimeException {
    
    public MinIOException(String message) {
        super(message);
    }
    
    public MinIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
package org.xiaowu.wpywebframework.common.exception;

import lombok.Generated;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {
    private Integer status;

    public BadRequestException(String msg) {
        super(msg);
        this.status = HttpStatus.BAD_REQUEST.value();
    }

    public BadRequestException(HttpStatus status, String msg) {
        super(msg);
        this.status = HttpStatus.BAD_REQUEST.value();
        this.status = status.value();
    }

    @Generated
    public Integer getStatus() {
        return this.status;
    }
}

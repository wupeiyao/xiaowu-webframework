package org.xiaowu.wpywebframework.common.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class CommonResponse<T> implements Serializable {
    @Schema(
        description = "消息提示"
    )
    private String message;
    @Schema(
        description = "数据内容"
    )
    private T result;
    @Schema(
        description = "业务编码"
    )
    private String code;
    @Schema(
        description = "状态编码"
    )
    private Integer status;
    @Schema(
        description = "时间戳(毫秒)"
    )
    private Long timestamp = System.currentTimeMillis();

    public CommonResponse() {
    }

    public static <T> CommonResponse<T> of(T data) {
        return of(HttpStatus.OK, "请求成功", data);
    }

    public static <T> CommonResponse<T> of(HttpStatus status, String message) {
        return of(status.value(), String.valueOf(status.value()), message, null);
    }

    public static <T> CommonResponse<T> of(HttpStatus status, String code, String message) {
        return of(status.value(), code, message, null);
    }


    public static <T> CommonResponse<T> of(HttpStatus status, T data) {
        return of(status.value(), String.valueOf(status.value()), status.getReasonPhrase(), data);
    }

    public static <T> CommonResponse<T> of(HttpStatus status, String message, T data) {
        return of(status.value(), String.valueOf(status.value()), message, data);
    }

    public static <T> CommonResponse<T> of(Integer status, String code, String message, T data) {
        CommonResponse<T> response = new CommonResponse();
        response.setMessage(message);
        response.setResult(data);
        response.setStatus(status);
        response.setCode(code);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }

    @Generated
    public String getMessage() {
        return this.message;
    }

    @Generated
    public T getResult() {
        return this.result;
    }

    @Generated
    public String getCode() {
        return this.code;
    }

    @Generated
    public Integer getStatus() {
        return this.status;
    }

    @Generated
    public Long getTimestamp() {
        return this.timestamp;
    }

    @Generated
    public void setMessage(final String message) {
        this.message = message;
    }

    @Generated
    public void setResult(final T result) {
        this.result = result;
    }

    @Generated
    public void setCode(final String code) {
        this.code = code;
    }

    @Generated
    public void setStatus(final Integer status) {
        this.status = status;
    }

    @Generated
    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }
}

package org.xiaowu.wpywebframework.core.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T>
 */
@Data

@NoArgsConstructor
public class Result<T>  implements Serializable {

    private Integer code; //编码：200成功，500和其它数字为失败
    private String msg; //错误信息
    private T data; //数据

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> success(String msg,T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 500;
        return result;
    }

    public static <T> Result<T> error(String code ,String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 500;
        return result;
    }
}

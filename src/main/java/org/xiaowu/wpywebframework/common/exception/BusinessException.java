//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.xiaowu.wpywebframework.common.exception;


import lombok.Generated;
import lombok.Getter;
import org.xiaowu.wpywebframework.common.registry.Registry;

public class BusinessException extends RuntimeException {
    protected String code;
    @Getter
    protected String message;

    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Registry registry) {
        super(registry.getMessage());
        this.code = registry.getCode();
        this.message = registry.getMessage();
    }

    public BusinessException(Registry registry, Object... args) {
        super(String.format(registry.getMessage(), args));
        this.code = registry.getCode();
        this.message = registry.getMessage();
    }

    public String getLocalizedMessage() {
        String var10000 = this.getMessage();
        return var10000 + " (" + this.code + ")";
    }

    public final String toString() {
        String var10000 = this.getClass().getSimpleName();
        return var10000 + ": " + this.getLocalizedMessage();
    }

    @Generated
    public String getCode() {
        return this.code;
    }
}

package org.xiaowu.wpywebframework.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class KeyValue {

    private String key;

    private Object value;

    private String name;

    private String unit;

    public static KeyValue of(String key, Object value, String name, String unit) {
        KeyValue keyValue = new KeyValue();
        keyValue.setKey(key);
        keyValue.setValue(Objects.requireNonNullElse(value, "0"));
        keyValue.setName(name);
        keyValue.setUnit(unit);
        return keyValue;
    }

    public static KeyValue of(String key, Object value, String name) {
        return of(key, value, name, null);
    }
}

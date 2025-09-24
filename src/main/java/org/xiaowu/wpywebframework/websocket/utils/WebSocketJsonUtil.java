package org.xiaowu.wpywebframework.websocket.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebSocket JSON工具类
 */
public class WebSocketJsonUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketJsonUtil.class);
    private static final ObjectMapper objectMapper;
    
    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 对象转JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("对象转JSON失败", e);
            return "{}";
        }
    }
    
    /**
     * JSON字符串转对象
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            logger.error("JSON转对象失败: json={}, class={}", json, clazz.getName(), e);
            return null;
        }
    }
    
    /**
     * 检查是否为有效JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
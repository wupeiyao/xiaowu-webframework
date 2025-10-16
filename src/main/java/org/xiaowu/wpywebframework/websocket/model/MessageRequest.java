package org.xiaowu.wpywebframework.websocket.model;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author wupy
 */
@Getter
@Setter
public class MessageRequest implements Serializable {

    /**
     * 消息id
     */
    @Parameter(description = "消息id")
    private String id;

    /**
     * 消息类型
     */
    @Parameter(description = "消息类型")
    private Type type;
    /**
     * 主题
     */
    @Parameter(description = "主题")
    private String topic;

    /**
     * 参数
     */
    @Parameter(description = "参数")
    private Map<String, Object> parameter;

    /**
     * 消息类型
     */
    @Generated
    public enum Type {
        /**
         * 发布
         */
        pub,
        /**
         * 订阅
         */
        sub,
        /**
         * 退订
         */
        unsub,
        /**
         * ping
         */
        ping,
        /**
         * pong
         */
        pong,

    }
}

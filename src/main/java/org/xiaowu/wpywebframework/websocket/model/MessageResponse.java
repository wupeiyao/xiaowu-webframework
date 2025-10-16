package org.xiaowu.wpywebframework.websocket.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author wupy
 */
@Getter
@Setter
@Accessors(chain = true)
public class MessageResponse implements Serializable {

    @Schema(description = "请求id")
    private String requestId;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "数据内容")
    private Object payload;

    @Schema(description = "消息类型")
    private Type type;

    @Schema(description = "消息提示")
    private String message;

    /**
     * 认证失败
     *
     * @return
     */
    public static MessageResponse authError() {
        return new MessageResponse().setType(Type.authError).setMessage("认证失败");
    }

    /**
     * 错误
     *
     * @param requestId
     * @param message
     * @return
     */
    public static MessageResponse error(String requestId, String message) {
        return new MessageResponse().setType(Type.error).setRequestId(requestId).setMessage(message);
    }


    /**
     * 错误
     *
     * @param requestId
     * @param topic
     * @param message
     * @return
     */
    public static MessageResponse error(String requestId, String topic, String message) {
        return new MessageResponse().setType(Type.error).setRequestId(requestId).setTopic(topic).setMessage(message);
    }

    /**
     * 错误
     *
     * @param requestId
     * @param topic
     * @param e
     * @return
     */
    public static MessageResponse error(String requestId, String topic, Throwable e) {
        return new MessageResponse().setType(Type.error).setRequestId(requestId).setTopic(topic).setMessage(e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage());
    }

    /**
     * 成功
     *
     * @param requestId
     * @param topic
     * @param payload
     * @return
     */
    public static MessageResponse of(String requestId, String topic, Object payload) {
        return new MessageResponse().setType(Type.result).setRequestId(requestId).setTopic(topic).setPayload(payload);
    }

    /**
     * 完成
     *
     * @param requestId
     * @return
     */
    public static MessageResponse complete(String requestId) {
        return new MessageResponse().setType(Type.complete).setRequestId(requestId);
    }

    /**
     * 发送pong消息,主要是对ping信息的反馈
     *
     * @param requestId
     * @return
     */
    public static MessageResponse pong(String requestId) {
        return new MessageResponse().setType(Type.pong).setRequestId(requestId);
    }

    public enum Type {
        /**
         * 认证失败
         */
        authError,
        /**
         * 结果
         */
        result,
        /**
         * 错误
         */
        error,
        /**
         * 完成
         */
        complete,
        /**
         * ping
         */
        ping,
        /**
         * pong
         */
        pong
    }
}

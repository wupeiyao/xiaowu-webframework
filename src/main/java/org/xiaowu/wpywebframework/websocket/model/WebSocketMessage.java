package org.xiaowu.wpywebframework.websocket.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * WebSocket消息实体
 */

public class WebSocketMessage {

    // Getters and Setters
    /**
     * 消息类型
     */
    private String type;
    
    /**
     * 消息内容
     */
    private Object data;
    
    /**
     * 发送者ID
     */
    private String senderId;
    
    /**
     * 接收者ID(可选，用于私聊)
     */
    private String receiverId;
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 扩展字段
     */
    private Object extra;
    
    public WebSocketMessage() {
        this.timestamp = LocalDateTime.now();
        this.messageId = java.util.UUID.randomUUID().toString();
    }
    
    public WebSocketMessage(String type, Object data) {
        this();
        this.type = type;
        this.data = data;
    }
    
    public WebSocketMessage(String type, Object data, String senderId) {
        this(type, data);
        this.senderId = senderId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}

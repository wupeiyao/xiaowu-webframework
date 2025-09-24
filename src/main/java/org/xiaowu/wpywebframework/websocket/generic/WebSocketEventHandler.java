package org.xiaowu.wpywebframework.websocket.generic;


import jakarta.websocket.Session;
import org.xiaowu.wpywebframework.websocket.model.WebSocketMessage;


/**
 * WebSocket事件处理接口
 */
public interface WebSocketEventHandler {
    
    /**
     * 连接建立时调用
     */
    void onOpen(Session session, String userId);
    
    /**
     * 连接关闭时调用
     */
    void onClose(Session session, String userId);
    
    /**
     * 收到消息时调用
     */
    void onMessage(Session session, WebSocketMessage message, String userId);
    
    /**
     * 发生错误时调用
     */
    void onError(Session session, Throwable error, String userId);
}
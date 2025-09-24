package org.xiaowu.wpywebframework.websocket.generic;

import jakarta.websocket.Session;
import org.xiaowu.wpywebframework.websocket.model.WebSocketMessage;

/**
 * WebSocket消息拦截器接口
 */
public interface WebSocketMessageInterceptor {
    
    /**
     * 消息发送前拦截
     * @return true-继续发送，false-中断发送
     */
    boolean beforeSend(Session session, WebSocketMessage message);
    
    /**
     * 消息发送后拦截
     */
    void afterSend(Session session, WebSocketMessage message, boolean success);
    
    /**
     * 消息接收前拦截
     * @return true-继续处理，false-中断处理
     */
    boolean beforeReceive(Session session, WebSocketMessage message);
    
    /**
     * 消息接收后拦截
     */
    void afterReceive(Session session, WebSocketMessage message);
}

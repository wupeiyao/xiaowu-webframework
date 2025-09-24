package org.xiaowu.wpywebframework.websocket.utils;

import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xiaowu.wpywebframework.websocket.model.WebSocketMessage;

import java.util.Set;

/**
 * WebSocket消息发送工具类
 */
public class WebSocketMessageUtil {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketMessageUtil.class);

    private WebSocketMessageUtil() {}

    /**
     * 发送消息给指定用户
     */
    public static boolean sendToUser(String userId, WebSocketMessage message) {
        Session session = WebSocketSessionManager.getSessionByUserId(userId);
        if (session != null && session.isOpen()) {
            return sendMessage(session, message);
        }
        logger.warn("用户不在线或Session无效: userId={}", userId);
        return false;
    }

    /**
     * 发送消息给指定用户(简化版本)
     */
    public static boolean sendToUser(String userId, String type, Object data) {
        return sendToUser(userId, new WebSocketMessage(type, data));
    }

    /**
     * 广播消息给所有用户
     */
    public static int broadcastToAll(WebSocketMessage message) {
        Set<Session> sessions = WebSocketSessionManager.getAllActiveSessions();
        int successCount = 0;

        for (Session session : sessions) {
            if (sendMessage(session, message)) {
                successCount++;
            }
        }

        logger.info("广播消息完成: 总用户数={}, 成功发送数={}", sessions.size(), successCount);
        return successCount;
    }

    /**
     * 广播消息给所有用户(简化版本)
     */
    public static int broadcastToAll(String type, Object data) {
        return broadcastToAll(new WebSocketMessage(type, data));
    }

    /**
     * 发送消息给指定用户列表
     */
    public static int sendToUsers(Set<String> userIds, WebSocketMessage message) {
        int successCount = 0;

        for (String userId : userIds) {
            if (sendToUser(userId, message)) {
                successCount++;
            }
        }

        logger.info("批量发送消息完成: 目标用户数={}, 成功发送数={}", userIds.size(), successCount);
        return successCount;
    }

    /**
     * 发送消息给指定用户列表(简化版本)
     */
    public static int sendToUsers(Set<String> userIds, String type, Object data) {
        return sendToUsers(userIds, new WebSocketMessage(type, data));
    }

    /**
     * 发送系统通知
     */
    public static int sendSystemNotification(String content) {
        WebSocketMessage message = new WebSocketMessage("SYSTEM_NOTIFICATION", content);
        message.setSenderId("SYSTEM");
        return broadcastToAll(message);
    }

    /**
     * 基础消息发送方法
     */
    private static boolean sendMessage(Session session, WebSocketMessage message) {
        try {
            String json = WebSocketJsonUtil.toJson(message);
            session.getBasicRemote().sendText(json);
            return true;
        } catch (Exception e) {
            logger.error("发送WebSocket消息失败: sessionId={}", session.getId(), e);
            return false;
        }
    }

    /**
     * 异步发送消息
     */
    public static void sendMessageAsync(Session session, WebSocketMessage message) {
        try {
            String json = WebSocketJsonUtil.toJson(message);
            session.getAsyncRemote().sendText(json);
        } catch (Exception e) {
            logger.error("异步发送WebSocket消息失败: sessionId={}", session.getId(), e);
        }
    }
}

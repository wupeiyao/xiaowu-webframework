package org.xiaowu.wpywebframework.websocket.utils;

import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * WebSocket会话管理器
 */
public class WebSocketSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionManager.class);

    /**
     * 用户ID -> Session映射
     */
    private static final Map<String, Session> userSessions = new ConcurrentHashMap<>();

    /**
     * Session -> 用户ID映射
     */
    private static final Map<Session, String> sessionUsers = new ConcurrentHashMap<>();

    /**
     * 所有活跃的Session
     */
    private static final Set<Session> allSessions = new CopyOnWriteArraySet<>();

    private WebSocketSessionManager() {}

    /**
     * 添加会话
     */
    public static void addSession(String userId, Session session) {
        // 如果用户已有连接，先移除旧连接
        removeSessionByUserId(userId);

        userSessions.put(userId, session);
        sessionUsers.put(session, userId);
        allSessions.add(session);

        logger.info("添加WebSocket会话: userId={}, sessionId={}, 当前在线用户数={}",
                   userId, session.getId(), userSessions.size());
    }

    /**
     * 移除会话
     */
    public static void removeSession(Session session) {
        String userId = sessionUsers.remove(session);
        if (userId != null) {
            userSessions.remove(userId);
        }
        allSessions.remove(session);

        logger.info("移除WebSocket会话: userId={}, sessionId={}, 当前在线用户数={}",
                   userId, session.getId(), userSessions.size());
    }

    /**
     * 根据用户ID移除会话
     */
    public static void removeSessionByUserId(String userId) {
        Session session = userSessions.get(userId);
        if (session != null) {
            removeSession(session);
            try {
                session.close();
            } catch (Exception e) {
                logger.warn("关闭WebSocket连接失败: userId={}", userId, e);
            }
        }
    }

    /**
     * 获取用户的Session
     */
    public static Session getSessionByUserId(String userId) {
        return userSessions.get(userId);
    }

    /**
     * 获取Session对应的用户ID
     */
    public static String getUserIdBySession(Session session) {
        return sessionUsers.get(session);
    }

    /**
     * 检查用户是否在线
     */
    public static boolean isUserOnline(String userId) {
        Session session = userSessions.get(userId);
        return session != null && session.isOpen();
    }

    /**
     * 获取所有在线用户ID
     */
    public static Set<String> getOnlineUserIds() {
        return userSessions.entrySet().stream()
                .filter(entry -> entry.getValue().isOpen())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * 获取所有活跃Session
     */
    public static Set<Session> getAllActiveSessions() {
        return allSessions.stream()
                .filter(Session::isOpen)
                .collect(Collectors.toSet());
    }

    /**
     * 获取在线用户数量
     */
    public static int getOnlineUserCount() {
        return (int) userSessions.values().stream()
                .filter(Session::isOpen)
                .count();
    }

    /**
     * 清理无效连接
     */
    public static void cleanInactiveSessions() {
        Set<Session> inactiveSessions = allSessions.stream()
                .filter(session -> !session.isOpen())
                .collect(Collectors.toSet());

        for (Session session : inactiveSessions) {
            removeSession(session);
        }

        if (!inactiveSessions.isEmpty()) {
            logger.info("清理无效WebSocket连接数量: {}", inactiveSessions.size());
        }
    }
}

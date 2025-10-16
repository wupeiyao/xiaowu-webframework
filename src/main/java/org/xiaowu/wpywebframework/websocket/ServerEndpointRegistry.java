package org.xiaowu.wpywebframework.websocket;


import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketSession;
import org.xiaowu.wpywebframework.authorization.context.AuthorizationContext;
import org.xiaowu.wpywebframework.authorization.context.UserContext;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author wupy
 */
public class ServerEndpointRegistry {

    /**
     * concurrent包的线程安全，用来存放每个客户端对应的endpoint对象。
     */
    private static final Map<String, Set<WebSocketSession>> endpointMap = new ConcurrentHashMap<>();

    /**
     * 主要是用于记录session和userid的关系
     */
    private static final Map<WebSocketSession, String> endpointToUserIdMap = new ConcurrentHashMap<>();

    /**
     * 主要是用于记录在线数
     */
    private static final AtomicLong counter = new AtomicLong(0);

    /**
     * 获取用户Token信息
     *
     * @param session 会话
     * @return 用户token
     */
    public static UserContext getUserToken(WebSocketSession session) {
        return AuthorizationContext.getContext();
    }

    /**
     * 注册
     *
     * @param userId  用户id
     * @param session 会话
     */
    public static void register(String userId, WebSocketSession session) {
        synchronized (session) {
            //记录userid和session的关系
            endpointMap.computeIfAbsent(userId, uid -> new HashSet<>()).add(session);
            //记录session和userid的关系
            endpointToUserIdMap.put(session, userId);
            //增加在线人数
            counter.incrementAndGet();
        }
    }

    /**
     * 移除
     *
     * @param session 会话
     */
    public static void remove(WebSocketSession session) {
        synchronized (session) {
            //移除Session和用户ID map中的这个通道，并得到用户ID
            Optional.ofNullable(endpointToUserIdMap.remove(session))
                    .flatMap(userId -> {
                        //减少在线人数
                        counter.decrementAndGet();
                        // 获取注册表中用户id的会话信息
                        return Optional.ofNullable(endpointMap.get(userId))
                                .map(sessions -> {
                                    // 将参数指定通道从用户会话中移除
                                    sessions.remove(session);
                                    // 若移除后，用户会话为空，则将这个会话从注册表移除
                                    endpointMap.compute(userId, (uid, si) -> (Objects.isNull(si) || CollectionUtils.isEmpty(si)) ? null : si);
                                    // 返回移除参数指定通道后的会话信息
                                    return sessions;
                                });
                    });
        }
    }


    /**
     * 获取在线人数
     *
     * @return 在线人数
     */
    public static Long online() {
        return counter.get();
    }

    /**
     * 根据userId获取Session
     *
     * @param userId 用户id
     * @return 会话
     */
    public static Set<WebSocketSession> getSessions(Long userId) {
        return endpointMap.get(userId);
    }

    /**
     * 根据session获取userId
     *
     * @param session 会话
     * @return 用户id
     */
    public static String getUserId(WebSocketSession session) {
        return endpointToUserIdMap.get(session);
    }
}

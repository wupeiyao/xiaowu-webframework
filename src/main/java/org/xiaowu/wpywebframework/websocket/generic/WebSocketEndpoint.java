package org.xiaowu.wpywebframework.websocket.generic;

import jakarta.websocket.CloseReason;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

public interface WebSocketEndpoint {
    void onOpen(Session session, EndpointConfig config);
    void onClose(Session session, CloseReason closeReason);
    void onMessage(String message, Session session);
    void onError(Session session, Throwable error);
    String extractUserId(Session session);
}

package org.xiaowu.wpywebframework.websocket;


import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.xiaowu.wpywebframework.authorization.context.AuthorizationContext;
import org.xiaowu.wpywebframework.authorization.context.UserContext;

import java.util.Map;
import java.util.Objects;

/**
 * @author wupy
 * 这个类的作用就是在连接成功前和成功后增加一些额外的功能
 */
public class ServerEndpointHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private final Logger logger = LoggerFactory.getLogger(ServerEndpointHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler handler, @NonNull Map<String, Object> attributes) throws Exception {
        UserContext userToken = AuthorizationContext.getContext();
        if ( Objects.isNull(userToken)) {
            if (logger.isDebugEnabled()) {
                logger.debug("websocket closed: user token is null");
            }
            return false;
        }
        attributes.put("token", userToken);
        return super.beforeHandshake(request, response, handler, attributes);
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}

package org.xiaowu.wpywebframework.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.xiaowu.wpywebframework.authorization.context.UserContext;
import org.xiaowu.wpywebframework.websocket.model.MessageRequest;
import org.xiaowu.wpywebframework.websocket.model.MessageResponse;
import org.xiaowu.wpywebframework.websocket.publisher.PublishBroker;
import org.xiaowu.wpywebframework.websocket.subscription.SubscriptionBroker;
import reactor.core.Disposable;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wupy
 */
public class ServerEndpointHandler extends TextWebSocketHandler {

    private final Logger logger = LoggerFactory.getLogger(ServerEndpointHandler.class);

    private final Map<WebSocketSession, Map<String, Disposable>> subscribes = new ConcurrentHashMap<>();

    /**
     * 订阅管理器
     */
    @Autowired
    private SubscriptionBroker subscriber;

    @Autowired
    private PublishBroker publisher;

    /**
     * 连接成功时候，会触发UI上onopen方法
     */
    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        UserContext userToken = ServerEndpointRegistry.getUserToken(session);
        subscribes.computeIfAbsent(session, key -> new ConcurrentHashMap<>(16));
        if (!Objects.isNull(userToken)) {
            ServerEndpointRegistry.register(userToken.getUserId(), session);
        }
    }

    /**
     * 连接异常时
     *
     * @param session   会话
     * @param exception 异常信息
     * @throws Exception 异常
     */
    @Override
    public void handleTransportError(WebSocketSession session, @NonNull Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        logger.debug("websocket connection closed......");
        Optional.ofNullable(subscribes.remove(session))
                .ifPresent(disposables -> disposables.values().forEach(Disposable::dispose));

        ServerEndpointRegistry.remove(session);
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) throws Exception {
        logger.debug("websocket connection closed......");
        Optional.ofNullable(subscribes.remove(session))
                .ifPresent(disposables -> disposables.values().forEach(Disposable::dispose));
        ServerEndpointRegistry.remove(session);
    }


    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        try {
            Map<String, Disposable> disposables = subscribes.get(session);
            MessageRequest request = JSON.parseObject(message.getPayload(), MessageRequest.class);
            //ping主要是为了保持长链接
            if (MessageRequest.Type.ping.equals(request.getType())) {
                session.sendMessage(new PongMessage(ByteBuffer.wrap(JSONObject.toJSONBytes(MessageResponse.pong(request.getId())))));
                return;
            }
            if (StringUtils.isEmpty(request.getId())) {
                session.sendMessage(new TextMessage(JSONObject.toJSONBytes(MessageResponse.error(request.getType().name(), "请求Id不能为空"))));
                return;
            }
            //客户端发布消息给其他客户端
            if (MessageRequest.Type.pub.equals(request.getType())) {
                if (StringUtils.isEmpty(request.getTopic())) {
                    session.sendMessage(new TextMessage(JSONObject.toJSONBytes(MessageResponse.error(request.getType().name(), "发布的主题不能为空"))));
                    return;
                }
                publisher.publish(session, request);
                return;
            }
            //订阅消息
            if (MessageRequest.Type.sub.equals(request.getType())) {
                if (StringUtils.isEmpty(request.getTopic())) {
                    session.sendMessage(new TextMessage(JSONObject.toJSONBytes(MessageResponse.error(request.getType().name(), "订阅的主题不能为空"))));
                    return;
                }
                //重复订阅
                Disposable old = disposables.get(request.getId());
                if (old != null && !old.isDisposed()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("重复订阅Topic:{}", request.getTopic());
                    }
                    return;
                }
                Disposable disposable = subscriber.subscribe(session, request)
                        .doOnError(error -> {
                            TextMessage msg = new TextMessage(JSONObject.toJSONBytes(MessageResponse.error("illegal_subscription", request.getTopic(), error.getMessage())));
                            this.sendMessage(session, msg);
                        })
                        .onErrorContinue((err, v) -> logger.error(err.getMessage(), err))
                        .subscribe(msg -> this.sendMessage(session, msg));
                //查询是否解除订阅 true 代表 已经解除订阅
                if (!disposable.isDisposed()) {
                    disposables.put(request.getId(), disposable);
                }
                return;
            }
            //退订消息
            if (MessageRequest.Type.unsub.equals(request.getType())) {
                Optional.ofNullable(disposables.remove(request.getId()))
                        .ifPresent(Disposable::dispose);
                return;
            }
            this.sendMessage(session, new TextMessage(JSONObject.toJSONBytes(MessageResponse.error(request.getId(), request.getTopic(), "不支持的类型:" + request.getType()))));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            this.sendMessage(session, new TextMessage(JSONObject.toJSONBytes(MessageResponse.error("illegal_argument", "消息格式错误"))));
        }
    }

    private void sendMessage(WebSocketSession session, TextMessage message) {
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    session.sendMessage(message);
                    return;
                }
            }
            logger.warn("Session[{}] is closed! Message:{}", session.getId(), message.getPayload());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

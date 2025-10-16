package org.xiaowu.wpywebframework.websocket.subscription;

import com.alibaba.fastjson.JSONObject;

import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.xiaowu.wpywebframework.websocket.model.MessageRequest;
import org.xiaowu.wpywebframework.websocket.model.MessageResponse;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wupy
 */
public class SubscriptionBroker implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(SubscriptionBroker.class);

    /**
     * topic主题匹配器
     */
    private final AntPathMatcher matcher = new AntPathMatcher();

    private final List<SubscriptionProvider<?>> providers = new CopyOnWriteArrayList<>();

    /**
     * 订阅
     *
     * @param session 会话
     * @param request 请求信息
     * @return 消息流
     */
    public Flux<TextMessage> subscribe(WebSocketSession session, MessageRequest request) {
        return Flux.<TextMessage>defer(() -> {
            for (SubscriptionProvider provider : providers) {
                if (this.isMatcher(provider.getTopic(), request.getTopic())) {
                    return provider.subscribe(session, request).map(payload -> {
                        if (payload instanceof TextMessage) {
                            return payload;
                        }
                        if (payload instanceof MessageResponse) {
                            return new TextMessage(JSONObject.toJSONBytes(payload));
                        }
                        return new TextMessage(JSONObject.toJSONBytes(new MessageResponse()
                                .setRequestId(request.getId())
                                .setType(MessageResponse.Type.result)
                                .setTopic(request.getTopic())
                                .setPayload(payload)));
                    });
                }
            }
            logger.error("不支持的topic:{}", request.getTopic());
            return Flux.empty();
        });
    }

    /**
     * 是否匹配
     *
     * @param patterns 匹配
     * @param topic    主题
     * @return 是否匹配
     */
    private boolean isMatcher(String[] patterns, String topic) {
        //如果订阅了则只发送订阅的信息
        for (String pattern : patterns) {
            if (matcher.match(pattern, topic)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 注册
     *
     * @param provider 提供者
     */
    public void register(SubscriptionProvider<?> provider) {
        providers.add(provider);
        providers.sort(Comparator.comparingInt(SubscriptionProvider::getOrder));
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof SubscriptionProvider) {
            register(((SubscriptionProvider<?>) bean));
        }
        return bean;
    }
}

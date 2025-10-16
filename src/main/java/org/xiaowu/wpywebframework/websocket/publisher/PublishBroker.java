package org.xiaowu.wpywebframework.websocket.publisher;


import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.WebSocketSession;
import org.xiaowu.wpywebframework.websocket.model.MessageRequest;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wupy
 **/
public class PublishBroker implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(PublishBroker.class);

    /**
     * topic主题匹配器
     */
    private final AntPathMatcher matcher = new AntPathMatcher();

    private final List<PublishProvider> providers = new CopyOnWriteArrayList<>();

    /**
     * 发送
     *
     * @param session 会话
     * @param request 请求信息
     */
    public void publish(WebSocketSession session, MessageRequest request) {
        for (PublishProvider provider : providers) {
            if (this.isMatcher(provider.getTopic(), request.getTopic())) {
                provider.publish(session, request);
                return;
            }
        }
        logger.error("不支持的topic:{}", request.getTopic());
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
    public void register(PublishProvider provider) {
        providers.add(provider);
        providers.sort(Comparator.comparingInt(PublishProvider::getOrder));
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof PublishProvider) {
            register(((PublishProvider) bean));
        }
        return bean;
    }
}

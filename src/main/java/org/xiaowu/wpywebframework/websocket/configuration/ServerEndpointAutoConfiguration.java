package org.xiaowu.wpywebframework.websocket.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.xiaowu.wpywebframework.websocket.ServerEndpointHandler;
import org.xiaowu.wpywebframework.websocket.ServerEndpointHandshakeInterceptor;
import org.xiaowu.wpywebframework.websocket.publisher.PublishBroker;
import org.xiaowu.wpywebframework.websocket.subscription.SubscriptionBroker;

/**
 * @author wupy
 */
@EnableWebSocket
@Configuration
public class ServerEndpointAutoConfiguration implements WebSocketConfigurer {

    @Value("${server.servlet.path-match:}")
    private String pathMath;


    @Bean
    @ConditionalOnMissingBean(SubscriptionBroker.class)
    public SubscriptionBroker subscriptionBroker() {
        return new SubscriptionBroker();
    }

    @Bean
    @ConditionalOnMissingBean(PublishBroker.class)
    public PublishBroker publishBroker() {
        return new PublishBroker();
    }

    @Bean
    @ConditionalOnMissingBean(ServerEndpointHandler.class)
    public ServerEndpointHandler serverEndpointHandler() {
        return new ServerEndpointHandler();
    }

    @Bean
    public ServerEndpointHandshakeInterceptor serverEndpointHandshakeInterceptor() {
        return new ServerEndpointHandshakeInterceptor();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(serverEndpointHandler(), StringUtils.hasText(pathMath) ? pathMath + "/messaging/**" : "/messaging/**")
                // 添加拦截器
                .addInterceptors(serverEndpointHandshakeInterceptor())
                // 解决跨域问题 [4]
                .setAllowedOrigins("*");
    }
}

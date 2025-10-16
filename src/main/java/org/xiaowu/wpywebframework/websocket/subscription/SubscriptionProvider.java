package org.xiaowu.wpywebframework.websocket.subscription;


import org.springframework.core.Ordered;
import org.springframework.web.socket.WebSocketSession;
import org.xiaowu.wpywebframework.websocket.model.MessageRequest;
import reactor.core.publisher.Flux;

/**
 * @author wupy
 */
public interface SubscriptionProvider<T> extends Ordered {
    /**
     * 消息提供者id
     *
     * @return 提供者id
     */
    String id();

    /**
     * 消息提供者名称
     *
     * @return 提供者名称
     */
    String name();

    /**
     * 订阅主题,主题以/分割,如: /device/TS-01/09012/message 支持通配符 /device/**
     *
     * @return 订阅主题
     */
    String[] getTopic();

    /**
     * 订阅
     *
     * @param session 会话
     * @param request 请求信息
     * @return 消息流
     */
    Flux<T> subscribe(WebSocketSession session, MessageRequest request);

    /**
     * 排序
     *
     * @return 排序序号
     */
    @Override
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}

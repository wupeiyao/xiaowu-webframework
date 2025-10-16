package org.xiaowu.wpywebframework.websocket.publisher;


import org.springframework.core.Ordered;
import org.springframework.web.socket.WebSocketSession;
import org.xiaowu.wpywebframework.websocket.model.MessageRequest;

/**
 * @author wupy
 **/
public interface PublishProvider extends Ordered {

    /**
     * 消息发送id
     *
     * @return 提供者id
     */
    String id();

    /**
     * 消息发送名称
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
     * 发送
     *
     * @param session 会话
     * @param request 请求信息
     */
    void publish(WebSocketSession session, MessageRequest request);

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

package org.xiaowu.wpywebframework.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@Slf4j
public class EventPublisherService {

    private final ApplicationEventPublisher publisher;

    public EventPublisherService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(Object event) {
        log.debug("发布事件: {}", event.getClass().getSimpleName());
        publisher.publishEvent(event);
    }


}

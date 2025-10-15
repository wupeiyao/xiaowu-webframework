package org.xiaowu.wpywebframework.events.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiaowu.wpywebframework.events.EventPublisherService;
import org.xiaowu.wpywebframework.events.EventSubscriberRegistrar;

@Configuration
public class EventAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EventPublisherService eventPublisherService(ApplicationEventPublisher publisher) {
        return new EventPublisherService(publisher);
    }

    @Bean
    @ConditionalOnMissingBean
    public EventSubscriberRegistrar eventSubscriberRegistrar() {
        return new EventSubscriberRegistrar();
    }
}

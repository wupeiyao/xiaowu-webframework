package org.xiaowu.wpywebframework.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseEvent {
    private String id;
    private String source;
    private Object data;
}

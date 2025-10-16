package org.xiaowu.wpywebframework.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CountData {

    private String name = "";

    private List<KeyValue> keyValues;

}

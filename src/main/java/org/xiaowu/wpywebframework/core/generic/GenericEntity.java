package org.xiaowu.wpywebframework.core.generic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;


public abstract class GenericEntity<ID> implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}

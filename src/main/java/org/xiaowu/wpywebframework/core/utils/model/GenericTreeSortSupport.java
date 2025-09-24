package org.xiaowu.wpywebframework.core.utils.model;

import java.util.List;

public interface GenericTreeSortSupport<PK> {
    PK getId();

    void setId(PK id);

    void setName(String name);

    String getName();

    String getPath();

    void setPath(String path);

    PK getParentId();

    void setParentId(PK parentId);

    void setPn(String pn);

    String getPn();

    Integer getLevel();

    void setLevel(Integer level);

    Integer getSortIndex();

    void setSortIndex(Integer sortIndex);

    <T extends GenericTreeSortSupport<PK>> List<T> getChildren();
}

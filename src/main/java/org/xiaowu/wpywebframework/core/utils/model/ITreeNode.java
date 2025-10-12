package org.xiaowu.wpywebframework.core.utils.model;

import java.util.List;

public interface ITreeNode<T extends ITreeNode<T>> {
    String getId();

    String getParentId();

    List<T> getChildren();

    void setChildren(List<T> children);

    String getTreePath();
}

package org.xiaowu.wpywebframework.core.generic;

import java.util.List;

public interface ITreeNode {
    String getId();
    String getParentId();
    List<? extends ITreeNode> getChildren();
    void setChildren(List<? extends ITreeNode> children);
}

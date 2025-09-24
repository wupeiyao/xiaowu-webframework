package org.xiaowu.wpywebframework.core.utils.model;

import java.util.List;

public interface ITreeNode {
    Object getId();
    Object getParentId();
    List<ITreeNode> getChildren();
    String getTreePath();
}

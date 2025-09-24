package org.xiaowu.wpywebframework.core.utils;


import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.xiaowu.wpywebframework.core.utils.model.ITreeNode;
import org.xiaowu.wpywebframework.core.utils.model.SystemConstants;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * TreeNodeUtil 工具类，用于将扁平化的数据列表转换为树形结构
 */
public class TreeNodeUtil {

    private static final Logger log = LoggerFactory.getLogger(TreeNodeUtil.class);

    // 常量定义
    public static final String PARENT_NAME = "parent";
    public static final String CHILDREN_NAME = "children";
    public static final List<Object> IDS = Collections.singletonList(0L);

    /**
     * 构建树形结构
     */
    public static <T extends ITreeNode> List<T> buildTree(List<T> dataList) {
        return buildTree(dataList, IDS, Function.identity(), (item) -> true);
    }

    public static <T extends ITreeNode> List<T> buildTree(List<T> dataList, Function<T, T> map) {
        return buildTree(dataList, IDS, map, (item) -> true);
    }

    public static <T extends ITreeNode> List<T> buildTree(List<T> dataList, Function<T, T> map, Predicate<T> filter) {
        return buildTree(dataList, IDS, map, filter);
    }

    public static <T extends ITreeNode> List<T> buildTree(List<T> dataList, List<Object> ids) {
        return buildTree(dataList, ids, Function.identity(), (item) -> true);
    }

    public static <T extends ITreeNode> List<T> buildTree(List<T> dataList, List<Object> ids, Function<T, T> map) {
        return buildTree(dataList, ids, map, (item) -> true);
    }

    /**
     * 生成树形结构，支持过滤、映射以及子节点添加
     *
     * @param dataList 数据集合
     * @param ids 父元素的 ID 集合
     * @param map 数据映射函数
     * @param filter 数据过滤函数
     * @param <T> 节点类型，必须实现 ITreeNode 接口
     * @return 树形结构数据
     */
    public static <T extends ITreeNode> List<T> buildTree(List<T> dataList, List<Object> ids, Function<T, T> map, Predicate<T> filter) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }

        // 1. 将数据分为父子结构
        Map<String, List<T>> nodeMap = dataList.stream()
                .filter(filter)
                .collect(Collectors.groupingBy(item -> ids.contains(item.getParentId()) ? PARENT_NAME : CHILDREN_NAME));

        List<T> parent = nodeMap.getOrDefault(PARENT_NAME, Collections.emptyList());
        List<T> children = nodeMap.getOrDefault(CHILDREN_NAME, Collections.emptyList());

        // 1.1 如果未分出或过滤了父元素则返回子元素
        if (parent.isEmpty()) {
            return children;
        }

        // 2. 使用有序集合存储下一轮父元素的 ids
        List<Object> nextIds = new ArrayList<>(dataList.size());

        // 3. 遍历父元素并修改父元素内容
        List<T> collectParent = parent.stream().map(map).collect(Collectors.toList());
        for (T parentItem : collectParent) {
            if (nextIds.size() == children.size()) {
                break;
            }

            children.stream()
                    .filter(childrenItem -> parentItem.getId().equals(childrenItem.getParentId()))
                    .forEach(childrenItem -> {
                        nextIds.add(childrenItem.getParentId());
                        try {
                            parentItem.getChildren().add(childrenItem);
                        } catch (Exception e) {
                            log.warn("TreeNodeUtil 发生错误, 传入参数中 children 不能为 null，解决方法: " +
                                    "在 map 或 filter 中初始化 children");
                        }
                    });
        }

        // 递归构建子树
        buildTree(children, nextIds, map, filter);
        return parent;
    }

    /**
     * 生成路径 treePath
     * @param currentId 当前节点的 id
     * @param getById 获取节点的函数
     * @param <T> 节点类型
     * @return 生成的路径
     */
    public static <T extends ITreeNode> String generateTreePath(Serializable currentId, Function<Serializable, T> getById) {
        StringBuilder treePath = new StringBuilder();
        if (SystemConstants.ROOT_NODE_ID.equals(currentId)) {
            treePath.append(currentId);
        } else {
            T byId = getById.apply(currentId);
            if (!ObjectUtils.isEmpty(byId)) {
                treePath.append(byId.getTreePath()).append(",").append(byId.getId());
            }
        }
        return treePath.toString();
    }
}


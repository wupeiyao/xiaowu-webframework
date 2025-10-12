package org.xiaowu.wpywebframework.core.utils;


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
    public static final List<String> DEFAULT_ROOT_IDS = Collections.singletonList("0");

    /**
     * 构建树形结构
     */
    public static <T extends ITreeNode<T>> List<T> buildTree(List<T> dataList) {
        return buildTree(dataList, DEFAULT_ROOT_IDS, Function.identity(), (item) -> true);
    }

    public static <T extends ITreeNode<T>> List<T> buildTree(List<T> dataList, Function<T, T> map) {
        return buildTree(dataList, DEFAULT_ROOT_IDS, map, (item) -> true);
    }

    public static <T extends ITreeNode<T>> List<T> buildTree(List<T> dataList, Function<T, T> map, Predicate<T> filter) {
        return buildTree(dataList, DEFAULT_ROOT_IDS, map, filter);
    }

    public static <T extends ITreeNode<T>> List<T> buildTree(List<T> dataList, List<String> rootIds) {
        return buildTree(dataList, rootIds, Function.identity(), (item) -> true);
    }

    public static <T extends ITreeNode<T>> List<T> buildTree(List<T> dataList, List<String> rootIds, Function<T, T> map) {
        return buildTree(dataList, rootIds, map, (item) -> true);
    }

    /**
     * 生成树形结构,支持过滤、映射以及子节点添加
     *
     * @param dataList 数据集合
     * @param rootIds 根节点的父ID集合
     * @param map 数据映射函数
     * @param filter 数据过滤函数
     * @param <T> 节点类型,必须实现 ITreeNode 接口
     * @return 树形结构数据
     */
    public static <T extends ITreeNode<T>> List<T> buildTree(
            List<T> dataList,
            List<String> rootIds,
            Function<T, T> map,
            Predicate<T> filter) {

        if (CollectionUtils.isEmpty(rootIds) || CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }
        Map<String, List<T>> nodeMap = dataList.stream()
                .filter(filter)
                .collect(Collectors.groupingBy(
                        item -> rootIds.contains(item.getParentId()) ? PARENT_NAME : CHILDREN_NAME
                ));

        List<T> parents = nodeMap.getOrDefault(PARENT_NAME, Collections.emptyList());
        List<T> children = nodeMap.getOrDefault(CHILDREN_NAME, Collections.emptyList());

        if (parents.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> nextRootIds = new ArrayList<>();

        List<T> result = parents.stream().map(map).collect(Collectors.toList());
        for (T parent : result) {
            if (nextRootIds.size() == children.size()) {
                break;
            }

            children.stream()
                    .filter(child -> parent.getId().equals(child.getParentId()))
                    .forEach(child -> {
                        if (!nextRootIds.contains(child.getParentId())) {
                            nextRootIds.add(child.getParentId());
                        }
                        try {
                            parent.getChildren().add(child);
                        } catch (Exception e) {
                            log.error("TreeNodeUtil 发生错误, children 不能为 null", e);
                            throw new RuntimeException("构建树失败: children 未初始化");
                        }
                    });
        }

        if (!nextRootIds.isEmpty()) {
            buildTree(children, nextRootIds, map, filter);
        }

        return result;
    }

    /**
     * 生成路径 treePath
     * @param currentId 当前节点的 id
     * @param getById 获取节点的函数
     * @param <T> 节点类型
     * @return 生成的路径,格式: 1,2,3
     */
    public static <T extends ITreeNode<T>> String generateTreePath(
            String currentId,
            Function<String, T> getById) {

        if (StringUtils.isBlank(currentId)) {
            return "";
        }

        if ("0".equals(currentId)) {
            return currentId;
        }

        T node = getById.apply(currentId);
        if (node == null) {
            return currentId;
        }

        String parentPath = node.getTreePath();
        if (StringUtils.isBlank(parentPath)) {
            return node.getId();
        }

        return parentPath + "," + node.getId();
    }
}

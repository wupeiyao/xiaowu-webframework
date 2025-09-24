package org.xiaowu.wpywebframework.core.utils;

import org.springframework.util.CollectionUtils;
import org.xiaowu.wpywebframework.common.model.Optionals;
import org.xiaowu.wpywebframework.core.utils.model.GenericTreeSortSupport;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import org.springframework.util.StringUtils;

public class TreeUtils {
    public TreeUtils() {
    }

    public static <T extends GenericTreeSortSupport<?>> List<T> buildTree(List<T> nodes, BiConsumer<T, List<T>> consumer) {
        List<T> roots = new ArrayList();
        if (!Objects.isNull(nodes) && !nodes.isEmpty()) {
            for(T node : nodes) {
                if (Objects.isNull(node.getParentId()) || !StringUtils.hasText(node.getParentId().toString())) {
                    roots.add(node);
                }
            }

            if (CollectionUtils.isEmpty(roots)) {
                return roots;
            } else {
                for(T root : roots) {
                    subordinate(root, nodes, consumer);
                }

                roots.sort(Comparator.comparingInt((node) -> (Integer) Optionals.ofNullable(node.getSortIndex()).orElse(0)));
                return roots;
            }
        } else {
            return roots;
        }
    }

    private static <T extends GenericTreeSortSupport<?>> void subordinate(T parent, List<T> nodes, BiConsumer<T, List<T>> consumer) {
        List<T> children = new ArrayList();

        for(T node : nodes) {
            if (!Objects.isNull(node.getParentId()) && StringUtils.hasText(node.getParentId().toString()) && node.getParentId().equals(parent.getId())) {
                subordinate(node, nodes, consumer);
                children.add(node);
            }
        }

        if (consumer != null && !CollectionUtils.isEmpty(children)) {
            consumer.accept(parent, children);
        }

        children.sort(Comparator.comparingInt((nodex) -> (Integer)Optionals.ofNullable(nodex.getSortIndex()).orElse(0)));
    }
}

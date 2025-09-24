package org.xiaowu.wpywebframework.core.generic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.xiaowu.wpywebframework.common.model.PageQueryRequest;
import org.xiaowu.wpywebframework.common.model.PageRequest;
import org.xiaowu.wpywebframework.core.utils.Result;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 通用Controller基类，集成接口和实现
 *
 * @param <T> 实体类型
 * @param <V> VO类型
 * @param <ID> 主键类型
 */
public interface GenericController<T, V, ID extends Serializable> {

    GenericService<T, V, ID> getService();

    default Set<String> getAllowedSortFields() {
        return Collections.emptySet();
    }
    default int getMaxBatchSize() {
        return 1000;
    }
    default int getMaxResultSize() {
        return 10000;
    }
    default QueryWrapper<T> buildQueryWrapper(V vo) {
        return new QueryWrapper<>();
    }

    default QueryWrapper<T> buildPageQueryWrapper(PageRequest pageRequest) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageRequest.getOrderBy())) {
            Set<String> allowedFields = getAllowedSortFields();
            if (!allowedFields.isEmpty() && !allowedFields.contains(pageRequest.getOrderBy())) {
                throw new IllegalArgumentException("不允许的排序字段: " + pageRequest.getOrderBy());
            }
            if (!isValidFieldName(pageRequest.getOrderBy())) {
                throw new IllegalArgumentException("非法的排序字段名: " + pageRequest.getOrderBy());
            }
            if ("desc".equalsIgnoreCase(pageRequest.getOrderDirection())) {
                queryWrapper.orderByDesc(pageRequest.getOrderBy());
            } else {
                queryWrapper.orderByAsc(pageRequest.getOrderBy());
            }
        }
        return queryWrapper;
    }

    default boolean isValidFieldName(String fieldName) {
        return fieldName != null && fieldName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    @PatchMapping
    @Operation(summary = "保存数据", description = "如果传入了id,并且对应数据存在,则尝试覆盖,不存在则新增.")
    default Result<Integer> save(@RequestBody V vo) {
        try {
            int result = this.getService().save(vo);
            return result > 0 ? Result.success("新增成功", result) : Result.error("新增失败");
        } catch (Exception e) {
            return Result.error("新增失败，请稍后重试");
        }
    }

    @PostMapping("/batch")
    @Operation(summary = "批量新增数据")
    default Result<Integer> saveBatch(@RequestBody List<V> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return Result.error("新增数据不能为空");
        }
        if (voList.size() > getMaxBatchSize()) {
            return Result.error("批量操作数量不能超过 " + getMaxBatchSize() + " 条");
        }
        try {
            int result = this.getService().saveBatch(voList);
            return result > 0 ? Result.success("批量新增成功", result) : Result.error("批量新增失败");
        } catch (Exception e) {
            return Result.error("批量新增失败，请稍后重试");
        }
    }

    @PatchMapping("/saveOrUpdate")
    @Operation(summary = "保存或更新单条数据")
    default Result<Integer> saveOrUpdate(@RequestBody V vo) {
        try {
            int result = this.getService().saveOrUpdate(vo);
            return result > 0 ? Result.success("保存成功", result) : Result.error("保存失败");
        } catch (Exception e) {
            return Result.error("保存失败，请稍后重试");
        }
    }

    @PostMapping("/saveOrUpdateBatch")
    @Operation(summary = "批量保存或更新数据")
    default Result<Integer> saveOrUpdateBatch(@RequestBody List<V> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return Result.error("保存数据不能为空");
        }
        if (voList.size() > getMaxBatchSize()) {
            return Result.error("批量操作数量不能超过 " + getMaxBatchSize() + " 条");
        }
        try {
            int result = this.getService().saveOrUpdateBatch(voList);
            return result > 0 ? Result.success("批量保存成功", result) : Result.error("批量保存失败");
        } catch (Exception e) {
            return Result.error("批量保存失败，请稍后重试");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据ID删除数据")
    default Result<Integer> removeById(@PathVariable("id") ID id) {
        if (id == null) {
            return Result.error("主键不能为空");
        }
        try {
            int result = this.getService().removeById(id);
            return result > 0 ? Result.success("删除成功", result) : Result.error("删除失败，数据不存在");
        } catch (Exception e) {
            return Result.error("删除失败，请稍后重试");
        }
    }

    @DeleteMapping("/batch")
    @Operation(summary = "根据ID列表批量删除数据")
    default Result<Integer> removeByIds(@RequestBody List<ID> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Result.error("主键列表不能为空");
        }
        if (idList.size() > getMaxBatchSize()) {
            return Result.error("批量删除数量不能超过 " + getMaxBatchSize() + " 条");
        }
        try {
            int result = this.getService().removeByIds(idList);
            return result > 0 ? Result.success("批量删除成功", result) : Result.error("批量删除失败");
        } catch (Exception e) {
            return Result.error("批量删除失败，请稍后重试");
        }
    }

    @PutMapping
    @Operation(summary = "根据ID更新数据")
    default Result<Integer> updateById(@RequestBody V vo) {
        try {
            int result = this.getService().updateByVo(vo);
            return result > 0 ? Result.success("更新成功", result) : Result.error("更新失败，数据不存在");
        } catch (Exception e) {
            return Result.error("更新失败，请稍后重试");
        }
    }

    @PutMapping("/batch")
    @Operation(summary = "批量更新数据")
    default Result<Integer> updateBatchById(@RequestBody List<V> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return Result.error("更新数据不能为空");
        }
        if (voList.size() > getMaxBatchSize()) {
            return Result.error("批量更新数量不能超过 " + getMaxBatchSize() + " 条");
        }
        try {
            int result = this.getService().updateBatchByVo(voList);
            return result > 0 ? Result.success("批量更新成功", result) : Result.error("批量更新失败");
        } catch (Exception e) {
            return Result.error("批量更新失败，请稍后重试");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询数据")
    default Result<V> getById(@PathVariable("id") ID id) {
        if (id == null) {
            return Result.error("主键不能为空");
        }
        try {
            V vo = this.getService().getById(id);
            return vo != null ? Result.success(vo) : Result.error("数据不存在");
        } catch (Exception e) {
            return Result.error("查询失败，请稍后重试");
        }
    }

    @PostMapping("/listByIds")
    @Operation(summary = "根据ID列表批量查询数据")
    default Result<List<V>> listByIds(@RequestBody List<ID> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Result.error("主键列表不能为空");
        }

        if (idList.size() > getMaxBatchSize()) {
            return Result.error("查询ID数量不能超过 " + getMaxBatchSize() + " 个");
        }

        try {
            List<V> list = this.getService().listByIds(idList);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("批量查询失败，请稍后重试");
        }
    }

    @GetMapping("/list")
    @Operation(summary = "查询数据列表（带数量限制）")
    default Result<List<V>> list(@RequestParam(defaultValue = "1000") int limit) {
        if (limit > getMaxResultSize()) {
            return Result.error("查询结果数量不能超过 " + getMaxResultSize() + " 条，请使用分页查询");
        }
        try {
            List<V> list = this.getService().listWithLimit(limit);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("查询失败，请稍后重试");
        }
    }

    @PostMapping("/page")
    @Operation(summary = "分页查询")
    default Result<IPage<V>> page(@RequestBody PageRequest pageRequest) {
        if (pageRequest.getSize() > 1000) {
            return Result.error("每页数量不能超过1000条");
        }
        try {
            IPage<T> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
            QueryWrapper<T> queryWrapper = buildPageQueryWrapper(pageRequest);
            IPage<V> result = this.getService().page(page, queryWrapper);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("分页查询失败，请稍后重试");
        }
    }

    /**
     * 修复参数绑定问题，使用组合对象
     */
    @PostMapping("/pageByCondition")
    @Operation(summary = "条件分页查询")
    default Result<IPage<V>> pageByCondition(@RequestBody PageQueryRequest<V> request) {
        if (request.getPageRequest().getSize() > 1000) {
            return Result.error("每页数量不能超过1000条");
        }

        try {
            IPage<T> page = new Page<>(request.getPageRequest().getCurrent(), request.getPageRequest().getSize());
            QueryWrapper<T> queryWrapper = buildQueryWrapper(request.getCondition());
            QueryWrapper<T> pageQueryWrapper = buildPageQueryWrapper(request.getPageRequest());
            queryWrapper.getExpression().getOrderBy().addAll(pageQueryWrapper.getExpression().getOrderBy());
            IPage<V> result = this.getService().page(page, queryWrapper);
            return Result.success(result);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("条件分页查询失败，请稍后重试");
        }
    }

    @GetMapping("/count")
    @Operation(summary = "统计总数")
    default Result<Long> count() {
        try {
            long count = this.getService().count();
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("统计失败，请稍后重试");
        }
    }

    @GetMapping("/exists/{id}")
    @Operation(summary = "根据ID判断是否存在")
    default Result<Boolean> existsById(@PathVariable("id") ID id) {
        if (id == null) {
            return Result.error("主键不能为空");
        }
        try {
            boolean exists = this.getService().existsById(id);
            return Result.success(exists);
        } catch (Exception e) {
            return Result.error("判断失败，请稍后重试");
        }
    }

    @PostMapping("/listByCondition")
    @Operation(summary = "条件查询（带数量限制）")
    default Result<List<V>> listByCondition(@RequestBody V vo, @RequestParam(defaultValue = "1000") int limit) {
        if (limit > getMaxResultSize()) {
            return Result.error("查询结果数量不能超过 " + getMaxResultSize() + " 条，请使用分页查询");
        }

        try {
            QueryWrapper<T> queryWrapper = buildQueryWrapper(vo);
            List<V> list = this.getService().listWithLimit(queryWrapper, limit);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error("条件查询失败，请稍后重试");
        }
    }

}



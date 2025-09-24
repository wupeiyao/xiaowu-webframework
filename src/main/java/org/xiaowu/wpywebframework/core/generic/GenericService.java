package org.xiaowu.wpywebframework.core.generic;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.xiaowu.wpywebframework.core.utils.Y;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

public interface GenericService<T, V, ID extends Serializable> {

    /**
     * Returns the repository instance for entity persistence.
     */
    JmapRepository<?, T> repository();

    default Class<T> entityClass() {
        Type type = getClass().getGenericInterfaces()[0];
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return (Class<T>) parameterizedType.getActualTypeArguments()[0];
    }

    default Class<V> voClass() {
        Type type = getClass().getGenericInterfaces()[0];
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return (Class<V>) parameterizedType.getActualTypeArguments()[1];
    }

    default String resolvePrimaryKeyName() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass());
        if (tableInfo == null) {
            throw new IllegalStateException("无法获取实体类 " + entityClass().getName() + " 的 TableInfo，请确认已配置 MyBatis-Plus");
        }
        return tableInfo.getKeyProperty();
    }

    default Object getIdValue(T entity) {
        try {
            Field field = entityClass().getDeclaredField(resolvePrimaryKeyName());
            field.setAccessible(true);
            return field.get(entity);
        } catch (Exception e) {
            throw new IllegalStateException("无法获取主键值: " + resolvePrimaryKeyName(), e);
        }
    }

    default T voToEntity(V vo) {
        return vo == null ? null : Y.bean.copy(vo, entityClass());
    }

    default V entityToVo(T entity) {
        return entity == null ? null : Y.bean.copy(entity, voClass());
    }

    default List<T> voListToEntityList(List<V> voList) {
        if (CollectionUtils.isEmpty(voList)) {
            return Collections.emptyList();
        }
        return voList.stream().map(this::voToEntity).collect(Collectors.toList());
    }

    default List<V> entityListToVoList(List<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::entityToVo).collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    default int save(V vo) {
        if (vo == null) return 0;
        T entity = voToEntity(vo);
        return this.repository().insert(entity);
    }

    /**
     * 批量保存
     */
    @Transactional(rollbackFor = Exception.class)
    default int saveBatch(List<V> voList) {
        List<T> entityList = voListToEntityList(voList);
        return insertBatchOptimized(entityList);
    }

    /**
     * 批量插入实现
     */
    @Transactional(rollbackFor = Exception.class)
    default int insertBatchOptimized(List<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) return 0;
        int batchSize = 500;
        int totalCount = 0;
        for (int i = 0; i < entityList.size(); i += batchSize) {
            List<T> batch = entityList.subList(i, Math.min(i + batchSize, entityList.size()));
            totalCount += this.repository().insertBatch(batch);
        }
        return totalCount;
    }

    @Transactional(rollbackFor = Exception.class)
    default int insertBatch(List<T> entityList) {
        return insertBatchOptimized(entityList);
    }

    @Transactional(rollbackFor = Exception.class)
    default int saveOrUpdate(V vo) {
        if (vo == null) return 0;
        T entity = voToEntity(vo);
        Object idValue = getIdValue(entity);
        if (idValue == null) {
            return this.repository().insert(entity);
        } else {
            return this.repository().updateById(entity);
        }
    }

    /**
     * 批量保存或更新
     */
    @Transactional(rollbackFor = Exception.class)
    default int saveOrUpdateBatch(List<V> voList) {
        List<T> insertList = new ArrayList<>();
        List<T> updateList = new ArrayList<>();
        for (V vo : voList) {
            T entity = voToEntity(vo);
            Object idValue = getIdValue(entity);
            if (idValue == null) {
                insertList.add(entity);
            } else {
                updateList.add(entity);
            }
        }
        int count = 0;
        if (!insertList.isEmpty()) {
            count += insertBatchOptimized(insertList);
        }
        if (!updateList.isEmpty()) {
            count += updateBatchOptimized(updateList);
        }

        return count;
    }

    /**
     * 批量更新
     */
    @Transactional(rollbackFor = Exception.class)
    default int updateBatchOptimized(List<T> entityList) {
        if (CollectionUtils.isEmpty(entityList)) return 0;
        int batchSize = 500;
        int totalCount = 0;
        for (int i = 0; i < entityList.size(); i += batchSize) {
            List<T> batch = entityList.subList(i, Math.min(i + batchSize, entityList.size()));
                totalCount += this.repository().updateBatch(batch);
        }
        return totalCount;
    }

    @Transactional(rollbackFor = Exception.class)
    default int removeById(ID id) {
        return id != null ? this.repository().deleteById(id) : 0;
    }

    @Transactional(rollbackFor = Exception.class)
    default int removeByIds(List<ID> idList) {
        if (CollectionUtils.isEmpty(idList)) return 0;
        int batchSize = 1000;
        int totalCount = 0;
        for (int i = 0; i < idList.size(); i += batchSize) {
            List<ID> batch = idList.subList(i, Math.min(i + batchSize, idList.size()));
            totalCount += this.repository().deleteBatchIds(batch);
        }
        return totalCount;
    }

    @Transactional(rollbackFor = Exception.class)
    default int remove(QueryWrapper<T> queryWrapper) {
        return this.repository().delete(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    default int updateByVo(V vo) {
        if (vo == null) return 0;
        T entity = voToEntity(vo);
        return this.repository().updateById(entity);
    }

    /**
     * 批量更新
     */
    @Transactional(rollbackFor = Exception.class)
    default int updateBatchByVo(List<V> voList) {
        if (CollectionUtils.isEmpty(voList)) return 0;
        List<T> entityList = voListToEntityList(voList);
        return updateBatchOptimized(entityList);
    }

    @Transactional(rollbackFor = Exception.class)
    default int update(V vo, QueryWrapper<T> queryWrapper) {
        if (vo == null) return 0;
        T entity = voToEntity(vo);
        return this.repository().update(entity, queryWrapper);
    }

    default V getById(ID id) {
        if (id == null) return null;
        return entityToVo(this.repository().selectById(id));
    }

    default List<V> listByIds(List<ID> idList) {
        if (CollectionUtils.isEmpty(idList)) return Collections.emptyList();
        int batchSize = 1000;
        List<T> allEntities = new ArrayList<>();
        for (int i = 0; i < idList.size(); i += batchSize) {
            List<ID> batch = idList.subList(i, Math.min(i + batchSize, idList.size()));
            List<T> batchEntities = this.repository().selectBatchIds(batch);
            allEntities.addAll(batchEntities);
        }
        return entityListToVoList(allEntities);
    }

    default List<V> listWithLimit(int limit) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("LIMIT " + Math.min(limit, 10000));
        return entityListToVoList(this.repository().selectList(queryWrapper));
    }

    default List<V> list() {
        return listWithLimit(1000);
    }

    /**
     * 带限制的条件查询
     */
    default List<V> listWithLimit(QueryWrapper<T> queryWrapper, int limit) {
        QueryWrapper<T> limitedWrapper = queryWrapper.clone();
        limitedWrapper.last("LIMIT " + Math.min(limit, 10000));
        return entityListToVoList(this.repository().selectList(limitedWrapper));
    }

    default List<V> list(QueryWrapper<T> queryWrapper) {
        return entityListToVoList(this.repository().selectList(queryWrapper));
    }

    default V getOne(QueryWrapper<T> queryWrapper) {
        return entityToVo(this.repository().selectOne(queryWrapper));
    }

    default IPage<V> page(IPage<T> page) {
        return convertPage(this.repository().selectPage(page, null));
    }

    default IPage<V> page(IPage<T> page, QueryWrapper<T> queryWrapper) {
        return convertPage(this.repository().selectPage(page, queryWrapper));
    }

    default IPage<V> convertPage(IPage<T> entityPage) {
        Page<V> voPage = new Page<>();
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        voPage.setTotal(entityPage.getTotal());
        voPage.setRecords(entityListToVoList(entityPage.getRecords()));
        return voPage;
    }

    default long count() {
        return this.repository().selectCount(null);
    }

    default long count(QueryWrapper<T> queryWrapper) {
        return this.repository().selectCount(queryWrapper);
    }

    default boolean exists(QueryWrapper<T> queryWrapper) {
        return count(queryWrapper) > 0;
    }

    default boolean existsById(ID id) {
        return id != null && this.repository().selectById(id) != null;
    }
}

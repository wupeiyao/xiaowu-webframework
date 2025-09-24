package org.xiaowu.wpywebframework.core.generic;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface GenericRepository<T> {

    int insert(T entity);

    int insertBatch(List<T> entityList);

    int updateById(T entity);

    int update(T entity, Wrapper<T> updateWrapper);

    int deleteById(Serializable id);

    int deleteBatchIds(Collection<? extends Serializable> idList);

    int delete(Wrapper<T> queryWrapper);

    T selectById(Serializable id);

    List<T> selectBatchIds(Collection<? extends Serializable> idList);

    List<T> selectList(Wrapper<T> queryWrapper);

    T selectOne(Wrapper<T> queryWrapper);

    IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper);

    long selectCount(Wrapper<T> queryWrapper);

    int updateBatch(List<T> entityList);

}

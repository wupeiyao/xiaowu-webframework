package org.xiaowu.wpywebframework.core.generic;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Cache;

import java.io.Serializable;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * author wupy
 */
public abstract class JmapRepository<M extends GenericBaseMapper<T>, T>
        implements GenericRepository<T> {

    protected M mapper;
    private Class<T> eclz;

    private final Cache<Serializable, T> cache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .recordStats()
            .build();

    private final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

    public JmapRepository(M mapper) {
        this.mapper = mapper;
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper 不能为空");
        }
    }

    public JmapRepository() {

    }

    public void setMapper(M mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper 不能为空");
        }
        this.mapper = mapper;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        if (eclz != null) {
            return eclz;
        }
        try {
            eclz = (Class<T>) ReflectionKit.getSuperClassGenericType(this.getClass(), JmapRepository.class, 1);
            if (eclz == null) {
                throw new IllegalStateException("无法获取实体类型，请检查泛型参数");
            }
            return eclz;
        } catch (Exception e) {
            throw new IllegalStateException("获取实体类型失败", e);
        }
    }

    private void putCache(Serializable id, T entity) {
        if (id != null && entity != null) {
            cacheLock.writeLock().lock();
            try {
                cache.put(id, entity);
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
    }

    private T getCache(Serializable id) {
        if (id == null) {
            return null;
        }

        cacheLock.readLock().lock();
        try {
            return cache.getIfPresent(id);
        } finally {
            cacheLock.readLock().unlock();
        }
    }

    private void evictCache(Serializable id) {
        if (id != null) {
            cacheLock.writeLock().lock();
            try {
                cache.invalidate(id);
            } finally {
                cacheLock.writeLock().unlock();
            }
        }
    }

    private void clearCache() {
        cacheLock.writeLock().lock();
        try {
            cache.invalidateAll();
        } finally {
            cacheLock.writeLock().unlock();
        }
    }

    @Override
    public int insert(T entity) {
        int result = mapper.insert(entity);
        if (result > 0) {
            clearCache();
        }
        return result;
    }

    @Override
    public int insertBatch(List<T> entityList) {
            int count = 0;
            for (T entity : entityList) {
                if (entity != null) {
                    count += mapper.insert(entity);
                }
            }
            if (count > 0) {
                clearCache();
            }
            return count;
    }

    @Override
    public int updateBatch(List<T> entityList) {
            int count = 0;
            for (T entity : entityList) {
                if (entity != null) {
                    count += mapper.updateById(entity);
                }
            }
            if (count > 0) {
                clearCache();
            }
            return count;
    }

    @Override
    public int updateById(T entity) {
            int result = mapper.updateById(entity);
            if (result > 0) {
                clearCache();
            }
            return result;
    }

    @Override
    public int update(T entity, Wrapper<T> updateWrapper) {
            int result = mapper.update(entity, updateWrapper);
            if (result > 0) {
                clearCache();
            }
            return result;
    }

    @Override
    public int deleteById(Serializable id) {
            int result = mapper.deleteById(id);
            if (result > 0) {
                evictCache(id);
            }
            return result;
    }

    @Override
    public int deleteBatchIds(Collection<? extends Serializable> idList) {
            int result = mapper.deleteBatchIds(idList);
            if (result > 0) {
                for (Serializable id : idList) {
                    evictCache(id);
                }
            }
            return result;
    }

    @Override
    public int delete(Wrapper<T> queryWrapper) {
            int result = mapper.delete(queryWrapper);
            if (result > 0) {
                clearCache();
            }
            return result;
    }

    @Override
    public T selectById(Serializable id) {
        T cached = getCache(id);
        if (cached != null) {
            return cached;
        }
            T entity = mapper.selectById(id);
            if (entity != null) {
                putCache(id, entity);
            }
            return entity;
    }

    @Override
    public List<T> selectBatchIds(Collection<? extends Serializable> idList) {
        return mapper.selectBatchIds(idList);
    }

    @Override
    public List<T> selectList(Wrapper<T> queryWrapper) {
            return mapper.selectList(queryWrapper);
    }

    @Override
    public T selectOne(Wrapper<T> queryWrapper) {
            return mapper.selectOne(queryWrapper);
    }

    @Override
    public IPage<T> selectPage(IPage<T> page, Wrapper<T> queryWrapper) {
        return mapper.selectPage(page, queryWrapper);
    }

    @Override
    public long selectCount(Wrapper<T> queryWrapper) {
        return mapper.selectCount(queryWrapper);
    }
}

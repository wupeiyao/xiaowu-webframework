package org.xiaowu.wpywebframework.core.utils;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cglib.beans.BeanMap;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class FastBeanCopier {
    private final Logger logger = LoggerFactory.getLogger(FastBeanCopier.class);
    private static FastBeanCopier copier;

    public FastBeanCopier() {
    }

    public static FastBeanCopier getCopier() {
        if (copier != null) {
            return copier;
        } else {
            Class var0 = FastBeanCopier.class;
            synchronized(FastBeanCopier.class) {
                if (copier != null) {
                    return copier;
                } else {
                    copier = new FastBeanCopier();
                    return copier;
                }
            }
        }
    }

    public <C, T> List<T> copy(List<C> list, Class<T> target) {
        return list == null ? null : (List)list.stream().map((source) -> {
            return this.copy(source, target);
        }).collect(Collectors.toList());
    }

    public <C, T> T copy(C source, Class<T> target) {
        try {
            if (Objects.isNull(source)) {
                return null;
            } else {
                T orig = target.getDeclaredConstructor().newInstance();
                BeanUtils.copyProperties(source, orig);
                return orig;
            }
        } catch (Exception var4) {
            Exception e = var4;
            this.logger.error(e.getMessage(), e);
            return null;
        }
    }

    public <T> Map<String, Object> toMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            Iterator var4 = beanMap.keySet().iterator();

            while(var4.hasNext()) {
                Object key = var4.next();
                map.put(key.toString(), beanMap.get(key));
            }
        }

        return map;
    }

    public <T> List<Map<String, Object>> toMap(List<T> list) {
        return (List)list.stream().map((t) -> {
            Map<String, Object> map = new HashMap(8);
            BeanMap beanMap = BeanMap.create(t);
            Iterator var3 = beanMap.keySet().iterator();

            while(var3.hasNext()) {
                Object key = var3.next();
                map.put(key.toString(), beanMap.get(key));
            }

            return map;
        }).collect(Collectors.toList());
    }

    public <T> T toBean(Map<String, Object> map, Class<T> type) {
        try {
            if (map != null && !map.isEmpty()) {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                T obj = type.getDeclaredConstructor().newInstance();
                PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                PropertyDescriptor[] var6 = propertyDescriptors;
                int var7 = propertyDescriptors.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    PropertyDescriptor descriptor = var6[var8];
                    String propertyName = descriptor.getName();
                    if (map.containsKey(propertyName)) {
                        try {
                            Object value = map.get(propertyName);
                            Object[] args = new Object[]{value};
                            Method write = descriptor.getWriteMethod();
                            if (value != null && write != null) {
                                write.invoke(obj, args);
                            }
                        } catch (Exception var14) {
                            Exception e = var14;
                            this.logger.warn("Map对象转化为{}属性{}赋值失败:{},无需处理!", new Object[]{type.getSimpleName(), propertyName, e.getMessage()});
                        }
                    }
                }

                return obj;
            } else {
                return null;
            }
        } catch (Exception var15) {
            Exception e = var15;
            this.logger.error(e.getMessage(), e);
            return null;
        }
    }

    public <T> List<T> toBean(List<Map<String, Object>> list, Class<T> type) {
        return list == null ? null : (List)list.stream().map((map) -> {
            return this.toBean(map, type);
        }).collect(Collectors.toList());
    }
}

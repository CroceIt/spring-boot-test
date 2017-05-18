package com.springboottest.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**

 */

/**
 * redis工具类
 */
@Component("redisUtils")
public class RedisUtils {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 普通key-value 设置值
     *
     * @param key
     * @param value
     * @param second
     */
    public void set(Object key, Object value, Long second) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, value, second, TimeUnit.SECONDS);
    }

    /**
     * 普通key-value 获取值
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T get(Object key, Class<T> clazz) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Object o = valueOperations.get(key);
        return (T) o;
    }
}

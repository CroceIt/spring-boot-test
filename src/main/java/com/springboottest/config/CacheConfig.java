package com.springboottest.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.lang.reflect.Method;

/**

 */

/**
 * 使用Redis作为缓存配置的Java配置类
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {

    /**
     * 配置缓存键值的生成器 (因为Redis是键值对存储的数据库, 所以要配置一个键值缓存的生成器)
     *
     * @return
     */
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            // 配置生成器
            @Override
            public Object generate(Object target, Method method, Object... objects) {
                StringBuilder sb = new StringBuilder();
                // 规则1: 拼接class名称
                sb.append(target.getClass().getName());
                // 规则2: 拼接方法名称
                sb.append(method.getName());
                for (Object obj : objects) {
                    // 规则3: 拼接参数值
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    /**
     * 配置缓存时间, 单位秒
     * @param redisTemplate
     * @return
     */
    @Bean
    public CacheManager cacheManager(@Autowired @Qualifier("cacheRedisTemplate") StringRedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        // 设定缓存过期时间, 单位秒
        rcm.setDefaultExpiration(60);
        return rcm;
    }

    /**
     * 配置用于Redis缓存的template
     * factory已经可以注入, 但是这里会提示找不到bean 强迫症所以使用了禁用提示
     * 需要注意的地方就是, 如果将来直接使用@Autowired注解 注入RedisTemplate, 会注入成为这个redisTemplate, 这样就不能实现序列化, 只能使用<String, String>
     * Bean注解的方法名称就是此Bean在Spring容器的中Bean名称, 也就是Bean注解里的name属性的值
     *
     * @param factory
     * @return
     */
    @SuppressWarnings("all")
    @Bean
    public StringRedisTemplate cacheRedisTemplate(RedisConnectionFactory factory) {
        StringRedisTemplate template = new StringRedisTemplate(factory);
        /**
         * 配置一个序列器, 将对象序列化为字符串存储, 和将对象反序列化为对象
         */
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}

package com.aethercoder.foundation.config.redis;

import com.aethercoder.foundation.contants.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.DefaultRedisCachePrefix;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hepengfei on 24/12/2017.
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Autowired
    private JedisConnectionFactory connectionFactory;

    @Bean
    public ShardedJedisPool jedis() {
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>();
        jedisShardInfoList.add(connectionFactory.getShardInfo());
        return new ShardedJedisPool(connectionFactory.getPoolConfig(), jedisShardInfoList);
    }


    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName());
                sb.append(method.getName());
                for (Object obj : params) {
                    sb.append(obj.toString());
                }
                return sb.toString();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager rcm = new RedisCacheManager(redisTemplate);
        rcm.setUsePrefix(true);
        rcm.setCachePrefix(new DefaultRedisCachePrefix(CommonConstants.REDIS_CACHE_PREFIX));
        Map<String, Long> expire = new HashMap<>();
        expire.put(CommonConstants.REDIS_CACHE_NAME_ACCOUNT, 3600L); //expire 1 hour
        rcm.setExpires(expire);
        //设置缓存过期时间
//        rcm.setDefaultExpiration(7200);//秒
        return rcm;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate< String, Object > template =  new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setConnectionFactory(connectionFactory);
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));

        RedisSerializer<Object> serializer = new JdkSerializationRedisSerializer(getClass().getClassLoader());
        template.setValueSerializer(serializer);

        return template;
    }
}

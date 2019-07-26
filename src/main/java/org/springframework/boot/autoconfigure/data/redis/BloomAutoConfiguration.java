package org.springframework.boot.autoconfigure.data.redis;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.BloomOperations;
import org.springframework.data.redis.core.DefaultBloomOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;

/**
 * @Auther: Jackie
 * @Date: 2019-07-26 11:58
 * @Description:
 */
@Configuration
@ConditionalOnMissingClass("io.lettuce.core.RedisClient")
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
@ConditionalOnBean(StringRedisTemplate.class)
public class BloomAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BloomOperations.class)
    public BloomOperations bloomOperations(StringRedisTemplate redisTemplate) {
        return new DefaultBloomOperations(redisTemplate);
    }
}

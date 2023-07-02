package com.nowcoder.mycommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    // RedisConnectionFactory also is a bean, when you use it, spring will automatically load it.
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Set serialization mode of the key
        template.setKeySerializer(RedisSerializer.string());
        // Set serialization mode of the value
        template.setValueSerializer(RedisSerializer.json());
        // Set serialization mode of the key of hash
        template.setValueSerializer(RedisSerializer.string());
        // Set serialization mode of the value of hash
        template.setValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}

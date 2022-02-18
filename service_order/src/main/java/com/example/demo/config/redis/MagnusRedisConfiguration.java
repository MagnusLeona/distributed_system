package com.example.demo.config.redis;

import io.lettuce.core.ReadFrom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
@PropertySource("classpath:properties/datasource.properties")
public class MagnusRedisConfiguration {

        @Value("${redis.host}")
        public String host;
        @Value("${redis.port}")
        public int port;
        @Value("${redis.timeout}")
        public int timeout;
        @Value("${redis.commonDatabase}")
        public int commonDatabase;

        public LettuceConnectionFactory lettuceConnectionFactory(int db) {
                RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
                redisStandaloneConfiguration.setPort(port);
                redisStandaloneConfiguration.setHostName(host);
                redisStandaloneConfiguration.setDatabase(commonDatabase);
                LettuceClientConfiguration lettuceClientConfiguration = LettuceClientConfiguration.builder()
                        .readFrom(ReadFrom.REPLICA_PREFERRED)
                        .commandTimeout(Duration.ofMillis(timeout))
                        .build();
                LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
                lettuceConnectionFactory.setDatabase(db);
                return lettuceConnectionFactory;
        }

        public RedisTemplate commonRedisTemplate() {
                RedisTemplate<String, Map> redisTemplate = new RedisTemplate<String, Map>();
                redisTemplate.setKeySerializer(new StringRedisSerializer());
                redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                redisTemplate.setConnectionFactory(lettuceConnectionFactory(commonDatabase));
                return redisTemplate;
        }
}

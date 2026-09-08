package dev.taskqueue.api.redis;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionProbe {
    private final RedisConnectionFactory connectionFactory;

    public RedisConnectionProbe(
        RedisConnectionFactory connectionFactory
    ) {
        this.connectionFactory = connectionFactory;
    }

    public String ping() {
        try (RedisConnection connection = connectionFactory.getConnection()){
            return connection.ping();
        }
    }
}
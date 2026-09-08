package dev.taskqueue.api.http;

import dev.taskqueue.api.redis.RedisConnectionProbe;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dependencies")
public class DependencyController {
    private final RedisConnectionProbe redisProbe;

    public DependencyController(
        RedisConnectionProbe redisProbe
    ) {
        this.redisProbe = redisProbe;
    }

    @GetMapping("/redis")
    public ResponseEntity<RedisStatusResponse> redis() {
        try {
            String response = redisProbe.ping();

            return ResponseEntity.ok(
                new RedisStatusResponse(
                    true,
                    response
                )
            );
        } catch (DataAccessException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                new RedisStatusResponse(
                    false,
                    null
                )
            );
        }
    }
}
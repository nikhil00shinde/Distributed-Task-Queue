package dev.taskqueue.api.http;

public record RedisStatusResponse(
    boolean reachable,
    String response
) {
    
}
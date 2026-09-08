package dev.taskqueue.api.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.taskqueue.model.RetryPolicy;
import dev.taskqueue.model.Task;
import dev.taskqueue.model.TaskFailure;
import dev.taskqueue.model.TaskState;
import dev.taskqueue.model.TaskType;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RedisTaskRepository implements TaskRepository {

    private final HashOperations<String, String, String> hashes;
    private final ObjectMapper json;

    public RedisTaskRepository(
            StringRedisTemplate redis,
            ObjectMapper json
    ) {
        this.hashes = redis.opsForHash();
        this.json = json;
    }

    @Override
    public Task save(Task task) {
        Map<String, String> fields = new HashMap<>();

        fields.put("id", task.id().toString());
        fields.put("type", task.type().name());
        fields.put("payload", task.payload());
        fields.put("state", task.state().name());
        fields.put("attempts", Integer.toString(task.attempts()));

        fields.put("maxAttempts",
                Integer.toString(task.retryPolicy().maxAttempts()));
        fields.put("initialBackoff",
                task.retryPolicy().initialBackoff().toString());
        fields.put("maxBackoff",
                task.retryPolicy().maxBackoff().toString());

        fields.put("createdAt", task.createdAt().toString());
        fields.put("updatedAt", task.updatedAt().toString());

        try {
            fields.put("result",
                    json.writeValueAsString(task.result().orElse(null)));
            fields.put("lastFailure",
                    json.writeValueAsString(task.lastFailure().orElse(null)));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Could not serialize task fields", exception);
        }

        hashes.putAll(key(task.id()), fields);

        return task;
    }

    @Override
    public Optional<Task> findById(UUID id) {
        Map<String, String> fields = hashes.entries(key(id));

        if (fields.isEmpty()) {
            return Optional.empty();
        }

        RetryPolicy policy = new RetryPolicy(
                Integer.parseInt(fields.get("maxAttempts")),
                Duration.parse(fields.get("initialBackoff")),
                Duration.parse(fields.get("maxBackoff"))
        );

        try {
            Task task = new Task(
                    UUID.fromString(fields.get("id")),
                    TaskType.valueOf(fields.get("type")),
                    fields.get("payload"),
                    TaskState.valueOf(fields.get("state")),
                    policy,
                    Integer.parseInt(fields.get("attempts")),
                    Instant.parse(fields.get("createdAt")),
                    Instant.parse(fields.get("updatedAt")),
                    Optional.ofNullable(
                            json.readValue(fields.get("result"), String.class)),
                    Optional.ofNullable(
                            json.readValue(fields.get("lastFailure"), TaskFailure.class))
            );

            return Optional.of(task);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException(
                    "Could not deserialize task fields", exception);
        }
    }

    private String key(UUID id) {
        return "task:" + id;
    }
}
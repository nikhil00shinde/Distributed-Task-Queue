package dev.taskqueue.model;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record Task(
    UUID id,
    TaskType type,
    String payload,
    TaskState state,
    RetryPolicy retryPolicy,
    int attempts,
    Instant createdAt,
    Instant updatedAt,
    Optional<String> result,
    Optional<TaskFailure> lastFailure
) {
    public Task {
        id = Objects.requireNonNull(id, "id must not be null");
        type = Objects.requireNonNull(type, "type must not be null");
        payload = Objects.requireNonNull(payload, "payload must not be null");
        state = Objects.requireNonNull(state, "state must not be null");

        retryPolicy = Objects.requireNonNull(
            retryPolicy,
            "retryPolicy must not be null"
        );

        createdAt = Objects.requireNonNull(
            createdAt,
            "createdAt must not be null"
        );

        updatedAt = Objects.requireNonNull(
            updatedAt,
            "createdAt must not be null"
        );

        if (payload.isBlank()){
            throw new IllegalArgumentException("payload must not be blank");
        }


        if (attempts < 0 || attempts > retryPolicy.maxAttempts()) {
            throw new IllegalArgumentException("attempts must be between 0 and maxAttempts");
        }

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }

        if (state == TaskState.DEAD_LETTERED && lastFailure.isEmpty()) {
            throw new IllegalArgumentException (
                "a dead-lettered task must contain failure metadata"
            );
        }
    }

    public static Task queued(
        UUID id,
        TaskType type,
        String payload,
        RetryPolicy retryPolicy,
        Clock clock
    ) {
        Objects.requireNonNull(clock, "clock must not be null");

        Instant now = clock.instant();

        return new Task(
            id,
            type,
            payload,
            TaskState.QUEUED,
            retryPolicy,
            0,
            now,
            now,
            Optional.empty(),
            Optional.empty()
        );
    }
}
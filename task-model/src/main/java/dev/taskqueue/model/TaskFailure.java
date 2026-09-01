package dev.taskqueue.model;

import java.time.Instant;
import java.util.Objects;


public record TaskFailure(
    String errorType,
    String message,
    boolean retryable,
    int attempt,
    Instant occurredAt
) {
    public TaskFailure {
        errorType = Objects.requireNonNull(
            errorType,
            "errorType must not be null"
        );

        message = Objects.requireNonNull(
            message,
            "message must not be null"
        );

        occurredAt = Objects.requireNonNull(
            occurredAt,
            "occurredAt must not be null"
        );

        if (errorType.isBlank()) {
            throw new IllegalArgumentException("error must not be blank");
        }

        if (message.isBlank()) {
            throw new IllegalArgumentException(
                "message must not be blank"
            );
        }

        if (attempt < 1) {
            throw new IllegalArgumentException(
                "failure attempt be atleast 1"
            );
        }
    }
}
package dev.taskqueue.model;

import java.time.Duration;
import java.util.Objects;

public record RetryPolicy(
        int maxAttempts,
        Duration initialBackoff,
        Duration maxBackoff
) {

    public RetryPolicy {
        initialBackoff = Objects.requireNonNull(
                initialBackoff,
                "initialBackoff must not be null"
        );

        maxBackoff = Objects.requireNonNull(
                maxBackoff,
                "maxBackoff must not be null"
        );

        if (maxAttempts < 1) {
            throw new IllegalArgumentException(
                    "maxAttempts must be at least 1"
            );
        }

        if (initialBackoff.isZero() || initialBackoff.isNegative()) {
            throw new IllegalArgumentException(
                    "initialBackoff must be positive"
            );
        }

        if (maxBackoff.compareTo(initialBackoff) < 0) {
            throw new IllegalArgumentException(
                    "maxBackoff must be greater than or equal to initialBackoff"
            );
        }
    }

    public static RetryPolicy standard() {
        return new RetryPolicy(
                3,
                Duration.ofSeconds(1),
                Duration.ofSeconds(30)
        );
    }
}
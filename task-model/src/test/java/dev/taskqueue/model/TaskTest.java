package dev.taskqueue.model;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTest {

    private static final UUID TASK_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");

    private static final Instant NOW =
            Instant.parse("2026-09-01T20:00:00Z");

    private static final Clock FIXED_CLOCK =
            Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void newTaskStartsInQueuedState() {
        RetryPolicy policy = RetryPolicy.standard();

        Task task = Task.queued(
                TASK_ID,
                TaskType.ECHO,
                "hello",
                policy,
                FIXED_CLOCK
        );

        assertAll(
                () -> assertEquals(TASK_ID, task.id()),
                () -> assertEquals(TaskType.ECHO, task.type()),
                () -> assertEquals("hello", task.payload()),
                () -> assertEquals(TaskState.QUEUED, task.state()),
                () -> assertEquals(0, task.attempts()),
                () -> assertEquals(NOW, task.createdAt()),
                () -> assertEquals(NOW, task.updatedAt()),
                () -> assertTrue(task.result().isEmpty()),
                () -> assertTrue(task.lastFailure().isEmpty())
        );
    }

    @Test
    void taskRejectsBlankPayload() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Task.queued(
                        TASK_ID,
                        TaskType.ECHO,
                        "   ",
                        RetryPolicy.standard(),
                        FIXED_CLOCK
                )
        );
    }

    @Test
    void retryPolicyRejectsInvalidConfiguration() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new RetryPolicy(
                        0,
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(30)
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new RetryPolicy(
                        3,
                        Duration.ofSeconds(30),
                        Duration.ofSeconds(1)
                )
        );
    }

    @Test
    void deadLetteredTaskRequiresFailureMetadata() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Task(
                        TASK_ID,
                        TaskType.ECHO,
                        "hello",
                        TaskState.DEAD_LETTERED,
                        RetryPolicy.standard(),
                        3,
                        NOW,
                        NOW,
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }
}
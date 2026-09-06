package dev.taskqueue.api.http;

import dev.taskqueue.model.Task;
import dev.taskqueue.model.TaskFailure;
import dev.taskqueue.model.TaskState;
import dev.taskqueue.model.TaskType;

import java.time.Instant;
import java.util.UUID;

public record TaskStatusResponse(
    UUID taskId,
    TaskType type,
    TaskState state,
    int attempts,
    Instant createdAt,
    Instant updatedAt,
    String result,
    TaskFailure lastFailure
) {
    public static TaskStatusResponse from(Task task){
        return new TaskStatusResponse(
            task.id(),
            task.type(),
            task.state(),
            task.attempts(),
            task.createdAt(),
            task.updatedAt(),
            task.result().orElse(null),
            task.lastFailure().orElse(null)
        );
    }
}

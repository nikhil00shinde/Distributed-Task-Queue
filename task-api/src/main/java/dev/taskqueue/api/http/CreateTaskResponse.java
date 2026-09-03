package dev.taskqueue.api.http;

import dev.taskqueue.model.TaskState;

import java.util.UUID;

public record CreateTaskResponse(UUID taskId, TaskState state){}


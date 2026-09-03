package dev.taskqueue.api.http;

import dev.taskqueue.model.TaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record CreateTaskRequest (

    @NotNull
    TaskType type,

    @NotBlank
    String payload
) {

}


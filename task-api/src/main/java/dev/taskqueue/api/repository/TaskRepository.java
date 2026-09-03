package dev.taskqueue.api.repository;

import dev.taskqueue.model.Task;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository{
    Task save(Task task);

    Optional<Task> findById(UUID id);
}



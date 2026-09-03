package dev.taskqueue.api.service;

import dev.taskqueue.api.repository.TaskRepository;
import dev.taskqueue.model.RetryPolicy;
import dev.taskqueue.model.Task;
import dev.taskqueue.model.TaskType;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final Clock clock;

    public TaskService(TaskRepository repository,Clock clock){
        this.repository = repository;
        this.clock = clock;
    }

    public Task create(TaskType type, String payload){
        Task task = Task.queued(UUID.randomUUID(),type,payload,RetryPolicy.standard(),clock);
        return repository.save(task);
    }

    public Optional<Task> findById(UUID id) {
        return repository.findById(id);
    }
}
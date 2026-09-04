package dev.taskqueue.api.http;

import dev.taskqueue.api.service.TaskService;
import dev.taskqueue.model.Task;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;
import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<CreateTaskResponse> create(@Valid @RequestBody CreateTaskRequest request){
        Task task = taskService.create(
            request.type(),
            request.payload()
        );

        CreateTaskResponse response = new CreateTaskResponse(
            task.id(),
            task.state()
        );

        URI location = URI.create(
            "/api/tasks/" + task.id()
        );

        return ResponseEntity.accepted().location(location).body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskStatusResponse> findById(
        @PathVariable UUID taskId
    ) {
        Optional<Task> task = taskService.findById(taskId);

        if (task.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        TaskStatusResponse response = TaskStatusResponse.from(task.get());
        return ResponseEntity.ok(response);
    }
}
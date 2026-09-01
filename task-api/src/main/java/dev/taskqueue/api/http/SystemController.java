package dev.taskqueue.api.http;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class SystemController {

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("task-api","UP");
    }
}



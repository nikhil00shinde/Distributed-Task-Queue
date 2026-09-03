package dev.taskqueue.api.http;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void validRequestCreatesQueuedTask() throws Exception {
        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "type": "ECHO",
                                          "payload": "hello"
                                        }
                                        """)
                )
                .andExpect(status().isAccepted())
                .andExpect(
                        header().string(
                                "Location",
                                matchesPattern(
                                        "/api/tasks/[0-9a-f-]{36}"
                                )
                        )
                )
                .andExpect(jsonPath("$.taskId").isString())
                .andExpect(jsonPath("$.state").value("QUEUED"));
    }

    @Test
    void blankPayloadIsRejected() throws Exception {
        mockMvc.perform(
                        post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                          "type": "ECHO",
                                          "payload": "   "
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());
    }
}
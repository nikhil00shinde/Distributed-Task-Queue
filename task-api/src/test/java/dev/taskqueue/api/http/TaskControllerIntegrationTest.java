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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(properties = "spring.data.redis.database=1")
@AutoConfigureMockMvc
public class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
void createdTaskCanBeRetrieved() throws Exception {
    MvcResult creationResult = mockMvc.perform(
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
            .andReturn();

    JsonNode creationBody = objectMapper.readTree(
            creationResult.getResponse().getContentAsString()
    );

    String taskId = creationBody
            .get("taskId")
            .asText();

    mockMvc.perform(
                    get("/api/tasks/{taskId}", taskId)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.taskId").value(taskId))
            .andExpect(jsonPath("$.type").value("ECHO"))
            .andExpect(jsonPath("$.state").value("QUEUED"))
            .andExpect(jsonPath("$.attempts").value(0))
            .andExpect(jsonPath("$.result").doesNotExist())
            .andExpect(jsonPath("$.lastFailure").doesNotExist());
}

@Test
void unknownTaskReturnsNotFound() throws Exception {
    UUID unknownId = UUID.fromString(
            "99999999-9999-9999-9999-999999999999"
    );

    mockMvc.perform(
                    get("/api/tasks/{taskId}", unknownId)
            )
            .andExpect(status().isNotFound());
}
}
package dev.taskqueue.api.http;

import dev.taskqueue.api.redis.RedisConnectionProbe;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DependencyController.class)
class DependencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RedisConnectionProbe redisProbe;

    @Test
    void reportsRedisIsReachable() throws Exception {
        when(redisProbe.ping()).thenReturn("PONG");

        mockMvc.perform(
                        get("/api/dependencies/redis")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reachable").value(true))
                .andExpect(jsonPath("$.response").value("PONG"));
    }

    @Test
    void reportsServiceUnavailableWhenRedisFails()
            throws Exception {

        when(redisProbe.ping()).thenThrow(
                new DataAccessResourceFailureException(
                        "Redis unavailable"
                )
        );

        mockMvc.perform(
                        get("/api/dependencies/redis")
                )
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.reachable").value(false));
    }
}
package com.ratelimiter.service;

import com.ratelimiter.filter.RateLimitFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {TenantRegistry.class, RateLimitFilter.class, com.ratelimiter.controller.SecureDataController.class})
@AutoConfigureMockMvc
public class GatewaySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void verifyGatewayAllowsAndThenThrottles() throws Exception {
        String testKey = "client-api-key-123";

        // 1. Initial burst requests should slide right through the filter
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/api/v1/secure-data").header("X-API-KEY", testKey))
                    .andExpect(status().isOk());
        }

        // 2. The 6th concurrent request must be instantly short-circuited with an HTTP 429
        mockMvc.perform(get("/api/v1/secure-data").header("X-API-KEY", testKey))
                .andExpect(status().isTooManyRequests());
    }
}
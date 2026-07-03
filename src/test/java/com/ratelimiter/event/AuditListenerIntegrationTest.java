package com.ratelimiter.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles; // <-- NEW IMPORT

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test") // <-- THIS ACTIVATES THE H2 DATABASE
public class AuditListenerIntegrationTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    public void testAsyncEventPublishing() throws InterruptedException {
        RateLimitEvent event = new RateLimitEvent("test-tenant", "/api/v1/secure-data", false);

        long startTime = System.currentTimeMillis();
        publisher.publishEvent(event);
        long endTime = System.currentTimeMillis();

        // The publish action should take less than 50ms because the actual logging
        // is offloaded to the ThreadPoolTaskExecutor.
        assertTrue((endTime - startTime) < 50, "Event publishing blocked the main thread!");
    }
}
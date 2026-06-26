package com.ratelimiter.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class AuditListenerIntegrationTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    public void testAsyncEventPublishing() throws InterruptedException {
        // This is a conceptual test. To make it strictly assertable,
        // you would inject a mock repository into the listener to verify the save call.
        // For now, we fire the event and ensure the application context handles it gracefully.

        RateLimitExceededEvent event = new RateLimitExceededEvent("test-tenant", "192.168.1.1");

        long startTime = System.currentTimeMillis();
        publisher.publishEvent(event);
        long endTime = System.currentTimeMillis();

        // The publish action should take less than 50ms because the actual logging
        // is offloaded to the ThreadPoolTaskExecutor.
        assertTrue((endTime - startTime) < 50, "Event publishing blocked the main thread!");
    }
}
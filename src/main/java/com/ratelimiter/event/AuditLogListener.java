package com.ratelimiter.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditLogListener {

    @Async // Forces this method to run on a background thread pool
    @EventListener
    public void handleRateLimitExceeded(RateLimitExceededEvent event) {
        // Here you would eventually inject a Repository to save to PostgreSQL
        System.out.println("🚨 AUDIT LOG: Tenant " + event.getTenantId() + " blocked.");
    }
}
package com.ratelimiter.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service // 1. THE INVERSION OF CONTROL TRIGGER
public class TenantRegistry {

    // 2. THE MULTI-TENANT STATE RECEPTACLE
    private final ConcurrentHashMap<String, TokenBucket> registry = new ConcurrentHashMap<>();

    // 3. enterprise CONFIGURATION LIMITS (TEST VALUES)
    private final long defaultCapacity = 5;
    private final long defaultRefillRate = 1;

    // 4. THE EXPLICIT DEFAULT CONSTRUCTOR FOR SPRING
    public TenantRegistry() {
        // Left empty intentionally so Spring can instantiate this as a Bean
    }

    // 5. THE HOT-PATH EVALUATION ENGINE
    public boolean allowRequest(String tenantId) {
        TokenBucket bucket = registry.computeIfAbsent(tenantId,
                id -> new TokenBucket(defaultCapacity, defaultRefillRate));

        return bucket.tryConsume();
    }
}
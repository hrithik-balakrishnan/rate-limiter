package com.ratelimiter.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TenantRegistry {

    private final ConcurrentHashMap<String, TokenBucket> registry = new ConcurrentHashMap<>();
    private final long defaultCapacity = 5;
    private final long defaultRefillRate = 1;

    public TenantRegistry() {}

    public boolean allowRequest(String tenantId) {
        TokenBucket bucket = registry.computeIfAbsent(tenantId,
                id -> new TokenBucket(defaultCapacity, defaultRefillRate));
        return bucket.tryConsume();
    }
}
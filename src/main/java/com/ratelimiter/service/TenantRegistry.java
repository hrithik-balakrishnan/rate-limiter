package com.ratelimiter.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TenantRegistry {
    // ConcurrentHashMap handles internal memory striping so threads don't block each other
    private final Map<String, TokenBucket> tenantBuckets = new ConcurrentHashMap<>();

    private final long capacity;
    private final double refillRate;

    // If a bucket isn't touched for 1 hour, it is considered stale
    private static final long STALE_THRESHOLD_NANOS = TimeUnit.HOURS.toNanos(1);

    public TenantRegistry(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;

        // Background daemon thread to prune dead tenants every 10 minutes
        ScheduledExecutorService pruner = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "tenant-pruner-thread");
            t.setDaemon(true); // Daemon threads don't prevent the JVM from shutting down
            return t;
        });
        pruner.scheduleAtFixedRate(this::evictStaleTenants, 10, 10, TimeUnit.MINUTES);
    }

    public TokenBucket getBucketForTenant(String tenantId) {
        // computeIfAbsent is atomic. If 100 requests for the same new API key arrive
        // at the exact same millisecond, the bucket is only instantiated once.
        return tenantBuckets.computeIfAbsent(tenantId, k -> new TokenBucket(capacity, refillRate));
    }

    private void evictStaleTenants() {
        long now = System.nanoTime();
        // removeIf safely iterates the ConcurrentHashMap and removes stale entries
        tenantBuckets.entrySet().removeIf(entry -> {
            long lastTouch = entry.getValue().getLastRefillTimeNanos();
            return (now - lastTouch) > STALE_THRESHOLD_NANOS;
        });
        System.out.println("Pruning cycle complete. Active tenants: " + tenantBuckets.size());
    }
}
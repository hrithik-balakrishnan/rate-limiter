package com.ratelimiter.service;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket {
    private final long capacity;
    private final double tokensPerNanosecond;
    private static final long SCALE = 1000L;

    private final AtomicLong tokens;
    private final AtomicLong lastRefillTimeNanos;

    public TokenBucket(long capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.tokensPerNanosecond = refillRatePerSecond / 1_000_000_000.0;
        this.tokens = new AtomicLong(capacity * SCALE);
        this.lastRefillTimeNanos = new AtomicLong(System.nanoTime());
    }

    public boolean allowRequest() {
        while (true) {
            long currentTokens = tokens.get();
            long lastRefill = lastRefillTimeNanos.get();

            long now = System.nanoTime();
            long elapsed = now - lastRefill;

            long tokensToAdd = (long) (elapsed * tokensPerNanosecond * SCALE);
            long newTokens = Math.min(capacity * SCALE, currentTokens + tokensToAdd);

            if (newTokens < SCALE) {
                return false;
            }

            // ✅ Fix: CAS on time first — only ONE thread wins the refill
            if (lastRefillTimeNanos.compareAndSet(lastRefill, now)) {
                tokens.set(newTokens - SCALE);
                return true;
            }
            // another thread already refilled → retry whole loop
        }
    }

    public long getTokensAtomicValue() {
        return tokens.get();
    }
    // The registry needs to know exactly when a bucket was last used
    public long getLastRefillTimeNanos() {
        return lastRefillTimeNanos.get();
    }
}
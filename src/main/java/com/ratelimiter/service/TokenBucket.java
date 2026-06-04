package com.ratelimiter.service;

public class TokenBucket {
    private final long capacity;
    private final double tokensPerNanosecond;

    private double tokens;
    private long lastRefillTimeNanos;

    public TokenBucket(long capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.tokensPerNanosecond = refillRatePerSecond / 1_000_000_000.0;
        this.tokens = capacity;
        this.lastRefillTimeNanos = System.nanoTime();
    }

    public boolean allowRequest() {
        refill();
        if (this.tokens >= 1.0) {
            tokens -= 1.0;
            return true;
        }
        return false;
    }


    private void refill() {
        long now = System.nanoTime();
        long elapsedNanos = now - this.lastRefillTimeNanos;

        if (elapsedNanos <= 0) return;

        double tokensToAdd = elapsedNanos * this.tokensPerNanosecond;
        this.tokens = Math.min(this.capacity, this.tokens + tokensToAdd);
        this.lastRefillTimeNanos = now;
    }

    public double getTokens() {
        return this.tokens;
    }
}
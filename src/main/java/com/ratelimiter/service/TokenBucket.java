package com.ratelimiter.service;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket {
    private final long capacity;
    private final long refillRatePerSecond;
    private final AtomicLong state;

    public TokenBucket(long capacity, long refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        long currentTimeSec = System.currentTimeMillis() / 1000;
        this.state = new AtomicLong(pack(capacity, currentTimeSec));
    }

    // ADD THIS METHOD OUT LOUD IF IT IS MISSING:
    public boolean tryConsume() {
        while (true) {
            long currentState = state.get();
            long currentTokens = getTokens(currentState);
            long lastRefillTime = getTimestamp(currentState);
            long currentTimeSec = System.currentTimeMillis() / 1000;

            // Calculate accumulated tokens lazily
            long elapsedSeconds = Math.max(0, currentTimeSec - lastRefillTime);
            long newTokens = currentTokens + (elapsedSeconds * refillRatePerSecond);
            newTokens = Math.min(capacity, newTokens);

            // If no tokens available, deny request
            if (newTokens < 1) {
                return false;
            }

            // Prepare next packed state (decrement 1 token, update timestamp)
            long nextState = pack(newTokens - 1, currentTimeSec);

            // Atomic CAS update execution
            if (state.compareAndSet(currentState, nextState)) {
                return true;
            }
        }
    }

    // Bit-packing helper utilities
    private long pack(long tokens, long timestamp) {
        return (tokens << 32) | (timestamp & 0xFFFFFFFFL);
    }

    private long getTokens(long packed) {
        return packed >>> 32;
    }

    private long getTimestamp(long packed) {
        return packed & 0xFFFFFFFFL;
    }
}
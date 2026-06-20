package com.ratelimiter.event;

public class RateLimitExceededEvent {
    private final String tenantId;
    private final String ipAddress;
    private final long timestamp;

    public RateLimitExceededEvent(String tenantId, String ipAddress) {
        this.tenantId = tenantId;
        this.ipAddress = ipAddress;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getTenantId() { return tenantId; }
    public String getIpAddress() { return ipAddress; }
    public long getTimestamp() { return timestamp; }
}
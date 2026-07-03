package com.ratelimiter.event;

public class RateLimitEvent {
    private final String tenantId;
    private final String endpoint;
    private final boolean allowed;
    private final long timestamp;

    public RateLimitEvent(String tenantId, String endpoint, boolean allowed) {
        this.tenantId = tenantId;
        this.endpoint = endpoint;
        this.allowed = allowed;
        this.timestamp = System.currentTimeMillis();
    }

    public String getTenantId() { return tenantId; }
    public String getEndpoint() { return endpoint; }
    public boolean isAllowed() { return allowed; }
    public long getTimestamp() { return timestamp; }
}
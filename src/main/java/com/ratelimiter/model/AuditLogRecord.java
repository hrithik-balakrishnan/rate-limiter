package com.ratelimiter.model;

import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_tenant_time", columnList = "tenant_id, timestamp")
})
public class AuditLogRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_seq")
    @SequenceGenerator(name = "audit_seq", sequenceName = "audit_log_seq", allocationSize = 50)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "endpoint", nullable = false)
    private String endpoint;

    @Column(name = "decision_allowed", nullable = false)
    private boolean decisionAllowed;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    protected AuditLogRecord() {}

    public AuditLogRecord(String tenantId, String endpoint, boolean decisionAllowed, long timestamp) {
        this.tenantId = tenantId;
        this.endpoint = endpoint;
        this.decisionAllowed = decisionAllowed;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getEndpoint() { return endpoint; }
    public boolean isDecisionAllowed() { return decisionAllowed; }
    public long getTimestamp() { return timestamp; }
}
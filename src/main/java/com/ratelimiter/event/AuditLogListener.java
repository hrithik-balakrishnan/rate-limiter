package com.ratelimiter.event;

import com.ratelimiter.model.AuditLogRecord;
import com.ratelimiter.repository.AuditLogRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditLogListener {

    private final AuditLogRepository repository;

    public AuditLogListener(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Async
    @EventListener
    public void handleRateLimitEvent(RateLimitEvent event) {
        AuditLogRecord record = new AuditLogRecord(
                event.getTenantId(),
                event.getEndpoint(),
                event.isAllowed(),
                event.getTimestamp()
        );
        repository.save(record);
    }
}
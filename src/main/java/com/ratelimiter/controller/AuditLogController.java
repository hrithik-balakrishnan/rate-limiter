package com.ratelimiter.controller;

import com.ratelimiter.model.AuditLogRecord;
import com.ratelimiter.repository.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditLogController {

    private final AuditLogRepository repository;

    public AuditLogController(AuditLogRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/api/v1/audit-logs")
    public ResponseEntity<Page<AuditLogRecord>> getAuditLogs(
            @RequestHeader("X-API-KEY") String tenantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Enforce a maximum page size to prevent memory exhaustion attacks
        int safeSize = Math.min(size, 100);

        // Sort by timestamp descending so the newest logs appear first
        PageRequest pageRequest = PageRequest.of(page, safeSize, Sort.by(Sort.Direction.DESC, "timestamp"));

        Page<AuditLogRecord> results = repository.findByTenantId(tenantId, pageRequest);

        return ResponseEntity.ok(results);
    }
}
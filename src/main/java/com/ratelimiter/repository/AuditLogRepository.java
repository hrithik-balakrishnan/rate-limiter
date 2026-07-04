package com.ratelimiter.repository;

import com.ratelimiter.model.AuditLogRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogRecord, Long> {

    // Spring generates: SELECT * FROM audit_logs WHERE tenant_id = ? ORDER BY ? LIMIT ? OFFSET ?
    Page<AuditLogRecord> findByTenantId(String tenantId, Pageable pageable);
}
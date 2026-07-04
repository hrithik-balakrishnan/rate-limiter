package com.ratelimiter.repository;

import com.ratelimiter.model.AuditLogRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
public class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository repository;

    @Test
    public void testPaginationReturnsCorrectSubset() {
        String tenantId = "tenant-a";

        // Insert 3 records directly into the H2 database
        repository.save(new AuditLogRecord(tenantId, "/api/data", true, 1000L));
        repository.save(new AuditLogRecord(tenantId, "/api/data", false, 2000L));
        repository.save(new AuditLogRecord(tenantId, "/api/data", true, 3000L));

        // Request Page 0, but strictly limit the size to 2 records
        PageRequest pageRequest = PageRequest.of(0, 2);
        Page<AuditLogRecord> result = repository.findByTenantId(tenantId, pageRequest);

        // Verify the database respected the limit and pagination metadata
        assertEquals(2, result.getContent().size(), "Should only return 2 records for this page");
        assertEquals(3, result.getTotalElements(), "Should know there are 3 total elements in the DB");
        assertEquals(2, result.getTotalPages(), "Should calculate 2 total pages needed");
    }
}
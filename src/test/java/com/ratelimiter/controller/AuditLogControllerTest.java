package com.ratelimiter.controller;

import com.ratelimiter.filter.RateLimitFilter;
import com.ratelimiter.model.AuditLogRecord;
import com.ratelimiter.repository.AuditLogRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Explicitly exclude the RateLimitFilter so it doesn't crash asking for the TenantRegistry
@WebMvcTest(
        controllers = AuditLogController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = RateLimitFilter.class
        )
)
public class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogRepository repository;

    @Test
    public void testGetAuditLogsRequiresApiKey() throws Exception {
        mockMvc.perform(get("/api/v1/audit-logs"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAuditLogsReturnsPaginatedData() throws Exception {
        String tenantId = "tenant-a";
        AuditLogRecord record = new AuditLogRecord(tenantId, "/api/data", true, 1000L);
        PageImpl<AuditLogRecord> mockPage = new PageImpl<>(List.of(record));

        Mockito.when(repository.findByTenantId(eq(tenantId), any(PageRequest.class)))
                .thenReturn(mockPage);

        mockMvc.perform(get("/api/v1/audit-logs")
                        .header("X-API-KEY", tenantId)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].tenantId").value(tenantId))
                .andExpect(jsonPath("$.totalElements").value(1));
    }
}
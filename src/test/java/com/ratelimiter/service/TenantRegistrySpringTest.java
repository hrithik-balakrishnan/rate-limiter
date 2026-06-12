package com.ratelimiter.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

// FIX: Explicitly pass the class to initialize only what we need in the context
@SpringBootTest(classes = TenantRegistry.class)
public class TenantRegistrySpringTest {

    @Autowired
    private TenantRegistry tenantRegistry;

    @Test
    public void verifyRegistryIsInjectableAndFunctional() {
        assertNotNull(tenantRegistry, "TenantRegistry bean should be successfully managed by Spring!");

        boolean allowed = tenantRegistry.allowRequest("test-tenant");
        assertTrue(allowed, "The core rate limiter engine should allow the first request from a new tenant.");
    }
}
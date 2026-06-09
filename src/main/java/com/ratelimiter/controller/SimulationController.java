package com.ratelimiter.controller;

import com.ratelimiter.service.TenantRegistry;
import com.ratelimiter.service.TokenBucket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulationController {

    private final TenantRegistry registry = new TenantRegistry(100, 10);

    @GetMapping("/api/v1/sim/test")
    public String test(@RequestParam(defaultValue = "default_user") String apiKey) {
        TokenBucket bucket = registry.getBucketForTenant(apiKey);
        boolean allowed = bucket.allowRequest();
        long currentTokens = bucket.getTokensAtomicValue() / 1000;

        return "Tenant: " + apiKey + " | Allowed: " + allowed + " | Tokens: " + currentTokens;
    }
}
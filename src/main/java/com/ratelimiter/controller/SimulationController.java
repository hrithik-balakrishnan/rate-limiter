package com.ratelimiter.controller;

import com.ratelimiter.service.TokenBucket;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SimulationController {

    private final TokenBucket localBucket = new TokenBucket(100, 10);

    @GetMapping("/api/v1/sim/test")
    public String test() {
        boolean allowed = localBucket.allowRequest();
        long currentTokens = localBucket.getTokensAtomicValue() / 1000;
        return "Request allowed: " + allowed + " | Tokens remaining: " + currentTokens;
    }
}
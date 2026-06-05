package com.ratelimiter.controller;

import com.ratelimiter.service.TokenBucket;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/sim")
public class SimulationController {

    // Initialize a single local bucket: Capacity = 5, Refill = 1 token/sec
    private final TokenBucket localBucket = new TokenBucket(5, 1.0);

    @GetMapping("/test")
    public ResponseEntity<String> simulateRateLimit() {
        if (localBucket.allowRequest()) {
            return ResponseEntity.ok("SUCCESS: Token consumed. Remaining: " + localBucket.getTokens());
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("ERROR 429: Rate Limit Exceeded.");
    }
}
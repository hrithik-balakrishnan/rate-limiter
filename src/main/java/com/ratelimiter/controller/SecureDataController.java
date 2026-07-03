package com.ratelimiter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecureDataController {
    @GetMapping("/api/v1/secure-data")
    public ResponseEntity<String> getResource() {
        return ResponseEntity.ok("Success! You successfully passed through the rate-limiting gateway.");
    }
}
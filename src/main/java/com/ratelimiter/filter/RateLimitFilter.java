package com.ratelimiter.filter;

import com.ratelimiter.service.TenantRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final TenantRegistry tenantRegistry;

    // Spring Boot automatically injects your Phase 1 Singleton Engine right here
    public RateLimitFilter(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Edge Extraction: Grab the identifier header from the network stream
        String tenantId = request.getHeader("X-API-KEY");

        // 2. Local Fallback: Allow easy local verification from web browsers
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = "anonymous-user";
        }

        // 3. The Hot Path Interrogation: Check the lock-free CAS engine
        if (tenantRegistry.allowRequest(tenantId)) {
            // Token successfully consumed! Pass request downstream to the controller layer.
            filterChain.doFilter(request, response);
        } else {
            // Short-circuit: The user is out of tokens. Fail fast at the perimeter.
            response.setStatus(429); // HTTP 429: Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"API rate limit exceeded. Please try again later.\"}");
        }
    }
}
package com.ratelimiter.filter;

import com.ratelimiter.event.RateLimitExceededEvent;
import com.ratelimiter.service.TenantRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final TenantRegistry tenantRegistry;
    private final ApplicationEventPublisher eventPublisher;

    public RateLimitFilter(TenantRegistry tenantRegistry, ApplicationEventPublisher eventPublisher) {
        this.tenantRegistry = tenantRegistry;
        this.eventPublisher = eventPublisher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Primary Extraction
        String tenantId = request.getHeader("X-API-KEY");

        // 2. Secondary Extraction: Proxy Chain Traversal
        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = extractRealIp(request);
        }

        // 3. Hot Path
        if (tenantRegistry.allowRequest(tenantId)) {
            filterChain.doFilter(request, response);
        } else {
            // 4. Fire Async Event using the exact identifier that was blocked
            eventPublisher.publishEvent(new RateLimitExceededEvent(tenantId, extractRealIp(request)));

            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"API rate limit exceeded.\"}");
        }
    }

    // UTILITY: Safely extract IP through load balancers
    private String extractRealIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // The first IP in the comma-separated list is the original client
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
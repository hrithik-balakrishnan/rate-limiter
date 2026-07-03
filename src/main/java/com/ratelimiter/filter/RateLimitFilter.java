package com.ratelimiter.filter;

import com.ratelimiter.event.RateLimitEvent;
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

        String tenantId = request.getHeader("X-API-KEY");

        if (tenantId == null || tenantId.isEmpty()) {
            tenantId = extractRealIp(request);
        }

        String endpoint = request.getRequestURI();

        if (tenantRegistry.allowRequest(tenantId)) {
            eventPublisher.publishEvent(new RateLimitEvent(tenantId, endpoint, true));
            filterChain.doFilter(request, response);
        } else {
            eventPublisher.publishEvent(new RateLimitEvent(tenantId, endpoint, false));
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too Many Requests\", \"message\": \"API rate limit exceeded.\"}");
        }
    }

    private String extractRealIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
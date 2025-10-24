package org.adarssh.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that adds correlation IDs to every request for distributed tracing.
 * <p>
 * Correlation IDs help track requests across the system and are essential for debugging
 * and monitoring in production environments.
 * <p>
 * Behavior:
 * - If X-Correlation-ID header is present, uses that value
 * - Otherwise, generates a new UUID
 * - Adds correlation ID to MDC (Mapped Diagnostic Context) for logging
 * - Adds X-Correlation-ID to response headers
 * - Cleans up MDC after request completes
 */
@Component
@Order(1) // Execute early in filter chain
public class CorrelationIdFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String CORRELATION_ID_MDC_KEY = "correlationId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Extract or generate correlation ID
        String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Add to MDC for logging (will appear in all log messages for this request)
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);

        // Add to response headers so clients can track their requests
        httpResponse.setHeader(CORRELATION_ID_HEADER, correlationId);

        try {
            log.debug("Processing request with correlation ID: {}", correlationId);
            chain.doFilter(request, response);
        } finally {
            // Clean up MDC to prevent memory leaks
            MDC.remove(CORRELATION_ID_MDC_KEY);
        }
    }
}

package org.adarssh.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting filter using token bucket algorithm via Bucket4j.
 * <p>
 * Rate Limits (per user):
 * - 100 requests per minute
 * - 1000 requests per hour
 * <p>
 * When rate limit is exceeded:
 * - Returns 429 Too Many Requests
 * - Includes Retry-After header (seconds until next token available)
 * - Includes X-Rate-Limit-Remaining header
 * - Returns JSON error response
 * <p>
 * Implementation:
 * - Uses token bucket algorithm for smooth rate limiting
 * - Per-user buckets (identified by authenticated username)
 * - Anonymous users share a single bucket
 * - Thread-safe with ConcurrentHashMap
 */
@Component
@Order(2) // Execute after CorrelationIdFilter
public class RateLimitingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RateLimitingFilter.class);

    // Rate limit configurations
    private static final long TOKENS_PER_MINUTE = 100;
    private static final long TOKENS_PER_HOUR = 1000;

    // Store buckets per user
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get user identifier (authenticated user or "anonymous")
        String userKey = getUserKey();

        // Get or create bucket for this user
        Bucket bucket = buckets.computeIfAbsent(userKey, this::createBucket);

        // Try to consume a token
        var probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            // Token consumed successfully, request allowed
            httpResponse.setHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
            log.debug("Rate limit check passed for user: {} (remaining: {})",
                    userKey, probe.getRemainingTokens());
            chain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000; // Convert to seconds
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
            httpResponse.setHeader("X-Rate-Limit-Remaining", "0");
            httpResponse.setHeader("Retry-After", String.valueOf(waitForRefill));

            String errorJson = String.format(
                    "{\"error\":\"Too Many Requests\","
                    + "\"message\":\"Rate limit exceeded. Try again in %d seconds.\","
                    + "\"status\":429}",
                    waitForRefill
            );
            httpResponse.getWriter().write(errorJson);

            log.warn("Rate limit exceeded for user: {} (retry after {} seconds)",
                    userKey, waitForRefill);
        }
    }

    /**
     * Get the user identifier for rate limiting.
     * Uses authenticated username or "anonymous" for unauthenticated requests.
     *
     * @return user key for rate limiting
     */
    private String getUserKey() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "anonymous";
    }

    /**
     * Create a new bucket with configured rate limits.
     * <p>
     * Rate limits:
     * - 100 requests per minute (burst capacity)
     * - 1000 requests per hour (refill rate)
     *
     * @param userKey the user identifier
     * @return configured bucket
     */
    private Bucket createBucket(String userKey) {
        log.debug("Creating new rate limit bucket for user: {}", userKey);

        // Per-minute limit (100 requests/minute, refills at ~1.67 requests/second)
        Bandwidth minuteLimit = Bandwidth.classic(
                TOKENS_PER_MINUTE,
                Refill.intervally(TOKENS_PER_MINUTE, Duration.ofMinutes(1))
        );

        // Per-hour limit (1000 requests/hour, refills at ~0.28 requests/second)
        Bandwidth hourLimit = Bandwidth.classic(
                TOKENS_PER_HOUR,
                Refill.intervally(TOKENS_PER_HOUR, Duration.ofHours(1))
        );

        // Use both limits (most restrictive applies)
        return Bucket.builder()
                .addLimit(minuteLimit)
                .addLimit(hourLimit)
                .build();
    }
}

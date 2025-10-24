package org.adarssh.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for HTTP security headers.
 * <p>
 * Adds security headers to protect against common web vulnerabilities:
 * - HSTS (HTTP Strict Transport Security)
 * - X-Content-Type-Options (prevent MIME sniffing)
 * - X-Frame-Options (prevent clickjacking)
 * - X-XSS-Protection (XSS protection)
 * - Content-Security-Policy (CSP)
 * - Referrer-Policy
 * - Permissions-Policy
 */
@Configuration
public class SecurityHeadersConfig {

    /**
     * Creates a filter that adds security headers to all HTTP responses.
     *
     * @return the security headers filter
     */
    @Bean
    public Filter securityHeadersFilter() {
        return (ServletRequest request, ServletResponse response, FilterChain chain)
                -> {
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            // HSTS - Force HTTPS for 1 year
            httpResponse.setHeader("Strict-Transport-Security",
                    "max-age=31536000; includeSubDomains");

            // Prevent MIME type sniffing
            httpResponse.setHeader("X-Content-Type-Options", "nosniff");

            // Prevent clickjacking
            httpResponse.setHeader("X-Frame-Options", "DENY");

            // XSS Protection
            httpResponse.setHeader("X-XSS-Protection", "1; mode=block");

            // Content Security Policy
            httpResponse.setHeader("Content-Security-Policy",
                    "default-src 'self'; "
                    + "script-src 'self' 'unsafe-inline'; "
                    + "style-src 'self' 'unsafe-inline'; "
                    + "img-src 'self' data: https:; "
                    + "font-src 'self' data:; "
                    + "connect-src 'self' https://api.spotify.com https://accounts.spotify.com");

            // Referrer Policy
            httpResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Permissions Policy (formerly Feature Policy)
            httpResponse.setHeader("Permissions-Policy",
                    "geolocation=(), microphone=(), camera=()");

            chain.doFilter(request, response);
        };
    }
}

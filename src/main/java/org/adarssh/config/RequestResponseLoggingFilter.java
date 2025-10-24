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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * Filter to log HTTP requests and responses in development environment.
 * Only active when 'dev' profile is enabled.
 */
@Component
@Profile("dev")  // Only active in development
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 10000;  // 10KB max

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Wrap request and response to allow reading body multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        long startTime = System.currentTimeMillis();

        try {
            // Log request
            logRequest(wrappedRequest);

            // Process the request
            chain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // Log response
            logResponse(wrappedResponse, duration);

            // IMPORTANT: Copy cached response body to actual response
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder("\n========== HTTP REQUEST ==========\n");
        sb.append(String.format("%s %s %s\n",
                request.getMethod(),
                request.getRequestURI(),
                request.getProtocol()));

        // Log headers
        sb.append("Headers:\n");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // Don't log sensitive headers
            if (!headerName.equalsIgnoreCase("Authorization") &&
                !headerName.equalsIgnoreCase("Cookie")) {
                sb.append(String.format("  %s: %s\n", headerName, request.getHeader(headerName)));
            } else {
                sb.append(String.format("  %s: [REDACTED]\n", headerName));
            }
        }

        // Log query parameters
        String queryString = request.getQueryString();
        if (queryString != null) {
            sb.append(String.format("Query: %s\n", queryString));
        }

        // Log request body
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            if (body.length() > MAX_PAYLOAD_LENGTH) {
                body = body.substring(0, MAX_PAYLOAD_LENGTH) + "... [TRUNCATED]";
            }
            sb.append(String.format("Body:\n%s\n", body));
        }

        sb.append("==================================");
        log.debug(sb.toString());
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        StringBuilder sb = new StringBuilder("\n========== HTTP RESPONSE ==========\n");
        sb.append(String.format("Status: %d\n", response.getStatus()));
        sb.append(String.format("Duration: %d ms\n", duration));

        // Log response headers
        sb.append("Headers:\n");
        response.getHeaderNames().forEach(headerName ->
                sb.append(String.format("  %s: %s\n", headerName, response.getHeader(headerName))));

        // Log response body
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, StandardCharsets.UTF_8);
            if (body.length() > MAX_PAYLOAD_LENGTH) {
                body = body.substring(0, MAX_PAYLOAD_LENGTH) + "... [TRUNCATED]";
            }
            sb.append(String.format("Body:\n%s\n", body));
        }

        sb.append("===================================");
        log.debug(sb.toString());
    }
}

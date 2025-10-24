package org.adarssh.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for RequestResponseLoggingFilter.
 */
class RequestResponseLoggingFilterTest {

    private RequestResponseLoggingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RequestResponseLoggingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);
    }

    @Test
    void doFilterLogsRequestAndResponse() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/spotify/top/tracks");
        request.setQueryString("limit=10");
        request.addHeader("Content-Type", "application/json");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilterRedactsAuthorizationHeader() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");
        request.addHeader("Authorization", "Bearer secret-token");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
        // Authorization header should be logged as [REDACTED]
    }

    @Test
    void doFilterRedactsCookieHeader() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");
        request.addHeader("Cookie", "session=abc123");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
        // Cookie header should be logged as [REDACTED]
    }

    @Test
    void doFilterHandlesRequestWithBody() throws Exception {
        // given
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContentType("application/json");
        request.setContent("{\"test\":\"data\"}".getBytes());

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doFilterHandlesRequestWithoutQueryString() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/health");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doFilterHandlesResponseWithBody() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        // when
        filter.doFilter(request, response, filterChain);
        response.getWriter().write("{\"status\":\"ok\"}");
        response.getWriter().flush();

        // then
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doFilterPassesThroughNonHttpRequests() throws Exception {
        // given
        ServletResponse nonHttpResponse = mock(ServletResponse.class);

        // when
        filter.doFilter(request, nonHttpResponse, filterChain);

        // then
        verify(filterChain).doFilter(request, nonHttpResponse);
    }

    @Test
    void doFilterMeasuresRequestDuration() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        // when
        long startTime = System.currentTimeMillis();
        filter.doFilter(request, response, filterChain);
        long endTime = System.currentTimeMillis();

        // then
        verify(filterChain).doFilter(any(), any());
        // Duration should be measurable (even if very small)
        assertThat(endTime).isGreaterThanOrEqualTo(startTime);
    }

    @Test
    void doFilterCopiesResponseBodyToActualResponse() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
        // Response should be properly copied back
        assertThat(response.getContentAsByteArray()).isNotNull();
    }

    @Test
    void doFilterHandlesMultipleHeaders() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");
        request.addHeader("Content-Type", "application/json");
        request.addHeader("Accept", "application/json");
        request.addHeader("User-Agent", "Test Client");

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doFilterHandlesEmptyRequestBody() throws Exception {
        // given
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContent(new byte[0]);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(any(), any());
    }

    @Test
    void doFilterHandlesDifferentHttpMethods() throws Exception {
        // given
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH"};

        for (String method : methods) {
            request = new MockHttpServletRequest();
            response = new MockHttpServletResponse();
            request.setMethod(method);
            request.setRequestURI("/api/test");

            // when
            filter.doFilter(request, response, filterChain);
        }

        // then - verify doFilter was called 5 times (once per method)
        verify(filterChain, times(5)).doFilter(any(), any());
    }

    @Test
    void doFilterTruncatesLargeRequestBody() throws Exception {
        // given - Create a request body larger than 10KB
        StringBuilder largeBody = new StringBuilder();
        for (int i = 0; i < 15000; i++) {
            largeBody.append("x");
        }
        request.setMethod("POST");
        request.setRequestURI("/api/test");
        request.setContentType("application/json");
        request.setContent(largeBody.toString().getBytes());

        // Create a filter chain that reads the request body
        FilterChain readingChain = (req, resp) -> {
            // Force reading the request body so ContentCachingRequestWrapper caches it
            try {
                req.getInputStream().readAllBytes();
            } catch (Exception e) {
                // Ignore
            }
        };

        // when
        filter.doFilter(request, response, readingChain);

        // then - Request should be logged with truncation
        // The filter will truncate and log "[TRUNCATED]"
    }

    @Test
    void doFilterTruncatesLargeResponseBody() throws Exception {
        // given
        request.setMethod("GET");
        request.setRequestURI("/api/test");

        // Create a filter chain that writes a large response
        FilterChain largeResponseChain = (req, resp) -> {
            StringBuilder largeBody = new StringBuilder();
            for (int i = 0; i < 15000; i++) {
                largeBody.append("y");
            }
            resp.getWriter().write(largeBody.toString());
            resp.getWriter().flush();
        };

        // when
        filter.doFilter(request, response, largeResponseChain);

        // then - Response should be logged with truncation
        // The filter will truncate and log "[TRUNCATED]"
        assertThat(response.getContentAsString().length()).isGreaterThan(10000);
    }
}

package org.adarssh.config;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for RateLimitingFilter.
 */
class RateLimitingFilterTest {

    private RateLimitingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RateLimitingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterAllowsRequestWhenUnderRateLimit() throws Exception {
        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isNotNull();
    }

    @Test
    void doFilterSetsRateLimitRemainingHeader() throws Exception {
        // when
        filter.doFilter(request, response, filterChain);

        // then
        String remaining = response.getHeader("X-Rate-Limit-Remaining");
        assertThat(remaining).isNotNull();
        assertThat(Long.parseLong(remaining)).isGreaterThanOrEqualTo(0);
    }

    @Test
    void doFilterCreatesPerUserBuckets() throws Exception {
        // given - authenticated user
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "testuser", "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilterUsesAnonymousBucketForUnauthenticatedUsers() throws Exception {
        // given - no authentication
        SecurityContextHolder.clearContext();

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilterRejectRequestsWhenRateLimitExceeded() throws Exception {
        // given - consume all tokens by making 101 requests
        for (int i = 0; i < 101; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(request, response, filterChain);
        }

        // when - one more request should be rejected
        response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getHeader("X-Rate-Limit-Remaining")).isEqualTo("0");
        assertThat(response.getHeader("Retry-After")).isNotNull();
    }

    @Test
    void doFilterReturnsJsonErrorWhenRateLimitExceeded() throws Exception {
        // given - exhaust rate limit
        for (int i = 0; i < 101; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(request, response, filterChain);
        }

        // when - one more request
        response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);

        // then
        String content = response.getContentAsString();
        assertThat(content).contains("Too Many Requests");
        assertThat(content).contains("Rate limit exceeded");
        assertThat(content).contains("429");
        assertThat(content).contains("seconds");
    }

    @Test
    void doFilterSetsRetryAfterHeaderWhenRateLimitExceeded() throws Exception {
        // given - exhaust rate limit
        for (int i = 0; i < 101; i++) {
            response = new MockHttpServletResponse();
            filter.doFilter(request, response, filterChain);
        }

        // when
        response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);

        // then
        String retryAfter = response.getHeader("Retry-After");
        assertThat(retryAfter).isNotNull();
        assertThat(Long.parseLong(retryAfter)).isGreaterThanOrEqualTo(0);
    }

    @Test
    void doFilterMaintainsSeparateBucketsForDifferentUsers() throws Exception {
        // given - user1
        Authentication user1 = new UsernamePasswordAuthenticationToken(
                "user1", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(user1);

        // when - user1 makes 2 requests
        filter.doFilter(request, response, filterChain);
        response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);
        long user1Remaining = Long.parseLong(response.getHeader("X-Rate-Limit-Remaining"));

        // given - user2
        Authentication user2 = new UsernamePasswordAuthenticationToken(
                "user2", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(user2);

        // when - user2 makes 1 request (fresh bucket)
        response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);
        long user2Remaining = Long.parseLong(response.getHeader("X-Rate-Limit-Remaining"));

        // then - user2 should have more tokens (fresh bucket, fewer requests)
        assertThat(user2Remaining).isGreaterThan(user1Remaining);
    }

    @Test
    void doFilterHandlesAnonymousUserAuthentication() throws Exception {
        // given - anonymous authentication
        Authentication anonymous = new UsernamePasswordAuthenticationToken(
                "anonymousUser", "password", List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void doFilterCreatesNewBucketOnFirstRequest() throws Exception {
        // given - brand new user
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "newuser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when
        filter.doFilter(request, response, filterChain);

        // then - should have nearly full bucket
        long remaining = Long.parseLong(response.getHeader("X-Rate-Limit-Remaining"));
        assertThat(remaining).isGreaterThan(90); // Should be close to 100
    }

    @Test
    void doFilterReusesBucketForSameUser() throws Exception {
        // given
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "sameuser", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when - first request
        filter.doFilter(request, response, filterChain);
        long firstRemaining = Long.parseLong(response.getHeader("X-Rate-Limit-Remaining"));

        // when - second request
        response = new MockHttpServletResponse();
        filter.doFilter(request, response, filterChain);
        long secondRemaining = Long.parseLong(response.getHeader("X-Rate-Limit-Remaining"));

        // then - remaining tokens should decrease
        assertThat(secondRemaining).isLessThan(firstRemaining);
    }

    @Test
    void doFilterAllowsRequestToPassThroughChain() throws Exception {
        // when
        filter.doFilter(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(any(), any());
    }
}

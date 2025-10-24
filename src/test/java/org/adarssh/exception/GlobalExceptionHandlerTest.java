package org.adarssh.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleSpotifyAuthenticationException_returnsUnauthorizedResponse() {
        // given
        SpotifyAuthenticationException exception = new SpotifyAuthenticationException(
                "Failed to authenticate with Spotify");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/tracks");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyAuthenticationException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(401);
        assertThat(response.getBody().error()).isEqualTo("Authentication failed");
        assertThat(response.getBody().message()).isEqualTo("Failed to authenticate with Spotify");
        assertThat(response.getBody().path()).isEqualTo("uri=/api/spotify/top/tracks");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleSpotifyAuthenticationException_withDifferentMessage_returnsCorrectMessage() {
        // given
        SpotifyAuthenticationException exception = new SpotifyAuthenticationException(
                "Invalid credentials");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/artists");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyAuthenticationException(exception, webRequest);

        // then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
        assertThat(response.getBody().path()).isEqualTo("uri=/api/spotify/top/artists");
    }

    @Test
    void handleSpotifyApiException_returnsCorrectStatusCode() {
        // given
        SpotifyApiException exception = new SpotifyApiException("API Error", 500);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/tracks");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyApiException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Spotify API error");
        assertThat(response.getBody().message()).isEqualTo("API Error");
        assertThat(response.getBody().path()).isEqualTo("uri=/api/spotify/top/tracks");
    }

    @Test
    void handleSpotifyApiException_with404Status_returnsNotFound() {
        // given
        SpotifyApiException exception = new SpotifyApiException("Resource not found", 404);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/albums");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyApiException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).isEqualTo("Resource not found");
    }

    @Test
    void handleSpotifyApiException_with400Status_returnsBadRequest() {
        // given
        SpotifyApiException exception = new SpotifyApiException("Invalid request", 400);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/genres");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyApiException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    void handleGlobalException_returnsInternalServerError() {
        // given
        Exception exception = new RuntimeException("Unexpected error occurred");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/wrapped");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGlobalException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
        assertThat(response.getBody().error()).isEqualTo("Internal server error");
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred. Please try again later.");
        assertThat(response.getBody().path()).isEqualTo("uri=/api/spotify/wrapped");
        assertThat(response.getBody().timestamp()).isNotNull();
    }

    @Test
    void handleGlobalException_withNullPointerException_returnsGenericMessage() {
        // given
        Exception exception = new NullPointerException("Null value encountered");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/health");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGlobalException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("An unexpected error occurred. Please try again later.");
    }

    @Test
    void handleGlobalException_withIllegalArgumentException_returnsInternalServerError() {
        // given
        Exception exception = new IllegalArgumentException("Invalid argument");
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleGlobalException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @Test
    void errorResponse_containsAllRequiredFields() {
        // given
        SpotifyApiException exception = new SpotifyApiException("Test error", 500);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyApiException(exception, webRequest);

        // then
        GlobalExceptionHandler.ErrorResponse errorResponse = response.getBody();
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.status()).isNotNull();
        assertThat(errorResponse.error()).isNotNull();
        assertThat(errorResponse.message()).isNotNull();
        assertThat(errorResponse.path()).isNotNull();
        assertThat(errorResponse.timestamp()).isNotNull();
    }

    @Test
    void handleSpotifyApiException_withCause_stillReturnsCorrectResponse() {
        // given
        Throwable cause = new RuntimeException("Root cause");
        SpotifyApiException exception = new SpotifyApiException("Wrapped error", 503, cause);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/tracks");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyApiException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(503);
        assertThat(response.getBody().message()).isEqualTo("Wrapped error");
    }

    @Test
    void handleSpotifyAuthenticationException_withCause_returnsUnauthorized() {
        // given
        Throwable cause = new RuntimeException("OAuth error");
        SpotifyAuthenticationException exception = new SpotifyAuthenticationException(
                "Authentication failed", cause);
        when(webRequest.getDescription(false)).thenReturn("uri=/api/spotify/top/artists");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> response =
                exceptionHandler.handleSpotifyAuthenticationException(exception, webRequest);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Authentication failed");
    }

    @Test
    void allExceptionHandlers_returnTimestampInResponse() {
        // given
        when(webRequest.getDescription(false)).thenReturn("uri=/api/test");

        // when
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> authResponse =
                exceptionHandler.handleSpotifyAuthenticationException(
                        new SpotifyAuthenticationException("Auth error"), webRequest);
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> apiResponse =
                exceptionHandler.handleSpotifyApiException(
                        new SpotifyApiException("API error", 500), webRequest);
        ResponseEntity<GlobalExceptionHandler.ErrorResponse> globalResponse =
                exceptionHandler.handleGlobalException(
                        new Exception("Generic error"), webRequest);

        // then
        assertThat(authResponse.getBody()).isNotNull();
        assertThat(authResponse.getBody().timestamp()).isNotNull();
        assertThat(apiResponse.getBody()).isNotNull();
        assertThat(apiResponse.getBody().timestamp()).isNotNull();
        assertThat(globalResponse.getBody()).isNotNull();
        assertThat(globalResponse.getBody().timestamp()).isNotNull();
    }
}

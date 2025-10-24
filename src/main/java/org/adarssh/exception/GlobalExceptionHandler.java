package org.adarssh.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

/**
 * Global exception handler for the application
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(SpotifyAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleSpotifyAuthenticationException(
            SpotifyAuthenticationException ex,
            WebRequest request) {
        log.error("Spotify authentication error: {}", ex.getMessage(), ex);

        var errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication failed",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(SpotifyApiException.class)
    public ResponseEntity<ErrorResponse> handleSpotifyApiException(
            SpotifyApiException ex,
            WebRequest request) {
        log.error("Spotify API error: {}", ex.getMessage(), ex);

        var status = HttpStatus.valueOf(ex.getStatusCode());
        var errorResponse = new ErrorResponse(
                status.value(),
                "Spotify API error",
                ex.getMessage(),
                request.getDescription(false),
                Instant.now()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);

        var errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal server error",
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false),
                Instant.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Standard error response format
     */
    public record ErrorResponse(
            int status,
            String error,
            String message,
            String path,
            Instant timestamp
    ) {}
}

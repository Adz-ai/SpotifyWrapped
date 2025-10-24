package org.adarssh.exception;

/**
 * Custom exception for Spotify API errors
 */
public class SpotifyApiException extends RuntimeException {

    private final int statusCode;

    public SpotifyApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public SpotifyApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

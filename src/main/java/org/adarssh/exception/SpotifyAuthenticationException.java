package org.adarssh.exception;

/**
 * Exception thrown when Spotify authentication fails
 */
public class SpotifyAuthenticationException extends RuntimeException {

    public SpotifyAuthenticationException(String message) {
        super(message);
    }

    public SpotifyAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}

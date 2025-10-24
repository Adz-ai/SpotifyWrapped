package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for Spotify OAuth token response
 */
public record SpotifyTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("expires_in") Integer expiresIn
) { }

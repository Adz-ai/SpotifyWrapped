package org.adarssh.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for Spotify API
 */
@ConfigurationProperties(prefix = "spotify.api")
@Validated
public record SpotifyProperties(
        @NotBlank String baseUrl,
        @NotBlank String authUrl,
        @NotBlank String clientId,
        @NotBlank String clientSecret,
        Integer defaultLimit
) {}

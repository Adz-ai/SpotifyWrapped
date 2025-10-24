package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for Spotify External URLs
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ExternalUrls(
        String spotify
) { }

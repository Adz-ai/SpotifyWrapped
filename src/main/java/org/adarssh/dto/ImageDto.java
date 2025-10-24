package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for Spotify Image
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ImageDto(
        String url,
        Integer height,
        Integer width
) { }

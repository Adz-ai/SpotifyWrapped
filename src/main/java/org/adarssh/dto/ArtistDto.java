package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for Spotify Artist
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ArtistDto(
        String id,
        String name,
        List<String> genres,
        Integer popularity,
        @JsonProperty("external_urls") ExternalUrls externalUrls,
        List<ImageDto> images
) {}

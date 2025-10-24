package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for Spotify Track
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TrackDto(
        String id,
        String name,
        @JsonProperty("album") AlbumDto album,
        @JsonProperty("artists") List<ArtistDto> artists,
        Integer popularity,
        @JsonProperty("duration_ms") Integer durationMs,
        @JsonProperty("external_urls") ExternalUrls externalUrls
) { }

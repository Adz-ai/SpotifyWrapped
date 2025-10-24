package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * DTO for Spotify Album
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AlbumDto(
        String id,
        String name,
        @JsonProperty("album_type") String albumType,
        @JsonProperty("release_date") String releaseDate,
        List<ArtistDto> artists,
        List<ImageDto> images,
        @JsonProperty("external_urls") ExternalUrls externalUrls
) {}

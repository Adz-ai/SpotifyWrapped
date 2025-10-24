package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Generic DTO for Spotify paged responses
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SpotifyPagedResponse<T>(
        List<T> items,
        Integer total,
        Integer limit,
        Integer offset,
        String next,
        String previous
) {}

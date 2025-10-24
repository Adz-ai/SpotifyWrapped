package org.adarssh.dto;

import java.util.List;

/**
 * Response DTO for user's top items (tracks, artists, etc.)
 */
public record UserTopItemsResponse<T>(
        String type,
        Integer count,
        List<T> items
) {}

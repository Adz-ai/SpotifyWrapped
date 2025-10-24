package org.adarssh.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Time range for Spotify top items.
 * <p>
 * Spotify API supports three time ranges for calculating user's top items:
 * - SHORT_TERM: approximately last 4 weeks
 * - MEDIUM_TERM: approximately last 6 months (default)
 * - LONG_TERM: calculated from all time data
 */
public enum TimeRange {

    /**
     * Short term (approximately last 4 weeks).
     */
    SHORT_TERM("short_term"),

    /**
     * Medium term (approximately last 6 months) - DEFAULT.
     */
    MEDIUM_TERM("medium_term"),

    /**
     * Long term (calculated from all time data).
     */
    LONG_TERM("long_term");

    private final String value;

    TimeRange(String value) {
        this.value = value;
    }

    /**
     * Gets the Spotify API value for this time range.
     *
     * @return the API value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Parse a string value into a TimeRange.
     * Accepts both enum names (SHORT_TERM) and API values (short_term).
     *
     * @param value the string value
     * @return the TimeRange
     * @throws IllegalArgumentException if value is invalid
     */
    public static TimeRange fromString(String value) {
        if (value == null) {
            return MEDIUM_TERM; // Default
        }

        // Try matching by API value
        for (TimeRange timeRange : values()) {
            if (timeRange.value.equalsIgnoreCase(value)) {
                return timeRange;
            }
        }

        // Try matching by enum name
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid time range: " + value + ". Valid values are: short_term, medium_term, long_term");
        }
    }
}

package org.adarssh.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for TimeRange enum.
 */
class TimeRangeTest {

    @Test
    void enumValuesAreCorrect() {
        // when/then
        assertThat(TimeRange.values()).hasSize(3);
        assertThat(TimeRange.values()).containsExactly(
                TimeRange.SHORT_TERM,
                TimeRange.MEDIUM_TERM,
                TimeRange.LONG_TERM
        );
    }

    @Test
    void getValueReturnsCorrectApiValue() {
        // when/then
        assertThat(TimeRange.SHORT_TERM.getValue()).isEqualTo("short_term");
        assertThat(TimeRange.MEDIUM_TERM.getValue()).isEqualTo("medium_term");
        assertThat(TimeRange.LONG_TERM.getValue()).isEqualTo("long_term");
    }

    @Test
    void fromStringParsesApiValuesCaseInsensitive() {
        // when/then
        assertThat(TimeRange.fromString("short_term")).isEqualTo(TimeRange.SHORT_TERM);
        assertThat(TimeRange.fromString("SHORT_TERM")).isEqualTo(TimeRange.SHORT_TERM);
        assertThat(TimeRange.fromString("Short_Term")).isEqualTo(TimeRange.SHORT_TERM);

        assertThat(TimeRange.fromString("medium_term")).isEqualTo(TimeRange.MEDIUM_TERM);
        assertThat(TimeRange.fromString("MEDIUM_TERM")).isEqualTo(TimeRange.MEDIUM_TERM);

        assertThat(TimeRange.fromString("long_term")).isEqualTo(TimeRange.LONG_TERM);
        assertThat(TimeRange.fromString("LONG_TERM")).isEqualTo(TimeRange.LONG_TERM);
    }

    @Test
    void fromStringParsesEnumNames() {
        // when/then
        assertThat(TimeRange.fromString("SHORT_TERM")).isEqualTo(TimeRange.SHORT_TERM);
        assertThat(TimeRange.fromString("MEDIUM_TERM")).isEqualTo(TimeRange.MEDIUM_TERM);
        assertThat(TimeRange.fromString("LONG_TERM")).isEqualTo(TimeRange.LONG_TERM);
    }

    @Test
    void fromStringWithNullReturnsDefault() {
        // when
        TimeRange result = TimeRange.fromString(null);

        // then
        assertThat(result).isEqualTo(TimeRange.MEDIUM_TERM);
    }

    @Test
    void fromStringWithInvalidValueThrowsException() {
        // when/then
        assertThatThrownBy(() -> TimeRange.fromString("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid time range: invalid")
                .hasMessageContaining("short_term, medium_term, long_term");
    }

    @Test
    void fromStringWithEmptyStringThrowsException() {
        // when/then
        assertThatThrownBy(() -> TimeRange.fromString(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid time range");
    }

    @Test
    void valueOfWorksForEnumNames() {
        // when/then
        assertThat(TimeRange.valueOf("SHORT_TERM")).isEqualTo(TimeRange.SHORT_TERM);
        assertThat(TimeRange.valueOf("MEDIUM_TERM")).isEqualTo(TimeRange.MEDIUM_TERM);
        assertThat(TimeRange.valueOf("LONG_TERM")).isEqualTo(TimeRange.LONG_TERM);
    }
}

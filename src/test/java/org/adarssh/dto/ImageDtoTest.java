package org.adarssh.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ImageDto record.
 */
class ImageDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void constructorCreatesInstanceWithAllFields() {
        // when
        ImageDto image = new ImageDto("https://example.com/image.jpg", 640, 640);

        // then
        assertThat(image.url()).isEqualTo("https://example.com/image.jpg");
        assertThat(image.height()).isEqualTo(640);
        assertThat(image.width()).isEqualTo(640);
    }

    @Test
    void constructorAcceptsNullValues() {
        // when
        ImageDto image = new ImageDto(null, null, null);

        // then
        assertThat(image.url()).isNull();
        assertThat(image.height()).isNull();
        assertThat(image.width()).isNull();
    }

    @Test
    void equalsComparesAllFields() {
        // given
        ImageDto image1 = new ImageDto("https://example.com/image.jpg", 640, 640);
        ImageDto image2 = new ImageDto("https://example.com/image.jpg", 640, 640);
        ImageDto image3 = new ImageDto("https://different.com/image.jpg", 640, 640);

        // when/then
        assertThat(image1).isEqualTo(image2);
        assertThat(image1).isNotEqualTo(image3);
        assertThat(image1).isNotEqualTo(null);
        assertThat(image1).isNotEqualTo("not an ImageDto");
    }

    @Test
    void hashCodeIsConsistent() {
        // given
        ImageDto image1 = new ImageDto("https://example.com/image.jpg", 640, 640);
        ImageDto image2 = new ImageDto("https://example.com/image.jpg", 640, 640);

        // when/then
        assertThat(image1.hashCode()).isEqualTo(image2.hashCode());
    }

    @Test
    void toStringIncludesAllFields() {
        // given
        ImageDto image = new ImageDto("https://example.com/image.jpg", 640, 480);

        // when
        String result = image.toString();

        // then
        assertThat(result).contains("https://example.com/image.jpg");
        assertThat(result).contains("640");
        assertThat(result).contains("480");
    }

    @Test
    void deserializesFromJsonIgnoringUnknownProperties() throws Exception {
        // given
        String json = """
                {
                    "url": "https://example.com/image.jpg",
                    "height": 300,
                    "width": 300,
                    "unknownField": "should be ignored"
                }
                """;

        // when
        ImageDto result = objectMapper.readValue(json, ImageDto.class);

        // then
        assertThat(result.url()).isEqualTo("https://example.com/image.jpg");
        assertThat(result.height()).isEqualTo(300);
        assertThat(result.width()).isEqualTo(300);
    }

    @Test
    void serializesToJson() throws Exception {
        // given
        ImageDto image = new ImageDto("https://example.com/image.jpg", 640, 480);

        // when
        String json = objectMapper.writeValueAsString(image);

        // then
        assertThat(json).contains("https://example.com/image.jpg");
        assertThat(json).contains("640");
        assertThat(json).contains("480");
    }
}

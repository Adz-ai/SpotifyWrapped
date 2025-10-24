package org.adarssh.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    private OpenApiConfig openApiConfig;

    @BeforeEach
    void setUp() {
        openApiConfig = new OpenApiConfig();
    }

    @Test
    void openApiConfigCanBeInstantiated() {
        // then
        assertThat(openApiConfig).isNotNull();
    }

    @Test
    void spotifyWrappedOpenApiBeanIsCreated() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI).isNotNull();
    }

    @Test
    void spotifyWrappedOpenApiHasCorrectTitle() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Spotify Wrapped API");
    }

    @Test
    void spotifyWrappedOpenApiHasCorrectVersion() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
    }

    @Test
    void spotifyWrappedOpenApiHasDescription() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI.getInfo().getDescription()).isNotNull();
        assertThat(openAPI.getInfo().getDescription()).isNotEmpty();
        assertThat(openAPI.getInfo().getDescription()).contains("REST API");
        assertThat(openAPI.getInfo().getDescription()).contains("Spotify");
    }

    @Test
    void spotifyWrappedOpenApiHasContact() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI.getInfo().getContact()).isNotNull();
        assertThat(openAPI.getInfo().getContact().getName()).isEqualTo("Spotify Wrapped");
        assertThat(openAPI.getInfo().getContact().getUrl())
                .isEqualTo("https://github.com/adarssh/SpotifyWrapped");
    }

    @Test
    void spotifyWrappedOpenApiHasLicense() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI.getInfo().getLicense()).isNotNull();
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("Educational Use");
        assertThat(openAPI.getInfo().getLicense().getUrl())
                .isEqualTo("https://github.com/adarssh/SpotifyWrapped");
    }

    @Test
    void spotifyWrappedOpenApiHasServers() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        assertThat(openAPI.getServers()).isNotNull();
        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers()).hasSize(1);
    }

    @Test
    void spotifyWrappedOpenApiServerHasCorrectUrl() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        Server server = openAPI.getServers().getFirst();
        assertThat(server.getUrl()).isEqualTo("http://127.0.0.1:8080");
    }

    @Test
    void spotifyWrappedOpenApiServerHasDescription() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();

        // then
        Server server = openAPI.getServers().getFirst();
        assertThat(server.getDescription()).isEqualTo("Local development server");
    }

    @Test
    void spotifyWrappedOpenApiInfoContainsAllRequiredFields() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();
        Info info = openAPI.getInfo();

        // then
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isNotNull();
        assertThat(info.getVersion()).isNotNull();
        assertThat(info.getDescription()).isNotNull();
        assertThat(info.getContact()).isNotNull();
        assertThat(info.getLicense()).isNotNull();
    }

    @Test
    void spotifyWrappedOpenApiDescriptionContainsFeatures() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();
        String description = openAPI.getInfo().getDescription();

        // then
        assertThat(description).contains("Features");
        assertThat(description).contains("OAuth2");
        assertThat(description).contains("Authentication");
    }

    @Test
    void spotifyWrappedOpenApiDescriptionContainsUsageInstructions() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();
        String description = openAPI.getInfo().getDescription();

        // then
        assertThat(description).contains("Usage");
        assertThat(description).contains("authenticated");
        assertThat(description).contains("limit");
    }

    @Test
    void spotifyWrappedOpenApiDescriptionMentionsTopItems() {
        // when
        OpenAPI openAPI = openApiConfig.spotifyWrappedOpenApi();
        String description = openAPI.getInfo().getDescription();

        // then
        assertThat(description).containsIgnoringCase("tracks");
        assertThat(description).containsIgnoringCase("artists");
        assertThat(description).containsIgnoringCase("albums");
        assertThat(description).containsIgnoringCase("genres");
    }

    @Test
    void spotifyWrappedOpenApiCanBeInstantiatedMultipleTimes() {
        // when
        OpenAPI openAPI1 = openApiConfig.spotifyWrappedOpenApi();
        OpenAPI openAPI2 = openApiConfig.spotifyWrappedOpenApi();

        // then - Should create new instances each time
        assertThat(openAPI1).isNotNull();
        assertThat(openAPI2).isNotNull();
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
    }
}

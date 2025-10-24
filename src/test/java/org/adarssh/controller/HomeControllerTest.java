package org.adarssh.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeWhenAuthenticatedReturnsUserInfoAndEndpoints() throws Exception {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("display_name", "John Doe");
        OAuth2User oauth2User = new DefaultOAuth2User(
                null,
                attributes,
                "display_name"
        );

        // when/then
        mockMvc.perform(get("/api/")
                        .with(oauth2Login().oauth2User(oauth2User)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user").value("John Doe"))
                .andExpect(jsonPath("$.message").value("You are logged in! Try: /api/spotify/wrapped"))
                .andExpect(jsonPath("$.endpoints.wrapped").value("/api/spotify/wrapped"))
                .andExpect(jsonPath("$.endpoints.topTracks").value("/api/spotify/top/tracks"))
                .andExpect(jsonPath("$.endpoints.topArtists").value("/api/spotify/top/artists"))
                .andExpect(jsonPath("$.endpoints.topAlbums").value("/api/spotify/top/albums"))
                .andExpect(jsonPath("$.endpoints.topGenres").value("/api/spotify/top/genres"))
                .andExpect(jsonPath("$.endpoints.logout").value("/logout"));
    }

    @Test
    void homeWhenAuthenticatedWithDifferentUsernameReturnsCorrectUsername() throws Exception {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("display_name", "Jane Smith");
        OAuth2User oauth2User = new DefaultOAuth2User(
                null,
                attributes,
                "display_name"
        );

        // when/then
        mockMvc.perform(get("/api/")
                        .with(oauth2Login().oauth2User(oauth2User)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user").value("Jane Smith"));
    }

    @Test
    void homeWhenNotAuthenticatedReturnsLoginUrl() throws Exception {
        // when/then
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.message").value("Welcome to Spotify Wrapped API! Please log in with Spotify."))
                .andExpect(jsonPath("$.loginUrl").value("/oauth2/authorization/spotify"))
                .andExpect(jsonPath("$.user").doesNotExist())
                .andExpect(jsonPath("$.endpoints").doesNotExist());
    }

    @Test
    void homeWhenAuthenticatedWithNullDisplayNameReturnsNullUser() throws Exception {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("display_name", null);
        OAuth2User oauth2User = new DefaultOAuth2User(
                null,
                attributes,
                "display_name"
        );

        // when/then
        mockMvc.perform(get("/api/")
                        .with(oauth2Login().oauth2User(oauth2User)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user").isEmpty());
    }

    @Test
    void homeWhenAuthenticatedWithoutDisplayNameReturnsNullUser() throws Exception {
        // given
        Map<String, Object> attributes = new HashMap<>();
        OAuth2User oauth2User = new DefaultOAuth2User(
                null,
                attributes,
                "id"
        );

        // when/then
        mockMvc.perform(get("/api/")
                        .with(oauth2Login().oauth2User(oauth2User)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(true))
                .andExpect(jsonPath("$.user").isEmpty());
    }

    @Test
    void homeAlwaysReturnsJsonContentType() throws Exception {
        // when/then
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void homeAuthenticatedResponseContainsAllRequiredFields() throws Exception {
        // given
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("display_name", "Test User");
        OAuth2User oauth2User = new DefaultOAuth2User(
                null,
                attributes,
                "display_name"
        );

        // when/then
        mockMvc.perform(get("/api/")
                        .with(oauth2Login().oauth2User(oauth2User)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.endpoints").exists())
                .andExpect(jsonPath("$.loginUrl").doesNotExist());
    }

    @Test
    void homeUnauthenticatedResponseContainsAllRequiredFields() throws Exception {
        // when/then
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.loginUrl").exists())
                .andExpect(jsonPath("$.user").doesNotExist())
                .andExpect(jsonPath("$.endpoints").doesNotExist());
    }
}

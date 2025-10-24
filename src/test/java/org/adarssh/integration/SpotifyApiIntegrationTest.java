package org.adarssh.integration;

import org.adarssh.config.TestSecurityConfig;
import org.adarssh.dto.*;
import org.adarssh.service.SpotifyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class SpotifyApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpotifyService spotifyService;

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTest_getTopTracks_success() throws Exception {
        // given
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        TrackDto track = new TrackDto("track1", "Test Track", album, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 1, List.of(track));

        when(spotifyService.getTopTracks(5)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("tracks"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Track"));

        verify(spotifyService).getTopTracks(5);
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTest_getTopArtists_success() throws Exception {
        // given
        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock", "pop"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        UserTopItemsResponse<ArtistDto> mockResponse = new UserTopItemsResponse<>(
                "artists", 1, List.of(artist));

        when(spotifyService.getTopArtists(10)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/artists").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("artists"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Artist"))
                .andExpect(jsonPath("$.items[0].genres[0]").value("rock"));

        verify(spotifyService).getTopArtists(10);
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTest_getTopAlbums_success() throws Exception {
        // given
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        UserTopItemsResponse<AlbumDto> mockResponse = new UserTopItemsResponse<>(
                "albums", 1, List.of(album));

        when(spotifyService.getTopAlbums(5)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/albums").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("albums"))
                .andExpect(jsonPath("$.items[0].name").value("Test Album"));

        verify(spotifyService).getTopAlbums(5);
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTest_getTopGenres_success() throws Exception {
        // given
        UserTopItemsResponse<String> mockResponse = new UserTopItemsResponse<>(
                "genres", 3, List.of("rock", "indie", "pop"));

        when(spotifyService.getTopGenres(5)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/genres").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("genres"))
                .andExpect(jsonPath("$.items[0]").value("rock"))
                .andExpect(jsonPath("$.items[1]").value("indie"))
                .andExpect(jsonPath("$.items[2]").value("pop"));

        verify(spotifyService).getTopGenres(5);
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTest_getSpotifyWrapped_success() throws Exception {
        // given
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        TrackDto track = new TrackDto("track1", "Test Track", album, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock"),
                90, new ExternalUrls("https://spotify.com"), List.of());

        UserTopItemsResponse<TrackDto> tracksResponse = new UserTopItemsResponse<>(
                "tracks", 1, List.of(track));
        UserTopItemsResponse<ArtistDto> artistsResponse = new UserTopItemsResponse<>(
                "artists", 1, List.of(artist));
        UserTopItemsResponse<AlbumDto> albumsResponse = new UserTopItemsResponse<>(
                "albums", 1, List.of(album));
        UserTopItemsResponse<String> genresResponse = new UserTopItemsResponse<>(
                "genres", 1, List.of("rock"));

        when(spotifyService.getTopTracks(5)).thenReturn(tracksResponse);
        when(spotifyService.getTopArtists(5)).thenReturn(artistsResponse);
        when(spotifyService.getTopAlbums(5)).thenReturn(albumsResponse);
        when(spotifyService.getTopGenres(5)).thenReturn(genresResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/wrapped").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topTracks").exists())
                .andExpect(jsonPath("$.topArtists").exists())
                .andExpect(jsonPath("$.topAlbums").exists())
                .andExpect(jsonPath("$.topGenres").exists())
                .andExpect(jsonPath("$.topTracks.type").value("tracks"))
                .andExpect(jsonPath("$.topArtists.type").value("artists"))
                .andExpect(jsonPath("$.topAlbums.type").value("albums"))
                .andExpect(jsonPath("$.topGenres.type").value("genres"));

        verify(spotifyService).getTopTracks(5);
        verify(spotifyService).getTopArtists(5);
        verify(spotifyService).getTopAlbums(5);
        verify(spotifyService).getTopGenres(5);
    }

    @Test
    void fullFlowTest_withoutAuthentication_returnsUnauthorized() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/spotify/top/artists").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/spotify/top/albums").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/spotify/top/genres").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/spotify/wrapped").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        verify(spotifyService, never()).getTopTracks(anyInt());
        verify(spotifyService, never()).getTopArtists(anyInt());
        verify(spotifyService, never()).getTopAlbums(anyInt());
        verify(spotifyService, never()).getTopGenres(anyInt());
    }

    @Test
    @WithMockUser
    void fullFlowTest_withInvalidLimitParameter_returnsBadRequest() throws Exception {
        // when/then - Test various invalid limits
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "-1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "51"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "100"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt());
    }

    @Test
    void healthEndpoint_alwaysAccessible() throws Exception {
        // when/then - Health endpoint should not require authentication
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void homeEndpoint_accessibleWithoutAuthentication() throws Exception {
        // when/then - Home endpoint should be accessible without authentication
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.loginUrl").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTest_multipleEndpointsWithSameLimit_success() throws Exception {
        // given
        int limit = 10;

        AlbumDto album = new AlbumDto("album1", "Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        TrackDto track = new TrackDto("track1", "Track", album, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        ArtistDto artist = new ArtistDto("artist1", "Artist", List.of("rock"),
                90, new ExternalUrls("https://spotify.com"), List.of());

        UserTopItemsResponse<TrackDto> tracksResponse = new UserTopItemsResponse<>(
                "tracks", 1, List.of(track));
        UserTopItemsResponse<ArtistDto> artistsResponse = new UserTopItemsResponse<>(
                "artists", 1, List.of(artist));

        when(spotifyService.getTopTracks(limit)).thenReturn(tracksResponse);
        when(spotifyService.getTopArtists(limit)).thenReturn(artistsResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        mockMvc.perform(get("/api/spotify/top/artists").param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        verify(spotifyService).getTopTracks(limit);
        verify(spotifyService).getTopArtists(limit);
    }

    @Test
    @WithMockUser
    void fullFlowTest_boundaryValues_success() throws Exception {
        // given
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 0, List.of());

        when(spotifyService.getTopTracks(1)).thenReturn(mockResponse);
        when(spotifyService.getTopTracks(50)).thenReturn(mockResponse);

        // when/then - Test minimum valid limit
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "1"))
                .andExpect(status().isOk());

        // Test maximum valid limit
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "50"))
                .andExpect(status().isOk());

        verify(spotifyService).getTopTracks(1);
        verify(spotifyService).getTopTracks(50);
    }
}

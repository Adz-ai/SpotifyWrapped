package org.adarssh.integration;

import org.adarssh.config.TestSecurityConfig;
import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.ExternalUrls;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    void fullFlowTestGetTopTracksSuccess() throws Exception {
        // given
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        TrackDto track = new TrackDto("track1", "Test Track", album, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 1, List.of(track));

        when(spotifyService.getTopTracks(5, "medium_term")).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("tracks"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Track"));

        verify(spotifyService).getTopTracks(5, "medium_term");
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTestGetTopArtistsSuccess() throws Exception {
        // given
        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock", "pop"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        UserTopItemsResponse<ArtistDto> mockResponse = new UserTopItemsResponse<>(
                "artists", 1, List.of(artist));

        when(spotifyService.getTopArtists(10, "medium_term")).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/artists").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("artists"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Artist"))
                .andExpect(jsonPath("$.items[0].genres[0]").value("rock"));

        verify(spotifyService).getTopArtists(10, "medium_term");
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTestGetTopAlbumsSuccess() throws Exception {
        // given
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        UserTopItemsResponse<AlbumDto> mockResponse = new UserTopItemsResponse<>(
                "albums", 1, List.of(album));

        when(spotifyService.getTopAlbums(5, "medium_term")).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/albums").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("albums"))
                .andExpect(jsonPath("$.items[0].name").value("Test Album"));

        verify(spotifyService).getTopAlbums(5, "medium_term");
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTestGetTopGenresSuccess() throws Exception {
        // given
        UserTopItemsResponse<String> mockResponse = new UserTopItemsResponse<>(
                "genres", 3, List.of("rock", "indie", "pop"));

        when(spotifyService.getTopGenres(5, "medium_term")).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/genres").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("genres"))
                .andExpect(jsonPath("$.items[0]").value("rock"))
                .andExpect(jsonPath("$.items[1]").value("indie"))
                .andExpect(jsonPath("$.items[2]").value("pop"));

        verify(spotifyService).getTopGenres(5, "medium_term");
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTestGetSpotifyWrappedSuccess() throws Exception {
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

        when(spotifyService.getTopTracks(5, "medium_term")).thenReturn(tracksResponse);
        when(spotifyService.getTopArtists(5, "medium_term")).thenReturn(artistsResponse);
        when(spotifyService.getTopAlbums(5, "medium_term")).thenReturn(albumsResponse);
        when(spotifyService.getTopGenres(5, "medium_term")).thenReturn(genresResponse);

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

        verify(spotifyService).getTopTracks(5, "medium_term");
        verify(spotifyService).getTopArtists(5, "medium_term");
        verify(spotifyService).getTopAlbums(5, "medium_term");
        verify(spotifyService).getTopGenres(5, "medium_term");
    }

    @Test
    void fullFlowTestWithoutAuthenticationReturnsUnauthorized() throws Exception {
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

        verify(spotifyService, never()).getTopTracks(anyInt(), anyString());
        verify(spotifyService, never()).getTopArtists(anyInt(), anyString());
        verify(spotifyService, never()).getTopAlbums(anyInt(), anyString());
        verify(spotifyService, never()).getTopGenres(anyInt(), anyString());
    }

    @Test
    @WithMockUser
    void fullFlowTestWithInvalidLimitParameterReturnsBadRequest() throws Exception {
        // when/then - Test various invalid limits
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "0"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "-1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "51"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "100"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt(), anyString());
    }

    @Test
    void healthEndpointAlwaysAccessible() throws Exception {
        // when/then - Health endpoint should not require authentication
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void homeEndpointAccessibleWithoutAuthentication() throws Exception {
        // when/then - Home endpoint should be accessible without authentication
        mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated").value(false))
                .andExpect(jsonPath("$.loginUrl").exists());
    }

    @Test
    @WithMockUser(username = "testuser")
    void fullFlowTestMultipleEndpointsWithSameLimitSuccess() throws Exception {
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

        when(spotifyService.getTopTracks(limit, "medium_term")).thenReturn(tracksResponse);
        when(spotifyService.getTopArtists(limit, "medium_term")).thenReturn(artistsResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        mockMvc.perform(get("/api/spotify/top/artists").param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));

        verify(spotifyService).getTopTracks(limit, "medium_term");
        verify(spotifyService).getTopArtists(limit, "medium_term");
    }

    @Test
    @WithMockUser
    void fullFlowTestBoundaryValuesSuccess() throws Exception {
        // given
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 0, List.of());

        when(spotifyService.getTopTracks(1, "medium_term")).thenReturn(mockResponse);
        when(spotifyService.getTopTracks(50, "medium_term")).thenReturn(mockResponse);

        // when/then - Test minimum valid limit
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "1"))
                .andExpect(status().isOk());

        // Test maximum valid limit
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "50"))
                .andExpect(status().isOk());

        verify(spotifyService).getTopTracks(1, "medium_term");
        verify(spotifyService).getTopTracks(50, "medium_term");
    }
}

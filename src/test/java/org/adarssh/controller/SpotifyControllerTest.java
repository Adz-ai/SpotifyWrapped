package org.adarssh.controller;

import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.ExternalUrls;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
import org.adarssh.service.SpotifyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpotifyController.class)
class SpotifyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpotifyService spotifyService;

    @MockBean
    private OAuth2AuthorizedClientService authorizedClientService;

    @Test
    @WithMockUser
    void getTopTracksWithValidLimitReturnsOk() throws Exception {
        // given
        int limit = 5;
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        TrackDto track = new TrackDto("track1", "Test Track", album, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 1, List.of(track));

        when(spotifyService.getTopTracks(limit)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("tracks"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Track"));

        verify(spotifyService).getTopTracks(5);
    }

    @Test
    @WithMockUser
    void getTopTracksWithDefaultLimitReturnsOk() throws Exception {
        // given
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 0, List.of());

        when(spotifyService.getTopTracks(5)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("tracks"));

        verify(spotifyService).getTopTracks(5);
    }

    @Test
    @WithMockUser
    void getTopTracksWithLimit10ReturnsOk() throws Exception {
        // given
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 0, List.of());

        when(spotifyService.getTopTracks(10)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "10"))
                .andExpect(status().isOk());

        verify(spotifyService).getTopTracks(10);
    }

    @Test
    @WithMockUser
    void getTopTracksWithLimit50ReturnsOk() throws Exception {
        // given
        UserTopItemsResponse<TrackDto> mockResponse = new UserTopItemsResponse<>(
                "tracks", 0, List.of());

        when(spotifyService.getTopTracks(50)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "50"))
                .andExpect(status().isOk());

        verify(spotifyService).getTopTracks(50);
    }

    @Test
    @WithMockUser
    void getTopTracksWithInvalidLimitZeroReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "0"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt());
    }

    @Test
    @WithMockUser
    void getTopTracksWithInvalidLimitNegativeReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "-1"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt());
    }

    @Test
    @WithMockUser
    void getTopTracksWithInvalidLimitTooHighReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "51"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt());
    }

    @Test
    @WithMockUser
    void getTopTracksWithInvalidLimit100ReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "100"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt());
    }

    @Test
    void getTopTracksWithoutAuthenticationReturnsUnauthorized() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/tracks").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        verify(spotifyService, never()).getTopTracks(anyInt());
    }

    @Test
    @WithMockUser
    void getTopArtistsWithValidLimitReturnsOk() throws Exception {
        // given
        int limit = 10;
        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        UserTopItemsResponse<ArtistDto> mockResponse = new UserTopItemsResponse<>(
                "artists", 1, List.of(artist));

        when(spotifyService.getTopArtists(limit)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/artists").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("artists"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Artist"));

        verify(spotifyService).getTopArtists(10);
    }

    @Test
    @WithMockUser
    void getTopArtistsWithInvalidLimitReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/artists").param("limit", "0"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopArtists(anyInt());
    }

    @Test
    void getTopArtistsWithoutAuthenticationReturnsUnauthorized() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/artists").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        verify(spotifyService, never()).getTopArtists(anyInt());
    }

    @Test
    @WithMockUser
    void getTopAlbumsWithValidLimitReturnsOk() throws Exception {
        // given
        int limit = 5;
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        UserTopItemsResponse<AlbumDto> mockResponse = new UserTopItemsResponse<>(
                "albums", 1, List.of(album));

        when(spotifyService.getTopAlbums(limit)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/albums").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("albums"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.items[0].name").value("Test Album"));

        verify(spotifyService).getTopAlbums(5);
    }

    @Test
    @WithMockUser
    void getTopAlbumsWithInvalidLimitReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/albums").param("limit", "51"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopAlbums(anyInt());
    }

    @Test
    void getTopAlbumsWithoutAuthenticationReturnsUnauthorized() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/albums").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        verify(spotifyService, never()).getTopAlbums(anyInt());
    }

    @Test
    @WithMockUser
    void getTopGenresWithValidLimitReturnsOk() throws Exception {
        // given
        int limit = 10;
        UserTopItemsResponse<String> mockResponse = new UserTopItemsResponse<>(
                "genres", 3, List.of("rock", "pop", "indie"));

        when(spotifyService.getTopGenres(limit)).thenReturn(mockResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/top/genres").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("genres"))
                .andExpect(jsonPath("$.count").value(3))
                .andExpect(jsonPath("$.items[0]").value("rock"))
                .andExpect(jsonPath("$.items[1]").value("pop"))
                .andExpect(jsonPath("$.items[2]").value("indie"));

        verify(spotifyService).getTopGenres(10);
    }

    @Test
    @WithMockUser
    void getTopGenresWithInvalidLimitReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/genres").param("limit", "-5"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopGenres(anyInt());
    }

    @Test
    void getTopGenresWithoutAuthenticationReturnsUnauthorized() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/top/genres").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        verify(spotifyService, never()).getTopGenres(anyInt());
    }

    @Test
    @WithMockUser
    void getSpotifyWrappedWithValidLimitReturnsCompleteData() throws Exception {
        // given
        int limit = 5;
        TrackDto track = new TrackDto("track1", "Test Track", null, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));

        UserTopItemsResponse<TrackDto> tracksResponse = new UserTopItemsResponse<>(
                "tracks", 1, List.of(track));
        UserTopItemsResponse<ArtistDto> artistsResponse = new UserTopItemsResponse<>(
                "artists", 1, List.of(artist));
        UserTopItemsResponse<AlbumDto> albumsResponse = new UserTopItemsResponse<>(
                "albums", 1, List.of(album));
        UserTopItemsResponse<String> genresResponse = new UserTopItemsResponse<>(
                "genres", 1, List.of("rock"));

        when(spotifyService.getTopTracks(limit)).thenReturn(tracksResponse);
        when(spotifyService.getTopArtists(limit)).thenReturn(artistsResponse);
        when(spotifyService.getTopAlbums(limit)).thenReturn(albumsResponse);
        when(spotifyService.getTopGenres(limit)).thenReturn(genresResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/wrapped").param("limit", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topTracks.type").value("tracks"))
                .andExpect(jsonPath("$.topArtists.type").value("artists"))
                .andExpect(jsonPath("$.topAlbums.type").value("albums"))
                .andExpect(jsonPath("$.topGenres.type").value("genres"))
                .andExpect(jsonPath("$.topTracks.count").value(1))
                .andExpect(jsonPath("$.topArtists.count").value(1))
                .andExpect(jsonPath("$.topAlbums.count").value(1))
                .andExpect(jsonPath("$.topGenres.count").value(1));

        verify(spotifyService).getTopTracks(5);
        verify(spotifyService).getTopArtists(5);
        verify(spotifyService).getTopAlbums(5);
        verify(spotifyService).getTopGenres(5);
    }

    @Test
    @WithMockUser
    void getSpotifyWrappedWithDefaultLimitReturnsCompleteData() throws Exception {
        // given
        UserTopItemsResponse<TrackDto> tracksResponse = new UserTopItemsResponse<>(
                "tracks", 0, List.of());
        UserTopItemsResponse<ArtistDto> artistsResponse = new UserTopItemsResponse<>(
                "artists", 0, List.of());
        UserTopItemsResponse<AlbumDto> albumsResponse = new UserTopItemsResponse<>(
                "albums", 0, List.of());
        UserTopItemsResponse<String> genresResponse = new UserTopItemsResponse<>(
                "genres", 0, List.of());

        when(spotifyService.getTopTracks(5)).thenReturn(tracksResponse);
        when(spotifyService.getTopArtists(5)).thenReturn(artistsResponse);
        when(spotifyService.getTopAlbums(5)).thenReturn(albumsResponse);
        when(spotifyService.getTopGenres(5)).thenReturn(genresResponse);

        // when/then
        mockMvc.perform(get("/api/spotify/wrapped"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topTracks").exists())
                .andExpect(jsonPath("$.topArtists").exists())
                .andExpect(jsonPath("$.topAlbums").exists())
                .andExpect(jsonPath("$.topGenres").exists());

        verify(spotifyService).getTopTracks(5);
        verify(spotifyService).getTopArtists(5);
        verify(spotifyService).getTopAlbums(5);
        verify(spotifyService).getTopGenres(5);
    }

    @Test
    @WithMockUser
    void getSpotifyWrappedWithInvalidLimitReturnsBadRequest() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/wrapped").param("limit", "0"))
                .andExpect(status().isBadRequest());

        verify(spotifyService, never()).getTopTracks(anyInt());
        verify(spotifyService, never()).getTopArtists(anyInt());
        verify(spotifyService, never()).getTopAlbums(anyInt());
        verify(spotifyService, never()).getTopGenres(anyInt());
    }

    @Test
    void getSpotifyWrappedWithoutAuthenticationReturnsUnauthorized() throws Exception {
        // when/then
        mockMvc.perform(get("/api/spotify/wrapped").param("limit", "5"))
                .andExpect(status().isUnauthorized());

        verify(spotifyService, never()).getTopTracks(anyInt());
        verify(spotifyService, never()).getTopArtists(anyInt());
        verify(spotifyService, never()).getTopAlbums(anyInt());
        verify(spotifyService, never()).getTopGenres(anyInt());
    }
}

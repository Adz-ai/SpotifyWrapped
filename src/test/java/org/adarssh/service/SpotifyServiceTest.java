package org.adarssh.service;

import org.adarssh.config.SpotifyProperties;
import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.ExternalUrls;
import org.adarssh.dto.SpotifyPagedResponse;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
import org.adarssh.exception.SpotifyApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyServiceTest {

    @Mock
    private RestClient spotifyRestClient;

    @Mock
    private OAuth2TokenService oauth2TokenService;

    @Mock
    private SpotifyProperties spotifyProperties;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private SpotifyService spotifyService;

    @BeforeEach
    void setUp() {
        spotifyService = new SpotifyService(spotifyRestClient, oauth2TokenService, spotifyProperties);
    }

    @Test
    void getTopTracksWithValidLimitReturnsTracksSuccessfully() {
        // given
        int limit = 5;
        String accessToken = "test-access-token";

        AlbumDto album = new AlbumDto("album1", "Test Album", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com"));
        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock", "pop"),
                80, new ExternalUrls("https://spotify.com"), List.of());
        TrackDto track = new TrackDto("track1", "Test Track", album, List.of(artist),
                85, 180000, new ExternalUrls("https://spotify.com"));

        SpotifyPagedResponse<TrackDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(track), 1, 5, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<TrackDto> result = spotifyService.getTopTracks(limit, "medium_term");

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo("tracks");
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).name()).isEqualTo("Test Track");

        verify(oauth2TokenService).getUserAccessToken();
        verify(spotifyRestClient).get();
    }

    @Test
    void getTopTracksWithNullLimitUsesDefaultLimit() {
        // given
        String accessToken = "test-access-token";
        int defaultLimit = 10;

        SpotifyPagedResponse<TrackDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(), 0, 10, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyProperties.defaultLimit()).thenReturn(defaultLimit);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<TrackDto> result = spotifyService.getTopTracks(null, "medium_term");

        // then
        assertThat(result).isNotNull();
        verify(spotifyProperties).defaultLimit();
        verify(requestHeadersUriSpec).uri(any(java.util.function.Function.class));
    }

    @Test
    void getTopTracksWhenNullResponseThrowsException() {
        // given
        when(oauth2TokenService.getUserAccessToken()).thenReturn("token");
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> spotifyService.getTopTracks(5, "medium_term"))
                .isInstanceOf(SpotifyApiException.class)
                .hasMessageContaining("Failed to retrieve top tracks");
    }

    @Test
    void getTopTracksWhenApiThrowsExceptionThrowsSpotifyApiException() {
        // given
        when(oauth2TokenService.getUserAccessToken()).thenReturn("token");
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("API Error"));

        // when/then
        assertThatThrownBy(() -> spotifyService.getTopTracks(5, "medium_term"))
                .isInstanceOf(SpotifyApiException.class)
                .hasMessageContaining("Failed to fetch top tracks from Spotify API");
    }

    @Test
    void getTopArtistsWithValidLimitReturnsArtistsSuccessfully() {
        // given
        int limit = 10;
        String accessToken = "test-access-token";

        ArtistDto artist = new ArtistDto("artist1", "Test Artist", List.of("rock", "indie"),
                90, new ExternalUrls("https://spotify.com"), List.of());

        SpotifyPagedResponse<ArtistDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(artist), 1, 10, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<ArtistDto> result = spotifyService.getTopArtists(limit, "medium_term");

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo("artists");
        assertThat(result.count()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items().get(0).name()).isEqualTo("Test Artist");
        assertThat(result.items().get(0).genres()).containsExactly("rock", "indie");
    }

    @Test
    void getTopArtistsWhenNullResponseThrowsException() {
        // given
        when(oauth2TokenService.getUserAccessToken()).thenReturn("token");
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> spotifyService.getTopArtists(10, "medium_term"))
                .isInstanceOf(SpotifyApiException.class)
                .hasMessageContaining("Failed to retrieve top artists");
    }

    @Test
    void getTopArtistsWhenApiThrowsExceptionThrowsSpotifyApiException() {
        // given
        when(oauth2TokenService.getUserAccessToken()).thenReturn("token");
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenThrow(new RuntimeException("API Error"));

        // when/then
        assertThatThrownBy(() -> spotifyService.getTopArtists(10, "medium_term"))
                .isInstanceOf(SpotifyApiException.class)
                .hasMessageContaining("Failed to fetch top artists from Spotify API");
    }

    @Test
    void getTopAlbumsExtractsAlbumsFromTopTracks() {
        // given
        int limit = 5;
        String accessToken = "test-access-token";

        AlbumDto album1 = new AlbumDto("album1", "Album One", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com/album1"));
        AlbumDto album2 = new AlbumDto("album2", "Album Two", "album", "2024-02-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com/album2"));

        TrackDto track1 = new TrackDto("track1", "Track 1", album1, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        TrackDto track2 = new TrackDto("track2", "Track 2", album1, List.of(),
                80, 200000, new ExternalUrls("https://spotify.com"));
        TrackDto track3 = new TrackDto("track3", "Track 3", album2, List.of(),
                90, 220000, new ExternalUrls("https://spotify.com"));

        SpotifyPagedResponse<TrackDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(track1, track2, track3), 3, 5, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<AlbumDto> result = spotifyService.getTopAlbums(limit, "medium_term");

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo("albums");
        assertThat(result.count()).isEqualTo(2); // 2 distinct albums
        assertThat(result.items()).hasSize(2);
        assertThat(result.items()).extracting("name").containsExactly("Album One", "Album Two");
    }

    @Test
    void getTopAlbumsLimitsResultsToSpecifiedLimit() {
        // given
        int limit = 1;
        String accessToken = "test-access-token";

        AlbumDto album1 = new AlbumDto("album1", "Album One", "album", "2024-01-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com/album1"));
        AlbumDto album2 = new AlbumDto("album2", "Album Two", "album", "2024-02-01",
                List.of(), List.of(), new ExternalUrls("https://spotify.com/album2"));

        TrackDto track1 = new TrackDto("track1", "Track 1", album1, List.of(),
                85, 180000, new ExternalUrls("https://spotify.com"));
        TrackDto track2 = new TrackDto("track2", "Track 2", album2, List.of(),
                80, 200000, new ExternalUrls("https://spotify.com"));

        SpotifyPagedResponse<TrackDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(track1, track2), 2, 1, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<AlbumDto> result = spotifyService.getTopAlbums(limit, "medium_term");

        // then
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(1);
    }

    @Test
    void getTopGenresExtractsGenresFromTopArtists() {
        // given
        int limit = 5;
        String accessToken = "test-access-token";

        ArtistDto artist1 = new ArtistDto("artist1", "Artist 1", List.of("rock", "indie"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        ArtistDto artist2 = new ArtistDto("artist2", "Artist 2", List.of("pop", "rock"),
                85, new ExternalUrls("https://spotify.com"), List.of());
        ArtistDto artist3 = new ArtistDto("artist3", "Artist 3", List.of("jazz"),
                80, new ExternalUrls("https://spotify.com"), List.of());

        SpotifyPagedResponse<ArtistDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(artist1, artist2, artist3), 3, 5, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<String> result = spotifyService.getTopGenres(limit, "medium_term");

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo("genres");
        assertThat(result.count()).isEqualTo(4); // 4 distinct genres: rock, indie, pop, jazz
        assertThat(result.items()).hasSize(4);
        assertThat(result.items()).containsExactly("rock", "indie", "pop", "jazz");
    }

    @Test
    void getTopGenresLimitsResultsToSpecifiedLimit() {
        // given
        int limit = 2;
        String accessToken = "test-access-token";

        ArtistDto artist1 = new ArtistDto("artist1", "Artist 1", List.of("rock", "indie", "pop"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        ArtistDto artist2 = new ArtistDto("artist2", "Artist 2", List.of("jazz", "blues"),
                85, new ExternalUrls("https://spotify.com"), List.of());

        SpotifyPagedResponse<ArtistDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(artist1, artist2), 2, 2, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<String> result = spotifyService.getTopGenres(limit, "medium_term");

        // then
        assertThat(result).isNotNull();
        assertThat(result.items()).hasSize(2); // Limited to 2 genres
    }

    @Test
    void getTopGenresWithNullLimitUsesDefaultLimit() {
        // given
        String accessToken = "test-access-token";
        int defaultLimit = 10;

        ArtistDto artist = new ArtistDto("artist1", "Artist 1", List.of("rock"),
                90, new ExternalUrls("https://spotify.com"), List.of());

        SpotifyPagedResponse<ArtistDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(artist), 1, 10, 0, null, null);

        when(oauth2TokenService.getUserAccessToken()).thenReturn(accessToken);
        when(spotifyProperties.defaultLimit()).thenReturn(defaultLimit);
        when(spotifyRestClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(any(ParameterizedTypeReference.class))).thenReturn(mockResponse);

        // when
        UserTopItemsResponse<String> result = spotifyService.getTopGenres(null, "medium_term");

        // then
        assertThat(result).isNotNull();
        // defaultLimit() is called once in getTopGenres and once in getTopArtists (which is called by getTopGenres)
        verify(spotifyProperties, atLeast(1)).defaultLimit();
    }
}

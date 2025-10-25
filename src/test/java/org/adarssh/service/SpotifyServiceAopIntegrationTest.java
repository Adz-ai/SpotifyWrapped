package org.adarssh.service;

import org.adarssh.config.SpotifyProperties;
import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.ExternalUrls;
import org.adarssh.dto.SpotifyPagedResponse;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for SpotifyService methods that use AOP proxy for self-invocation.
 * These tests require Spring Boot context to test @Cacheable, @Retry, and @CircuitBreaker annotations.
 */
@SpringBootTest
@ActiveProfiles("test")
class SpotifyServiceAopIntegrationTest {

    @Autowired
    private SpotifyService spotifyService;

    @MockBean(name = "spotifyRestClient")
    private RestClient spotifyRestClient;

    @MockBean
    private OAuth2TokenService oauth2TokenService;

    @MockBean
    private SpotifyProperties spotifyProperties;

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

        // Mock RestClient chain
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
            mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec =
            mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

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

        // Mock RestClient chain
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
            mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec =
            mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

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

        ArtistDto artist1 = new ArtistDto("artist1", "Artist One", List.of("rock", "indie"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        ArtistDto artist2 = new ArtistDto("artist2", "Artist Two", List.of("pop", "rock"),
                85, new ExternalUrls("https://spotify.com"), List.of());

        SpotifyPagedResponse<ArtistDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(artist1, artist2), 2, 5, 0, null, null);

        // Mock RestClient chain
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
            mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec =
            mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

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
        assertThat(result.count()).isEqualTo(3); // 3 distinct genres: rock, indie, pop
        assertThat(result.items()).hasSize(3);
        assertThat(result.items()).containsExactlyInAnyOrder("rock", "indie", "pop");
    }

    @Test
    void getTopGenresLimitsResultsToSpecifiedLimit() {
        // given
        int limit = 2;
        String accessToken = "test-access-token";

        ArtistDto artist1 = new ArtistDto("artist1", "Artist One", List.of("rock", "indie", "alternative"),
                90, new ExternalUrls("https://spotify.com"), List.of());
        ArtistDto artist2 = new ArtistDto("artist2", "Artist Two", List.of("pop", "dance"),
                85, new ExternalUrls("https://spotify.com"), List.of());

        SpotifyPagedResponse<ArtistDto> mockResponse = new SpotifyPagedResponse<>(
                List.of(artist1, artist2), 2, 2, 0, null, null);

        // Mock RestClient chain
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
            mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec =
            mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

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
        assertThat(result.items()).hasSize(2); // Limited to 2
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

        // Mock RestClient chain
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec =
            mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec =
            mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

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
        assertThat(result.type()).isEqualTo("genres");
    }
}

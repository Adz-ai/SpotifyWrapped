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
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
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

        // Execute the lambda to trigger defaultLimit() call
        doAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
            UriBuilder mockUriBuilder = mock(UriBuilder.class);
            when(mockUriBuilder.path(anyString())).thenReturn(mockUriBuilder);
            when(mockUriBuilder.queryParam(anyString(), (Object[]) any())).thenReturn(mockUriBuilder);
            when(mockUriBuilder.build()).thenReturn(URI.create("http://api.spotify.com/v1/me/top/tracks"));
            uriFunction.apply(mockUriBuilder);
            return requestHeadersSpec;
        }).when(requestHeadersUriSpec).uri(any(Function.class));

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

        // Actually execute the URI builder function to get coverage
        doAnswer(invocation -> {
            Function<UriBuilder, URI> uriFunction = invocation.getArgument(0);
            UriBuilder mockUriBuilder = mock(UriBuilder.class);
            when(mockUriBuilder.path(anyString())).thenReturn(mockUriBuilder);
            when(mockUriBuilder.queryParam(anyString(), any(Object[].class))).thenReturn(mockUriBuilder);
            when(mockUriBuilder.build()).thenReturn(URI.create("http://api.spotify.com/me/top/artists"));
            uriFunction.apply(mockUriBuilder);  // Execute the lambda
            return requestHeadersSpec;
        }).when(requestHeadersUriSpec).uri(any(Function.class));

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
    void getCurrentUsernameReturnsUsernameWhenAuthenticated() {
        // given
        org.springframework.security.core.Authentication authentication =
                org.mockito.Mockito.mock(org.springframework.security.core.Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        // when
        String username = spotifyService.getCurrentUsername();

        // then
        assertThat(username).isEqualTo("testuser");

        // cleanup
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUsernameReturnsAnonymousWhenNotAuthenticated() {
        // given
        org.springframework.security.core.context.SecurityContextHolder.clearContext();

        // when
        String username = spotifyService.getCurrentUsername();

        // then
        assertThat(username).isEqualTo("anonymous");
    }

    @Test
    void getCurrentUsernameReturnsAnonymousWhenAuthenticationIsNull() {
        // given
        org.springframework.security.core.context.SecurityContextHolder.getContext()
                .setAuthentication(null);

        // when
        String username = spotifyService.getCurrentUsername();

        // then
        assertThat(username).isEqualTo("anonymous");

        // cleanup
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void getTopTracksFallbackReturnsEmptyResponse() throws Exception {
        // given
        Integer limit = 5;
        String timeRange = "medium_term";
        Exception testException = new RuntimeException("Spotify API unavailable");

        // Use reflection to access private fallback method
        var method = SpotifyService.class.getDeclaredMethod(
                "getTopTracksFallback", Integer.class, String.class, Exception.class);
        method.setAccessible(true);

        // when
        @SuppressWarnings("unchecked")
        UserTopItemsResponse<TrackDto> result =
                (UserTopItemsResponse<TrackDto>) method.invoke(spotifyService, limit, timeRange, testException);

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo("tracks");
        assertThat(result.count()).isEqualTo(0);
        assertThat(result.items()).isEmpty();
    }

    @Test
    void getTopArtistsFallbackReturnsEmptyResponse() throws Exception {
        // given
        Integer limit = 5;
        String timeRange = "medium_term";
        Exception testException = new RuntimeException("Spotify API unavailable");

        // Use reflection to access private fallback method
        var method = SpotifyService.class.getDeclaredMethod(
                "getTopArtistsFallback", Integer.class, String.class, Exception.class);
        method.setAccessible(true);

        // when
        @SuppressWarnings("unchecked")
        UserTopItemsResponse<ArtistDto> result =
                (UserTopItemsResponse<ArtistDto>) method.invoke(spotifyService, limit, timeRange, testException);

        // then
        assertThat(result).isNotNull();
        assertThat(result.type()).isEqualTo("artists");
        assertThat(result.count()).isEqualTo(0);
        assertThat(result.items()).isEmpty();
    }

}

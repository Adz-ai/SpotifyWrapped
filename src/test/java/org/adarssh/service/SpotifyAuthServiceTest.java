package org.adarssh.service;

import org.adarssh.config.SpotifyProperties;
import org.adarssh.dto.SpotifyTokenResponse;
import org.adarssh.exception.SpotifyAuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyAuthServiceTest {

    @Mock
    private RestClient authRestClient;

    @Mock
    private SpotifyProperties spotifyProperties;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private SpotifyAuthService authService;

    @BeforeEach
    void setUp() {
        authService = new SpotifyAuthService(authRestClient, spotifyProperties);
    }

    @Test
    void obtainAccessTokenWithValidCredentialsReturnsToken() {
        // given
        String clientId = "test-client-id";
        String clientSecret = "test-client-secret";
        String expectedToken = "test-access-token-12345";

        SpotifyTokenResponse tokenResponse = new SpotifyTokenResponse(
                expectedToken, "Bearer", 3600);

        when(spotifyProperties.clientId()).thenReturn(clientId);
        when(spotifyProperties.clientSecret()).thenReturn(clientSecret);
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyTokenResponse.class)).thenReturn(tokenResponse);

        // when
        String actualToken = authService.obtainAccessToken();

        // then
        assertThat(actualToken).isEqualTo(expectedToken);
        verify(authRestClient).post();
        verify(spotifyProperties).clientId();
        verify(spotifyProperties).clientSecret();
    }

    @Test
    void obtainAccessTokenWhenResponseIsNullThrowsException() {
        // given
        String clientId = "test-client-id";
        String clientSecret = "test-client-secret";

        when(spotifyProperties.clientId()).thenReturn(clientId);
        when(spotifyProperties.clientSecret()).thenReturn(clientSecret);
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyTokenResponse.class)).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> authService.obtainAccessToken())
                .isInstanceOf(SpotifyAuthenticationException.class)
                .hasMessageContaining("Failed to obtain access token: null response");
    }

    @Test
    void obtainAccessTokenWhenTokenIsNullThrowsException() {
        // given
        String clientId = "test-client-id";
        String clientSecret = "test-client-secret";

        SpotifyTokenResponse tokenResponse = new SpotifyTokenResponse(
                null, "Bearer", 3600);

        when(spotifyProperties.clientId()).thenReturn(clientId);
        when(spotifyProperties.clientSecret()).thenReturn(clientSecret);
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyTokenResponse.class)).thenReturn(tokenResponse);

        // when/then
        assertThatThrownBy(() -> authService.obtainAccessToken())
                .isInstanceOf(SpotifyAuthenticationException.class)
                .hasMessageContaining("Failed to obtain access token: null response");
    }

    @Test
    void obtainAccessTokenWhenApiThrowsExceptionThrowsSpotifyAuthenticationException() {
        // given
        String clientId = "test-client-id";
        String clientSecret = "test-client-secret";

        when(spotifyProperties.clientId()).thenReturn(clientId);
        when(spotifyProperties.clientSecret()).thenReturn(clientSecret);
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenThrow(new RuntimeException("Network error"));

        // when/then
        assertThatThrownBy(() -> authService.obtainAccessToken())
                .isInstanceOf(SpotifyAuthenticationException.class)
                .hasMessageContaining("Failed to authenticate with Spotify API")
                .hasCauseInstanceOf(RuntimeException.class);
    }

    @Test
    void obtainAccessTokenSendsCorrectAuthorizationHeader() {
        // given
        String clientId = "myClientId";
        String clientSecret = "mySecret";
        String expectedToken = "access-token";

        SpotifyTokenResponse tokenResponse = new SpotifyTokenResponse(
                expectedToken, "Bearer", 3600);

        when(spotifyProperties.clientId()).thenReturn(clientId);
        when(spotifyProperties.clientSecret()).thenReturn(clientSecret);
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyTokenResponse.class)).thenReturn(tokenResponse);

        // when
        String token = authService.obtainAccessToken();

        // then
        assertThat(token).isEqualTo(expectedToken);
        verify(requestBodySpec).header(eq("Authorization"), startsWith("Basic "));
    }

    @Test
    void obtainAccessTokenSendsClientCredentialsGrantType() {
        // given
        String clientId = "test-id";
        String clientSecret = "test-secret";
        String expectedToken = "token-123";

        SpotifyTokenResponse tokenResponse = new SpotifyTokenResponse(
                expectedToken, "Bearer", 3600);

        when(spotifyProperties.clientId()).thenReturn(clientId);
        when(spotifyProperties.clientSecret()).thenReturn(clientSecret);
        when(authRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(MultiValueMap.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyTokenResponse.class)).thenReturn(tokenResponse);

        // when
        authService.obtainAccessToken();

        // then
        verify(requestBodySpec).body(argThat((MultiValueMap<String, String> map) ->
                map != null && "client_credentials".equals(map.getFirst("grant_type"))));
    }
}

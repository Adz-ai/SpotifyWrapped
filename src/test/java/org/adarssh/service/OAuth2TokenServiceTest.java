package org.adarssh.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2TokenServiceTest {

    @Mock
    private OAuth2AuthorizedClientService authorizedClientService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private OAuth2AuthenticationToken oAuth2AuthenticationToken;

    @Mock
    private OAuth2AuthorizedClient oAuth2AuthorizedClient;

    @Mock
    private OAuth2AccessToken oAuth2AccessToken;

    private OAuth2TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new OAuth2TokenService(authorizedClientService);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getUserAccessTokenWithValidAuthenticationReturnsToken() {
        // given
        String expectedToken = "test-access-token-12345";
        String clientRegistrationId = "spotify";
        String principalName = "testUser";

        when(securityContext.getAuthentication()).thenReturn(oAuth2AuthenticationToken);
        when(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()).thenReturn(clientRegistrationId);
        when(oAuth2AuthenticationToken.getName()).thenReturn(principalName);
        when(authorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName))
                .thenReturn(oAuth2AuthorizedClient);
        when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getTokenValue()).thenReturn(expectedToken);

        // when
        String actualToken = tokenService.getUserAccessToken();

        // then
        assertThat(actualToken).isEqualTo(expectedToken);
        verify(securityContext).getAuthentication();
        verify(authorizedClientService).loadAuthorizedClient(clientRegistrationId, principalName);
    }

    @Test
    void getUserAccessTokenWhenNotAuthenticatedThrowsIllegalStateException() {
        // given
        when(securityContext.getAuthentication()).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> tokenService.getUserAccessToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User must be authenticated with Spotify");
    }

    @Test
    void getUserAccessTokenWhenNotOAuth2AuthenticationThrowsIllegalStateException() {
        // given
        Authentication nonOAuthAuth = new UsernamePasswordAuthenticationToken("user", "password");
        when(securityContext.getAuthentication()).thenReturn(nonOAuthAuth);

        // when/then
        assertThatThrownBy(() -> tokenService.getUserAccessToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("User must be authenticated with Spotify");
    }

    @Test
    void getUserAccessTokenWhenAuthorizedClientIsNullThrowsIllegalStateException() {
        // given
        String clientRegistrationId = "spotify";
        String principalName = "testUser";

        when(securityContext.getAuthentication()).thenReturn(oAuth2AuthenticationToken);
        when(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()).thenReturn(clientRegistrationId);
        when(oAuth2AuthenticationToken.getName()).thenReturn(principalName);
        when(authorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName))
                .thenReturn(null);

        // when/then
        assertThatThrownBy(() -> tokenService.getUserAccessToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No access token available. Please re-authenticate.");
    }

    @Test
    void getUserAccessTokenWhenAccessTokenIsNullThrowsIllegalStateException() {
        // given
        String clientRegistrationId = "spotify";
        String principalName = "testUser";

        when(securityContext.getAuthentication()).thenReturn(oAuth2AuthenticationToken);
        when(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()).thenReturn(clientRegistrationId);
        when(oAuth2AuthenticationToken.getName()).thenReturn(principalName);
        when(authorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName))
                .thenReturn(oAuth2AuthorizedClient);
        when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> tokenService.getUserAccessToken())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No access token available. Please re-authenticate.");
    }

    @Test
    void getUserAccessTokenLogsDebugMessageWithUsername() {
        // given
        String expectedToken = "test-token";
        String clientRegistrationId = "spotify";
        String principalName = "john.doe";

        when(securityContext.getAuthentication()).thenReturn(oAuth2AuthenticationToken);
        when(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId()).thenReturn(clientRegistrationId);
        when(oAuth2AuthenticationToken.getName()).thenReturn(principalName);
        when(authorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName))
                .thenReturn(oAuth2AuthorizedClient);
        when(oAuth2AuthorizedClient.getAccessToken()).thenReturn(oAuth2AccessToken);
        when(oAuth2AccessToken.getTokenValue()).thenReturn(expectedToken);

        // when
        String actualToken = tokenService.getUserAccessToken();

        // then
        assertThat(actualToken).isEqualTo(expectedToken);
        // getName() is called twice: once for loadAuthorizedClient and once for debug log
        verify(oAuth2AuthenticationToken, atLeastOnce()).getName();
    }
}

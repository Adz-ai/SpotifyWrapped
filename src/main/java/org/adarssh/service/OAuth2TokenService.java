package org.adarssh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Service for retrieving OAuth2 access tokens for authenticated users
 */
@Service
public class OAuth2TokenService {

    private static final Logger log = LoggerFactory.getLogger(OAuth2TokenService.class);

    private final OAuth2AuthorizedClientService authorizedClientService;

    public OAuth2TokenService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    /**
     * Get the access token for the currently authenticated user
     * @return OAuth2 access token
     * @throws IllegalStateException if user is not authenticated or token is not available
     */
    public String getUserAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            log.error("User is not authenticated with OAuth2");
            throw new IllegalStateException("User must be authenticated with Spotify");
        }

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        if (client == null || client.getAccessToken() == null) {
            log.error("No authorized client or access token found for user");
            throw new IllegalStateException("No access token available. Please re-authenticate.");
        }

        log.debug("Retrieved access token for user: {}", oauthToken.getName());
        return client.getAccessToken().getTokenValue();
    }
}

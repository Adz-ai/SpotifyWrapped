package org.adarssh.service;

import org.adarssh.config.SpotifyProperties;
import org.adarssh.dto.SpotifyTokenResponse;
import org.adarssh.exception.SpotifyAuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Service for handling Spotify authentication
 */
@Service
public class SpotifyAuthService {

    private static final Logger log = LoggerFactory.getLogger(SpotifyAuthService.class);

    private final RestClient authRestClient;
    private final SpotifyProperties properties;

    public SpotifyAuthService(
            @Qualifier("spotifyAuthRestClient") RestClient authRestClient,
            SpotifyProperties properties) {
        this.authRestClient = authRestClient;
        this.properties = properties;
    }

    /**
     * Obtains an access token using client credentials flow
     */
    public String obtainAccessToken() {
        try {
            log.debug("Requesting Spotify access token");

            var credentials = properties.clientId() + ":" + properties.clientSecret();
            var encodedCredentials = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "client_credentials");

            var response = authRestClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "Basic " + encodedCredentials)
                    .body(formData)
                    .retrieve()
                    .body(SpotifyTokenResponse.class);

            if (response == null || response.accessToken() == null) {
                throw new SpotifyAuthenticationException("Failed to obtain access token: null response");
            }

            log.debug("Successfully obtained Spotify access token");
            return response.accessToken();

        } catch (SpotifyAuthenticationException e) {
            // Re-throw our own exception without wrapping
            throw e;
        } catch (Exception e) {
            log.error("Failed to authenticate with Spotify API", e);
            throw new SpotifyAuthenticationException("Failed to authenticate with Spotify API", e);
        }
    }
}

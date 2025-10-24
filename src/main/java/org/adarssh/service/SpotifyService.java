package org.adarssh.service;

import org.adarssh.config.SpotifyProperties;
import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.SpotifyPagedResponse;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
import org.adarssh.exception.SpotifyApiException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.stream.Collectors;

/**
 * Service for interacting with Spotify API
 */
@Service
public class SpotifyService {

    private static final Logger log = LoggerFactory.getLogger(SpotifyService.class);

    private final RestClient spotifyRestClient;
    private final OAuth2TokenService oauth2TokenService;
    private final SpotifyProperties properties;

    /**
     * Constructs a new SpotifyService with required dependencies.
     *
     * @param spotifyRestClient the configured RestClient for Spotify API calls
     * @param oauth2TokenService the service for managing OAuth2 tokens
     * @param properties the Spotify configuration properties
     */
    public SpotifyService(
            @Qualifier("spotifyRestClient") RestClient spotifyRestClient,
            OAuth2TokenService oauth2TokenService,
            SpotifyProperties properties) {
        this.spotifyRestClient = spotifyRestClient;
        this.oauth2TokenService = oauth2TokenService;
        this.properties = properties;
    }

    /**
     * Get user's top tracks.
     * Results are cached for 5 minutes per user and limit combination.
     * Implements retry (3 attempts) and circuit breaker patterns for resilience.
     *
     * @param limit the maximum number of tracks to return
     * @return the user's top tracks
     */
    @Cacheable(value = "topTracks", key = "#root.target.getCurrentUsername() + '-' + #limit")
    @Retry(name = "spotifyApi", fallbackMethod = "getTopTracksFallback")
    @CircuitBreaker(name = "spotifyApi", fallbackMethod = "getTopTracksFallback")
    public UserTopItemsResponse<TrackDto> getTopTracks(Integer limit) {
        log.debug("Fetching top {} tracks", limit);
        var accessToken = oauth2TokenService.getUserAccessToken();

        try {
            var response = spotifyRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/me/top/tracks")
                            .queryParam("limit", limit != null ? limit : properties.defaultLimit())
                            .build())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<SpotifyPagedResponse<TrackDto>>() { });

            if (response == null || response.items() == null) {
                throw new SpotifyApiException("Failed to retrieve top tracks: null response", 500);
            }

            return new UserTopItemsResponse<>("tracks", response.items().size(), response.items());

        } catch (Exception e) {
            log.error("Failed to fetch top tracks", e);
            throw new SpotifyApiException("Failed to fetch top tracks from Spotify API", 500, e);
        }
    }

    /**
     * Get user's top artists.
     * Results are cached for 5 minutes per user and limit combination.
     * Implements retry (3 attempts) and circuit breaker patterns for resilience.
     *
     * @param limit the maximum number of artists to return
     * @return the user's top artists
     */
    @Cacheable(value = "topArtists", key = "#root.target.getCurrentUsername() + '-' + #limit")
    @Retry(name = "spotifyApi", fallbackMethod = "getTopArtistsFallback")
    @CircuitBreaker(name = "spotifyApi", fallbackMethod = "getTopArtistsFallback")
    public UserTopItemsResponse<ArtistDto> getTopArtists(Integer limit) {
        log.debug("Fetching top {} artists", limit);
        var accessToken = oauth2TokenService.getUserAccessToken();

        try {
            var response = spotifyRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/me/top/artists")
                            .queryParam("limit", limit != null ? limit : properties.defaultLimit())
                            .build())
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<SpotifyPagedResponse<ArtistDto>>() { });

            if (response == null || response.items() == null) {
                throw new SpotifyApiException("Failed to retrieve top artists: null response", 500);
            }

            return new UserTopItemsResponse<>("artists", response.items().size(), response.items());

        } catch (Exception e) {
            log.error("Failed to fetch top artists", e);
            throw new SpotifyApiException("Failed to fetch top artists from Spotify API", 500, e);
        }
    }

    /**
     * Get top albums from user's top tracks.
     * Uses AOP proxy to ensure caching works on internal call to getTopTracks().
     *
     * @param limit the maximum number of albums to return
     * @return the user's top albums derived from top tracks
     */
    public UserTopItemsResponse<AlbumDto> getTopAlbums(Integer limit) {
        log.debug("Fetching top albums from top tracks");
        Integer maxSize = limit != null ? limit : properties.defaultLimit();
        // Get the Spring AOP proxy to ensure @Cacheable, @Retry, @CircuitBreaker work
        SpotifyService proxy = (SpotifyService) AopContext.currentProxy();
        var topTracks = proxy.getTopTracks(maxSize);

        var albums = topTracks.items().stream()
                .map(TrackDto::album)
                .distinct()
                .limit(maxSize)
                .collect(Collectors.toList());

        return new UserTopItemsResponse<>("albums", albums.size(), albums);
    }

    /**
     * Get top genres from user's top artists.
     * Uses AOP proxy to ensure caching works on internal call to getTopArtists().
     *
     * @param limit the maximum number of genres to return
     * @return the user's top genres derived from top artists
     */
    public UserTopItemsResponse<String> getTopGenres(Integer limit) {
        log.debug("Fetching top genres from top artists");
        Integer maxSize = limit != null ? limit : properties.defaultLimit();
        // Get the Spring AOP proxy to ensure @Cacheable, @Retry, @CircuitBreaker work
        SpotifyService proxy = (SpotifyService) AopContext.currentProxy();
        var topArtists = proxy.getTopArtists(maxSize);

        var genres = topArtists.items().stream()
                .flatMap(artist -> artist.genres().stream())
                .distinct()
                .limit(maxSize)
                .collect(Collectors.toList());

        return new UserTopItemsResponse<>("genres", genres.size(), genres);
    }

    /**
     * Get the current authenticated username for cache key generation.
     *
     * @return the current username or "anonymous" if not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "anonymous";
    }

    /**
     * Fallback method for getTopTracks when Spotify API is unavailable.
     *
     * @param limit the limit parameter
     * @param ex the exception that triggered the fallback
     * @return an empty response with error information
     */
    private UserTopItemsResponse<TrackDto> getTopTracksFallback(Integer limit, Exception ex) {
        log.error("Fallback triggered for getTopTracks. Returning empty response.", ex);
        return new UserTopItemsResponse<>("tracks", 0, java.util.Collections.emptyList());
    }

    /**
     * Fallback method for getTopArtists when Spotify API is unavailable.
     *
     * @param limit the limit parameter
     * @param ex the exception that triggered the fallback
     * @return an empty response with error information
     */
    private UserTopItemsResponse<ArtistDto> getTopArtistsFallback(Integer limit, Exception ex) {
        log.error("Fallback triggered for getTopArtists. Returning empty response.", ex);
        return new UserTopItemsResponse<>("artists", 0, java.util.Collections.emptyList());
    }
}

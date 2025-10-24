package org.adarssh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main Spring Boot application class for Spotify Wrapped API
 * <p>
 * This application provides REST endpoints to fetch user's top Spotify items:
 * - Top tracks
 * - Top artists
 * - Top albums (derived from top tracks)
 * - Top genres (derived from top artists)
 * <p>
 * API Endpoints:
 * - GET /api/health - Health check
 * - GET /api/spotify/top/tracks - Get top tracks
 * - GET /api/spotify/top/artists - Get top artists
 * - GET /api/spotify/top/albums - Get top albums
 * - GET /api/spotify/top/genres - Get top genres
 * - GET /api/spotify/wrapped - Get all top items at once
 * <p>
 * All endpoints accept an optional 'limit' query parameter (default: 5)
 */
@SpringBootApplication
@EnableCaching
public class SpotifyWrappedApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpotifyWrappedApplication.class, args);
    }
}

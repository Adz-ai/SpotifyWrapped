package org.adarssh.controller;

import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
import org.adarssh.service.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Spotify API endpoints
 */
@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {

    private static final Logger log = LoggerFactory.getLogger(SpotifyController.class);

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    /**
     * Get user's top tracks
     * @param limit Optional limit (default: 5)
     */
    @GetMapping("/top/tracks")
    public ResponseEntity<UserTopItemsResponse<TrackDto>> getTopTracks(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        log.info("GET /api/spotify/top/tracks - limit: {}", limit);
        var response = spotifyService.getTopTracks(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's top artists
     * @param limit Optional limit (default: 5)
     */
    @GetMapping("/top/artists")
    public ResponseEntity<UserTopItemsResponse<ArtistDto>> getTopArtists(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        log.info("GET /api/spotify/top/artists - limit: {}", limit);
        var response = spotifyService.getTopArtists(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's top albums
     * @param limit Optional limit (default: 5)
     */
    @GetMapping("/top/albums")
    public ResponseEntity<UserTopItemsResponse<AlbumDto>> getTopAlbums(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        log.info("GET /api/spotify/top/albums - limit: {}", limit);
        var response = spotifyService.getTopAlbums(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's top genres
     * @param limit Optional limit (default: 5)
     */
    @GetMapping("/top/genres")
    public ResponseEntity<UserTopItemsResponse<String>> getTopGenres(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        log.info("GET /api/spotify/top/genres - limit: {}", limit);
        var response = spotifyService.getTopGenres(limit);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all user's top items at once (wrapped summary)
     */
    @GetMapping("/wrapped")
    public ResponseEntity<SpotifyWrappedResponse> getSpotifyWrapped(
            @RequestParam(required = false, defaultValue = "5") Integer limit) {
        log.info("GET /api/spotify/wrapped - limit: {}", limit);

        var tracks = spotifyService.getTopTracks(limit);
        var artists = spotifyService.getTopArtists(limit);
        var albums = spotifyService.getTopAlbums(limit);
        var genres = spotifyService.getTopGenres(limit);

        var wrapped = new SpotifyWrappedResponse(tracks, artists, albums, genres);
        return ResponseEntity.ok(wrapped);
    }

    /**
     * DTO for the complete Spotify Wrapped response
     */
    public record SpotifyWrappedResponse(
            UserTopItemsResponse<TrackDto> topTracks,
            UserTopItemsResponse<ArtistDto> topArtists,
            UserTopItemsResponse<AlbumDto> topAlbums,
            UserTopItemsResponse<String> topGenres
    ) { }
}

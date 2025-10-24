package org.adarssh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.adarssh.dto.AlbumDto;
import org.adarssh.dto.ArtistDto;
import org.adarssh.dto.TrackDto;
import org.adarssh.dto.UserTopItemsResponse;
import org.adarssh.service.SpotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for Spotify API endpoints
 */
@RestController
@RequestMapping("/api/spotify")
@Validated
@Tag(name = "Spotify", description = "Endpoints for accessing your Spotify listening data")
public class SpotifyController {

    private static final Logger log = LoggerFactory.getLogger(SpotifyController.class);

    private final SpotifyService spotifyService;

    public SpotifyController(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    /**
     * Get user's top tracks.
     *
     * @param limit Number of tracks to return (1-50, default: 5)
     * @param timeRange Time range for calculation (short_term, medium_term, long_term)
     * @return User's top tracks
     */
    @Operation(
        summary = "Get top tracks",
        description = "Retrieves the user's top tracks based on their listening history. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved top tracks",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserTopItemsResponse.class),
                examples = @ExampleObject(
                    name = "Top Tracks Example",
                    value = """
                        {
                          "type": "tracks",
                          "count": 3,
                          "items": [
                            {
                              "id": "track123",
                              "name": "Bohemian Rhapsody",
                              "popularity": 95,
                              "durationMs": 354320,
                              "album": {
                                "id": "album456",
                                "name": "A Night at the Opera",
                                "releaseDate": "1975-11-21"
                              },
                              "artists": [
                                {"id": "artist789", "name": "Queen"}
                              ]
                            }
                          ]
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Not authenticated",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"error": "Unauthorized", "message": "Authentication required"}
                    """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid parameters",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                    {"error": "Bad Request", "message": "Limit must be between 1 and 50"}
                    """)
            )
        )
    })
    @GetMapping("/top/tracks")
    public ResponseEntity<UserTopItemsResponse<TrackDto>> getTopTracks(
            @Parameter(description = "Number of tracks to return (1-50)", example = "10")
            @RequestParam(required = false, defaultValue = "5")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit must be at most 50")
            Integer limit,
            @Parameter(
                    description = "Time range: short_term (4 weeks), medium_term (6 months), long_term (all time)",
                    example = "medium_term")
            @RequestParam(required = false, defaultValue = "medium_term")
            String timeRange) {
        log.info("GET /api/spotify/top/tracks - limit: {}, timeRange: {}", limit, timeRange);
        var response = spotifyService.getTopTracks(limit, timeRange);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's top artists.
     *
     * @param limit Number of artists to return (1-50, default: 5)
     * @param timeRange Time range for calculation (short_term, medium_term, long_term)
     * @return User's top artists
     */
    @Operation(
        summary = "Get top artists",
        description = "Retrieves the user's top artists based on their listening history. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved top artists",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserTopItemsResponse.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/top/artists")
    public ResponseEntity<UserTopItemsResponse<ArtistDto>> getTopArtists(
            @Parameter(description = "Number of artists to return (1-50)", example = "10")
            @RequestParam(required = false, defaultValue = "5")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit must be at most 50")
            Integer limit,
            @Parameter(
                    description = "Time range: short_term (4 weeks), medium_term (6 months), long_term (all time)",
                    example = "medium_term")
            @RequestParam(required = false, defaultValue = "medium_term")
            String timeRange) {
        log.info("GET /api/spotify/top/artists - limit: {}, timeRange: {}", limit, timeRange);
        var response = spotifyService.getTopArtists(limit, timeRange);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's top albums.
     *
     * @param limit Number of albums to return (1-50, default: 5)
     * @param timeRange Time range for calculation (short_term, medium_term, long_term)
     * @return User's top albums
     */
    @Operation(
        summary = "Get top albums",
        description = "Retrieves the user's top albums derived from their top tracks. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved top albums",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserTopItemsResponse.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/top/albums")
    public ResponseEntity<UserTopItemsResponse<AlbumDto>> getTopAlbums(
            @Parameter(description = "Number of albums to return (1-50)", example = "10")
            @RequestParam(required = false, defaultValue = "5")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit must be at most 50")
            Integer limit,
            @Parameter(
                    description = "Time range: short_term (4 weeks), medium_term (6 months), long_term (all time)",
                    example = "medium_term")
            @RequestParam(required = false, defaultValue = "medium_term")
            String timeRange) {
        log.info("GET /api/spotify/top/albums - limit: {}, timeRange: {}", limit, timeRange);
        var response = spotifyService.getTopAlbums(limit, timeRange);
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's top genres.
     *
     * @param limit Number of genres to return (1-50, default: 5)
     * @param timeRange Time range for calculation (short_term, medium_term, long_term)
     * @return User's top genres
     */
    @Operation(
        summary = "Get top genres",
        description = "Retrieves the user's top genres derived from their top artists. Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved top genres",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserTopItemsResponse.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/top/genres")
    public ResponseEntity<UserTopItemsResponse<String>> getTopGenres(
            @Parameter(description = "Number of genres to return (1-50)", example = "10")
            @RequestParam(required = false, defaultValue = "5")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit must be at most 50")
            Integer limit,
            @Parameter(
                    description = "Time range: short_term (4 weeks), medium_term (6 months), long_term (all time)",
                    example = "medium_term")
            @RequestParam(required = false, defaultValue = "medium_term")
            String timeRange) {
        log.info("GET /api/spotify/top/genres - limit: {}, timeRange: {}", limit, timeRange);
        var response = spotifyService.getTopGenres(limit, timeRange);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all user's top items at once (wrapped summary).
     *
     * @param limit Number of items per category to return (1-50, default: 5)
     * @param timeRange Time range for calculation (short_term, medium_term, long_term)
     * @return Complete wrapped data with top tracks, artists, albums, and genres
     */
    @Operation(
        summary = "Get Spotify Wrapped",
        description = "Retrieves all top items (tracks, artists, albums, genres) in a single request. "
            + "Requires authentication."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved complete wrapped data",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = SpotifyWrappedResponse.class)
            )
        ),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/wrapped")
    public ResponseEntity<SpotifyWrappedResponse> getSpotifyWrapped(
            @Parameter(description = "Number of items per category to return (1-50)", example = "10")
            @RequestParam(required = false, defaultValue = "5")
            @Min(value = 1, message = "Limit must be at least 1")
            @Max(value = 50, message = "Limit must be at most 50")
            Integer limit,
            @Parameter(
                    description = "Time range: short_term (4 weeks), medium_term (6 months), long_term (all time)",
                    example = "medium_term")
            @RequestParam(required = false, defaultValue = "medium_term")
            String timeRange) {
        log.info("GET /api/spotify/wrapped - limit: {}, timeRange: {}", limit, timeRange);

        var tracks = spotifyService.getTopTracks(limit, timeRange);
        var artists = spotifyService.getTopArtists(limit, timeRange);
        var albums = spotifyService.getTopAlbums(limit, timeRange);
        var genres = spotifyService.getTopGenres(limit, timeRange);

        var wrapped = new SpotifyWrappedResponse(tracks, artists, albums, genres);
        return ResponseEntity.ok(wrapped);
    }

    /**
     * DTO for the complete Spotify Wrapped response.
     */
    @Schema(description = "Complete Spotify Wrapped data with all top items")
    public record SpotifyWrappedResponse(
            @Schema(description = "User's top tracks") UserTopItemsResponse<TrackDto> topTracks,
            @Schema(description = "User's top artists") UserTopItemsResponse<ArtistDto> topArtists,
            @Schema(description = "User's top albums") UserTopItemsResponse<AlbumDto> topAlbums,
            @Schema(description = "User's top genres") UserTopItemsResponse<String> topGenres
    ) { }
}

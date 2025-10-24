package org.adarssh.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home controller for the application.
 */
@RestController
@Tag(name = "Home", description = "Authentication status and API information")
public class HomeController {

    /**
     * Get authentication status and available endpoints.
     *
     * @param principal OAuth2 principal if authenticated
     * @return Authentication status and endpoint information
     */
    @Operation(
        summary = "Get API home information",
        description = "Returns authentication status and available API endpoints. Does not require authentication."
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully retrieved API information",
        content = @Content(
            mediaType = "application/json",
            examples = {
                @ExampleObject(
                    name = "Authenticated",
                    value = """
                        {
                          "authenticated": true,
                          "user": "username",
                          "message": "You are logged in! Try: /api/spotify/wrapped",
                          "endpoints": {
                            "wrapped": "/api/spotify/wrapped",
                            "topTracks": "/api/spotify/top/tracks",
                            "topArtists": "/api/spotify/top/artists",
                            "topAlbums": "/api/spotify/top/albums",
                            "topGenres": "/api/spotify/top/genres",
                            "logout": "/logout"
                          }
                        }
                        """
                ),
                @ExampleObject(
                    name = "Not Authenticated",
                    value = """
                        {
                          "authenticated": false,
                          "message": "Welcome to Spotify Wrapped API! Please log in with Spotify.",
                          "loginUrl": "/oauth2/authorization/spotify"
                        }
                        """
                )
            }
        )
    )
    @GetMapping("/api/")
    public Map<String, Object> home(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal != null) {
            response.put("authenticated", true);
            response.put("user", principal.getAttribute("display_name"));
            response.put("message", "You are logged in! Try: /api/spotify/wrapped");
            response.put("endpoints", Map.of(
                "wrapped", "/api/spotify/wrapped",
                "topTracks", "/api/spotify/top/tracks",
                "topArtists", "/api/spotify/top/artists",
                "topAlbums", "/api/spotify/top/albums",
                "topGenres", "/api/spotify/top/genres",
                "logout", "/logout"
            ));
        } else {
            response.put("authenticated", false);
            response.put("message", "Welcome to Spotify Wrapped API! Please log in with Spotify.");
            response.put("loginUrl", "/oauth2/authorization/spotify");
        }

        return response;
    }
}

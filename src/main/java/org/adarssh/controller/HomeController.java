package org.adarssh.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home controller for the application
 */
@RestController
public class HomeController {

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

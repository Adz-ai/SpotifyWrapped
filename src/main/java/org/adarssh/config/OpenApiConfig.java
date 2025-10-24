package org.adarssh.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures OpenAPI documentation.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI spotifyWrappedOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("Spotify Wrapped API")
                .description("""
                    A modern REST API to access your Spotify listening habits.

                    ## Features
                    - OAuth2 authentication with Spotify
                    - Get your top tracks, artists, albums, and genres
                    - Input validation and error handling
                    - Health monitoring endpoints

                    ## Authentication
                    Before using the API endpoints, you need to authenticate with Spotify:
                    1. Visit http://127.0.0.1:8080/oauth2/authorization/spotify
                    2. Login with your Spotify account
                    3. Grant permissions
                    4. You'll be redirected to the frontend with an active session

                    ## Usage
                    All endpoints require an authenticated session. Use the limit parameter to control
                    the number of results (default: 10, max: 50).
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Spotify Wrapped")
                    .url("https://github.com/adarssh/SpotifyWrapped"))
                .license(new License()
                    .name("Educational Use")
                    .url("https://github.com/adarssh/SpotifyWrapped")))
            .servers(List.of(
                new Server()
                    .url("http://127.0.0.1:8080")
                    .description("Local development server")
            ));
    }
}

package org.adarssh.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for RestClient beans
 */
@Configuration
@EnableConfigurationProperties(SpotifyProperties.class)
public class RestClientConfig {

    @Bean
    public RestClient spotifyRestClient(SpotifyProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Bean
    public RestClient spotifyAuthRestClient(SpotifyProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.authUrl())
                .build();
    }
}

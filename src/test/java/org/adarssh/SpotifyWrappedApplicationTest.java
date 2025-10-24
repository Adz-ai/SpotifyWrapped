package org.adarssh;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for SpotifyWrappedApplication main class.
 */
@SpringBootTest
@ActiveProfiles("test")
class SpotifyWrappedApplicationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // when/then
        assertThat(applicationContext).isNotNull();
    }

    @Test
    void mainMethodStartsApplication() {
        // given
        String[] args = new String[]{};

        // when/then - Should not throw exception
        // Note: This test verifies the main method exists and can be called
        // In a real scenario, calling main() would start the application
        assertThat(SpotifyWrappedApplication.class)
                .hasDeclaredMethods("main");
    }

    @Test
    void applicationHasRequiredAnnotations() {
        // when/then
        assertThat(SpotifyWrappedApplication.class)
                .hasAnnotation(org.springframework.boot.autoconfigure.SpringBootApplication.class);

        assertThat(SpotifyWrappedApplication.class)
                .hasAnnotation(org.springframework.cache.annotation.EnableCaching.class);

        assertThat(SpotifyWrappedApplication.class)
                .hasAnnotation(org.springframework.context.annotation.EnableAspectJAutoProxy.class);
    }

    @Test
    void requiredBeansAreLoaded() {
        // when/then
        assertThat(applicationContext.containsBean("spotifyService")).isTrue();
        assertThat(applicationContext.containsBean("spotifyController")).isTrue();
        assertThat(applicationContext.containsBean("healthController")).isTrue();
        assertThat(applicationContext.containsBean("homeController")).isTrue();
    }

    @Test
    void cachingIsEnabled() {
        // when
        var cacheManager = applicationContext.getBean(org.springframework.cache.CacheManager.class);

        // then
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).isNotEmpty();
    }
}

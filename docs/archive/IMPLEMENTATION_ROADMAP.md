# Implementation Roadmap

## Current Status: ✅ Basic Setup Complete

The application is modernized with Java 21, Spring Boot 3.x, and has a working REST API architecture.

### What Works Now ✅
- ✅ Modern Java 21 + Spring Boot 3.x
- ✅ REST API with proper endpoints
- ✅ Client Credentials authentication
- ✅ Health check endpoint
- ✅ Error handling and logging
- ✅ Configuration management
- ✅ Clean architecture

### What Doesn't Work Yet ❌
- ❌ `/api/spotify/top/tracks` - Returns 403 (requires user auth)
- ❌ `/api/spotify/top/artists` - Returns 403 (requires user auth)
- ❌ `/api/spotify/top/albums` - Returns 403 (requires user auth)
- ❌ `/api/spotify/top/genres` - Returns 403 (requires user auth)
- ❌ `/api/spotify/wrapped` - Returns 403 (requires user auth)

**Why?** These endpoints require user authorization (OAuth Authorization Code Flow), but the app currently only implements Client Credentials Flow.

---

## Phase 1: Make It Work with User Authorization 🎯

### Priority: HIGH
### Estimated Time: 2-3 hours

#### 1. Add OAuth2 Dependencies

**File:** `build.gradle.kts`

```kotlin
dependencies {
    // Add these lines:
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Keep existing dependencies...
}
```

#### 2. Configure OAuth2 Client

**File:** `src/main/resources/application.yml`

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          spotify:
            client-id: ${SPOTIFY_CLIENT_ID}
            client-secret: ${SPOTIFY_CLIENT_SECRET}
            scope:
              - user-top-read
              - user-read-recently-played
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        provider:
          spotify:
            authorization-uri: https://accounts.spotify.com/authorize
            token-uri: https://accounts.spotify.com/api/token
            user-info-uri: https://api.spotify.com/v1/me
            user-name-attribute: id
```

#### 3. Create Security Configuration

**New File:** `src/main/java/org/adarssh/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/health").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .defaultSuccessUrl("/api/spotify/wrapped", true)
            );
        return http.build();
    }
}
```

#### 4. Create OAuth2 Service to Get User Token

**New File:** `src/main/java/org/adarssh/service/OAuth2TokenService.java`

```java
@Service
public class OAuth2TokenService {

    public String getUserAccessToken(OAuth2AuthenticationToken authentication) {
        OAuth2AuthorizedClient client = authorizedClientService
            .loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
            );
        return client.getAccessToken().getTokenValue();
    }
}
```

#### 5. Update SpotifyService to Use User Token

**File:** `src/main/java/org/adarssh/service/SpotifyService.java`

Change methods to accept user token or retrieve from security context.

#### 6. Update Spotify Dashboard

- Add redirect URI: `http://localhost:8080/login/oauth2/code/spotify`

---

## Phase 2: Improve User Experience 🎨

### Priority: MEDIUM
### Estimated Time: 2-4 hours

- [ ] Add a landing page with "Login with Spotify" button
- [ ] Store tokens in session/database for persistence
- [ ] Add token refresh logic
- [ ] Add user profile endpoint
- [ ] Add logout functionality
- [ ] Add better error pages

---

## Phase 3: Add More Features 🚀

### Priority: LOW
### Estimated Time: 4-8 hours

- [ ] Add recently played tracks endpoint
- [ ] Add time range parameter (short_term, medium_term, long_term)
- [ ] Add caching for API responses
- [ ] Add rate limiting
- [ ] Add analytics/statistics calculations
- [ ] Generate shareable "Wrapped" summary images
- [ ] Add playlist creation from top tracks

---

## Phase 4: Frontend Development 💻

### Priority: MEDIUM (after Phase 1)
### Estimated Time: 1-2 weeks

### Option A: Traditional Web App
- Add Thymeleaf templates
- Create dashboard with visualizations
- Add charts (Chart.js or similar)

### Option B: Modern SPA
- Create React/Vue/Angular frontend
- Connect to REST API
- Modern UI with animations

### Option C: Mobile App
- Create React Native or Flutter app
- Use the REST API as backend

---

## Quick Start: Get User Auth Working Now

### The Fastest Path (30 minutes)

**Option 1: Add Spring Security OAuth2** (Recommended)
1. Add dependencies from Phase 1 Step 1
2. Add configuration from Phase 1 Step 2
3. Update Spotify Dashboard redirect URI
4. Restart app and visit `http://localhost:8080`
5. You'll be redirected to Spotify login
6. After login, endpoints will work!

**Option 2: Manual Token (For Testing)**
1. Go to https://developer.spotify.com/console/get-current-user-top-artists/
2. Click "Get Token"
3. Select scopes: `user-top-read`
4. Copy token
5. Use in curl: `curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/...`
6. Modify SpotifyService to accept token as parameter

---

## Resources & References

### Documentation
- [Spotify Authorization Guide](https://developer.spotify.com/documentation/web-api/concepts/authorization)
- [Spring Security OAuth2 Client](https://docs.spring.io/spring-security/reference/servlet/oauth2/client/index.html)
- [Spotify API Reference](https://developer.spotify.com/documentation/web-api/reference)

### Example Projects
- [Spring Boot OAuth2 Example](https://github.com/spring-projects/spring-security-samples)
- [Spotify Web API Java](https://github.com/spotify-web-api-java/spotify-web-api-java)

### Tools
- [Postman Collection for Spotify API](https://www.postman.com/spotify-web-api)
- [JWT.io](https://jwt.io/) - Decode and inspect tokens

---

## Need Help?

1. **Read the detailed setup guide**: [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md)
2. **Check Spring Security docs**: OAuth2 Client configuration
3. **Test with Spotify Console**: Get familiar with the API
4. **Check logs**: Look for detailed error messages

---

## Contributing

If you implement any of these phases, consider:
1. Writing tests for new functionality
2. Updating documentation
3. Adding logging for debugging
4. Following existing code patterns

---

**Last Updated**: 2025-01-15
**Current Version**: 1.0.0 (Basic Setup Complete)
**Next Milestone**: Phase 1 - User Authorization

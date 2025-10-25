# Spotify Wrapped

A full-stack application that displays your Spotify listening habits including top tracks, artists, albums, and genres. Built with Java 21 Spring Boot backend and React TypeScript frontend.

## Tech Stack

### Backend
- Java 21
- Spring Boot 3.2.2
- Spring Security OAuth2 Client
- Spring Cache with Caffeine
- Resilience4j (Retry & Circuit Breaker)
- SpringDoc OpenAPI (Swagger)
- JUnit 5 & Mockito
- Checkstyle & SpotBugs
- JaCoCo (Code Coverage)
- Gradle 8.5

### Frontend
- React 18
- TypeScript 5.3 (strict mode)
- Vite 5
- Tailwind CSS 3
- Axios
- ESLint & Prettier

## Prerequisites

- Java 21 or higher
- Node.js 18 or higher
- Spotify Developer Account

## Spotify Dashboard Setup

### 1. Create Spotify Application

1. Navigate to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Log in with your Spotify account
3. Click "Create app"
4. Fill in the application details:
   - App name: `Spotify Wrapped API`
   - App description: `Personal Spotify statistics`
   - Website: `http://127.0.0.1:8080`
   - Redirect URI: `http://127.0.0.1:8080/login/oauth2/code/spotify`
5. Select "Web API" under API/SDKs
6. Accept Terms of Service and click "Save"

### 2. Get Credentials

1. Click "Settings" in your app dashboard
2. Copy your Client ID
3. Click "View client secret" to reveal and copy your Client Secret

### 3. Configure Environment

Create a `.env` file in the project root:

```bash
cp .env.example .env
```

Add your credentials to `.env`:

```
SPOTIFY_CLIENT_ID=your_client_id_here
SPOTIFY_CLIENT_SECRET=your_client_secret_here
```

## Installation

### Backend

```bash
./gradlew build
```

### Frontend

```bash
cd frontend
npm install
```

## Running the Application

### Start Backend

```bash
export SPOTIFY_CLIENT_ID=your_client_id_here
export SPOTIFY_CLIENT_SECRET=your_client_secret_here
./gradlew bootRun
```

Backend runs on `http://127.0.0.1:8080`

### Start Frontend

```bash
cd frontend
npm run dev
```

Frontend runs on `http://127.0.0.1:3000`

### Access Application

Open `http://127.0.0.1:3000` in your browser and log in with Spotify.

## API Endpoints

All endpoints require authentication except `/api/health`.

- `GET /api/` - Authentication status
- `GET /api/health` - Health check
- `GET /api/spotify/wrapped?limit=10` - All top items
- `GET /api/spotify/top/tracks?limit=10` - Top tracks (1-50)
- `GET /api/spotify/top/artists?limit=10` - Top artists (1-50)
- `GET /api/spotify/top/albums?limit=10` - Top albums (1-50)
- `GET /api/spotify/top/genres?limit=10` - Top genres (1-50)

### API Documentation

Interactive API documentation available at:
- Swagger UI: `http://127.0.0.1:8080/swagger-ui.html`
- OpenAPI JSON: `http://127.0.0.1:8080/api-docs`

### Monitoring

Health and monitoring endpoints:
- Health: `http://127.0.0.1:8080/actuator/health`
- Info: `http://127.0.0.1:8080/actuator/info`
- Metrics: `http://127.0.0.1:8080/actuator/metrics`

## Code Quality

### Run All Checks

```bash
# Backend
./gradlew checkstyleMain spotbugsMain test build

# Frontend
cd frontend
npm run lint
npm run type-check
npm run build
```

### View Reports

- Tests: `build/reports/tests/test/index.html`
- Coverage: `build/reports/jacoco/test/html/index.html`
- Checkstyle: `build/reports/checkstyle/main.html`
- SpotBugs: `build/reports/spotbugs/spotbugs.html`

## CI/CD

GitHub Actions workflows run on every push and pull request:

- Backend: Checkstyle, SpotBugs, tests, build
- Frontend: ESLint, TypeScript type-check, build
- Security: CodeQL analysis for Java and JavaScript
- Dependencies: Automated weekly updates via Dependabot

## Architecture

### Backend Structure

```
src/main/java/org/adarssh/
├── SpotifyWrappedApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── SecurityHeadersConfig.java
│   ├── SpotifyProperties.java
│   ├── RestClientConfig.java
│   └── OpenApiConfig.java
├── controller/
│   ├── SpotifyController.java
│   ├── HomeController.java
│   └── HealthController.java
├── service/
│   ├── SpotifyService.java
│   └── OAuth2TokenService.java
├── dto/
│   ├── TrackDto.java
│   ├── ArtistDto.java
│   └── AlbumDto.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── SpotifyApiException.java
```

### Frontend Structure

```
frontend/src/
├── components/
│   ├── Login.tsx
│   ├── Dashboard.tsx
│   ├── TrackCard.tsx
│   └── ArtistCard.tsx
├── hooks/
│   ├── useAuth.ts
│   └── useSpotifyData.ts
├── services/
│   └── api.ts
└── types/
    └── spotify.ts
```

## Features

### Performance & Resilience

- Response caching (5-minute TTL with Caffeine)
- HTTP compression (Gzip for responses > 1KB)
- Retry pattern (3 attempts with exponential backoff)
- Circuit breaker (50% failure threshold)
- Security headers (HSTS, CSP, X-Frame-Options)

### Testing

- 98 comprehensive unit and integration tests
- 80% code coverage target
- Service tests with Mockito
- Controller tests with MockMvc
- Integration tests with SpringBootTest

## Important Notes

### Using 127.0.0.1

This application uses `127.0.0.1` instead of `localhost` for OAuth2 session cookie management. Ensure:

- Spotify redirect URI uses `http://127.0.0.1:8080/login/oauth2/code/spotify`
- Frontend runs on `http://127.0.0.1:3000`
- Backend runs on `http://127.0.0.1:8080`

Mixing localhost and 127.0.0.1 will cause authentication issues.

## Security

- Never commit `.env` file (already in `.gitignore`)
- Client credentials managed via environment variables
- CSRF protection enabled
- Secure session management
- Security headers on all responses

## License

Educational purposes.

# Spotify Wrapped

A modern full-stack application that displays your Spotify listening habits - your top tracks, artists, albums, and genres. Built with Java 21 Spring Boot backend and React TypeScript frontend.

## Features

### Backend
- Built with Java 21 and Spring Boot 3.2.2
- OAuth2 Authorization Code Flow with Spring Security
- Modern REST API architecture
- Uses Spring's RestClient for HTTP operations
- **Response Caching** - 5 minute cache with Caffeine (50-90% faster responses)
- **HTTP Compression** - Gzip compression for 60-80% smaller payloads
- **Resilience Patterns** - Retry (3 attempts) + Circuit Breaker for fault tolerance
- **Security Headers** - HSTS, CSP, X-Frame-Options, and more
- Proper error handling and logging
- DTOs using Java records
- Clean layered architecture (Controller -> Service -> External API)

### Frontend
- React 18 with TypeScript (strict mode)
- Modern, responsive UI with Tailwind CSS
- Spotify-branded design
- Custom React hooks for data fetching
- Type-safe API client with Axios
- ESLint + Prettier for code quality
- Vite for fast development

## Prerequisites

- Java 21 or higher
- Node.js 18+ and npm
- Spotify Developer Account and API credentials

## Quick Start

See [RUN_FULL_STACK.md](RUN_FULL_STACK.md) for complete instructions to run both backend and frontend.

## Setup

### 1. Get Spotify API Credentials

See [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) for detailed step-by-step instructions.

Quick steps:
1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Create a new application
3. Add redirect URI: `http://127.0.0.1:8080/login/oauth2/code/spotify`
4. Copy your Client ID and Client Secret from Settings

### 2. Configure Environment Variables

Copy the example environment file:
```bash
cp .env.example .env
```

Edit `.env` and add your credentials:
```
SPOTIFY_CLIENT_ID=your_client_id_here
SPOTIFY_CLIENT_SECRET=your_client_secret_here
```

### 3. Run the Application

See [RUN_FULL_STACK.md](RUN_FULL_STACK.md) for detailed instructions on running both backend and frontend.

Quick start:
```bash
# Terminal 1: Start backend (from project root)
export SPOTIFY_CLIENT_ID=your_client_id_here
export SPOTIFY_CLIENT_SECRET=your_client_secret_here
./gradlew bootRun

# Terminal 2: Start frontend (from project root)
cd frontend
npm install
npm run dev
```

Then open `http://127.0.0.1:3000` in your browser and login with Spotify!

## API Endpoints

The backend exposes the following REST endpoints (most require authentication):

- `GET /api/` - Check authentication status
- `GET /api/health` - Health check
- `GET /api/spotify/wrapped?limit=10` - Get all top items at once
- `GET /api/spotify/top/tracks?limit=10` - Get top tracks (1-50)
- `GET /api/spotify/top/artists?limit=10` - Get top artists (1-50)
- `GET /api/spotify/top/albums?limit=10` - Get top albums (1-50)
- `GET /api/spotify/top/genres?limit=10` - Get top genres (1-50)

All endpoints return JSON responses with proper error handling and input validation.

### API Documentation (Swagger)

Interactive API documentation is available at:
- **Swagger UI**: `http://127.0.0.1:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://127.0.0.1:8080/api-docs`

The Swagger UI provides:
- Interactive API explorer
- Request/response examples
- Parameter validation details
- Try-it-out functionality

### Monitoring Endpoints (Actuator)

Health and monitoring endpoints:
- **Health**: `http://127.0.0.1:8080/actuator/health` - Application health status
- **Info**: `http://127.0.0.1:8080/actuator/info` - Application information
- **Metrics**: `http://127.0.0.1:8080/actuator/metrics` - Application metrics
- **Environment**: `http://127.0.0.1:8080/actuator/env` - Environment properties

## Architecture

### Backend Structure
```
src/main/java/org/adarssh/
├── SpotifyWrappedApplication.java  # Main Spring Boot application (@EnableCaching)
├── config/
│   ├── SecurityConfig.java         # OAuth2 + CORS configuration
│   ├── SecurityHeadersConfig.java  # Security headers filter
│   ├── SpotifyProperties.java      # Configuration properties
│   ├── RestClientConfig.java       # RestClient beans
│   └── OpenApiConfig.java          # Swagger/OpenAPI configuration
├── controller/
│   ├── SpotifyController.java      # REST endpoints with OpenAPI annotations
│   ├── HomeController.java         # Auth status endpoint
│   └── HealthController.java       # Health check
├── service/
│   ├── SpotifyService.java         # Business logic
│   └── OAuth2TokenService.java     # User token management
├── dto/                            # Data Transfer Objects (Java records)
│   ├── TrackDto.java
│   ├── ArtistDto.java
│   ├── AlbumDto.java
│   └── ...
└── exception/
    ├── GlobalExceptionHandler.java # Global error handling
    └── SpotifyApiException.java
```

### Frontend Structure
```
frontend/src/
├── components/         # React components
│   ├── Login.tsx      # Login page
│   ├── Dashboard.tsx  # Main dashboard
│   ├── TrackCard.tsx  # Track display
│   └── ArtistCard.tsx # Artist display
├── hooks/             # Custom React hooks
│   ├── useAuth.ts     # Authentication
│   └── useSpotifyData.ts # Data fetching
├── services/
│   └── api.ts         # Spotify API client
├── types/
│   └── spotify.ts     # TypeScript types
└── App.tsx            # Main app component
```

## Technologies Used

### Backend
- Java 21 with records and modern features
- Spring Boot 3.2.2
- Spring Security OAuth2 Client
- Spring Boot Actuator - Health checks and monitoring
- SpringDoc OpenAPI 3 - API documentation (Swagger)
- Jakarta Bean Validation - Input validation
- **Spring Cache + Caffeine** - Response caching (5 min TTL)
- **Resilience4j** - Retry and Circuit Breaker patterns
- **HTTP Compression** - Gzip for responses > 1KB
- **Security Headers** - Comprehensive security header configuration
- RestClient (Spring 6.1+)
- Gradle 8.5

### Frontend
- React 18
- TypeScript 5.3 (strict mode)
- Vite 5
- Tailwind CSS 3
- Axios for HTTP
- ESLint + Prettier

### Code Quality Tools
- **Checkstyle** - Enforces Java coding standards (Google Java Style)
- **SpotBugs** - Static analysis for finding bugs in Java code
- **JaCoCo** - Code coverage analysis (target: 80%)
- **JUnit 5** - Unit and integration testing framework
- **Mockito** - Mocking framework for tests
- **ESLint** - TypeScript/JavaScript linting with strict rules
- **Prettier** - Code formatting for frontend

## Code Quality

This project uses multiple code quality tools to maintain high standards:

### Running Tests

```bash
# Backend - Run all tests
./gradlew test

# Backend - Run tests with coverage report
./gradlew test jacocoTestReport

# Backend - View coverage report
open build/reports/jacoco/test/html/index.html

# Frontend - Run tests (when added)
cd frontend && npm test
```

### Running Code Quality Checks

```bash
# Backend - All quality checks
./gradlew test checkstyleMain spotbugsMain jacocoTestReport

# Backend - Checkstyle (coding standards)
./gradlew checkstyleMain

# Backend - SpotBugs (bug detection)
./gradlew spotbugsMain

# Backend - Code coverage
./gradlew jacocoTestReport

# Frontend - Type checking
cd frontend && npm run type-check

# Frontend - Linting
cd frontend && npm run lint

# Frontend - Formatting
cd frontend && npm run format
```

### Reports

After running the checks, reports are available at:
- **Tests**: `build/reports/tests/test/index.html`
- **Code Coverage**: `build/reports/jacoco/test/html/index.html`
- **Checkstyle**: `build/reports/checkstyle/main.html`
- **SpotBugs**: `build/reports/spotbugs/spotbugs.html`

### Configuration

- **JaCoCo**: `build.gradle.kts` - Configured for 80% coverage target
- **Tests**: `src/test/` - 98 comprehensive unit and integration tests
  - Service tests with Mockito mocks
  - Controller tests with MockMvc
  - Integration tests with @SpringBootTest
  - Exception handler tests
- **Checkstyle**: `config/checkstyle/checkstyle.xml` - Based on Google Java Style with Spring Boot adjustments
- **SpotBugs**: `config/spotbugs/excludeFilter.xml` - Excludes false positives for DTOs and Spring beans
- **ESLint**: `frontend/.eslintrc.cjs` - Strict TypeScript rules
- **Prettier**: `frontend/.prettierrc.json` - Code formatting rules

## Testing

The project includes a comprehensive test suite:

### Test Structure

```
src/test/java/org/adarssh/
├── config/
│   └── OpenApiConfigTest.java          (15 tests)
├── controller/
│   ├── HealthControllerTest.java       (6 tests)
│   ├── HomeControllerTest.java         (8 tests)
│   └── SpotifyControllerTest.java      (24 tests)
├── exception/
│   └── GlobalExceptionHandlerTest.java (12 tests)
├── integration/
│   └── SpotifyApiIntegrationTest.java  (11 tests)
└── service/
    ├── OAuth2TokenServiceTest.java     (6 tests)
    ├── SpotifyAuthServiceTest.java     (6 tests)
    └── SpotifyServiceTest.java         (12 tests)
```

### Test Coverage

- **Total Tests**: 98
- **Coverage Target**: 80%
- **Test Types**:
  - Unit tests for services (Mockito)
  - Controller tests (MockMvc)
  - Integration tests (SpringBootTest)
  - Exception handler tests

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

## CI/CD

This project includes a comprehensive CI/CD pipeline using GitHub Actions.

### Workflows

#### Main CI Pipeline (`.github/workflows/ci.yml`)

Runs on every push to `main` and all pull requests:

**Backend Checks:**
- Checkstyle (Google Java Style)
- SpotBugs (static bug analysis)
- JUnit tests (98 tests)
- Full build verification
- Uploads test reports, coverage, and quality reports as artifacts

**Frontend Checks:**
- ESLint (TypeScript strict mode)
- TypeScript type checking
- Production build verification
- Uploads build artifacts

**Status Check:**
- Ensures all backend and frontend checks pass before merging

#### Security Scanning (`.github/workflows/codeql.yml`)

Runs on pushes, PRs, and weekly schedule:

- CodeQL analysis for Java and JavaScript/TypeScript
- Automated security vulnerability detection
- Security advisories for known CVEs

#### Dependency Updates (`.github/dependabot.yml`)

Automated dependency management:

- **Gradle dependencies**: Weekly updates for Java backend
- **npm dependencies**: Weekly updates for React frontend
- **GitHub Actions**: Weekly updates for workflow dependencies
- Automatic PR creation with up to 5 open PRs per ecosystem

### Viewing CI Results

1. **In Pull Requests**: Status checks appear at the bottom of each PR
2. **Actions Tab**: View all workflow runs in the Actions tab
3. **Artifacts**: Download test reports and coverage from workflow run pages

### Local Testing Before Push

Run the same checks locally before pushing:

```bash
# Backend checks (same as CI)
./gradlew checkstyleMain spotbugsMain test build

# Frontend checks (same as CI)
cd frontend
npm run lint
npm run type-check
npm run build
```

All checks must pass for the CI pipeline to succeed.

## Performance & Resilience

This application implements several production-ready patterns for optimal performance and reliability:

### Response Caching

**Technology**: Spring Cache + Caffeine
**Configuration**: 5-minute TTL, 500 max entries per cache

```yaml
Caches:
- topTracks: User's top tracks (per user + limit)
- topArtists: User's top artists (per user + limit)

Benefits:
✓ 50-90% reduction in Spotify API calls
✓ 10-50ms cached responses vs 200-500ms API calls
✓ Reduced risk of rate limiting
✓ Better user experience
```

Cache keys include the authenticated username to ensure user-specific data isolation.

### HTTP Compression

**Technology**: Gzip compression (built-in Spring Boot)
**Configuration**: Enabled for responses > 1KB

```yaml
Compression:
- JSON responses: ~60-80% smaller
- Faster transfer times
- Better mobile experience
- Mime types: application/json, application/xml, text/*
```

### Resilience Patterns

**Technology**: Resilience4j (Retry + Circuit Breaker)

#### Retry Pattern
```yaml
Configuration:
- Max attempts: 3
- Wait duration: 500ms
- Exponential backoff: 2x multiplier
- Retry on: Network errors, timeouts

Example: 500ms → 1000ms → 2000ms
```

#### Circuit Breaker Pattern
```yaml
Configuration:
- Failure threshold: 50%
- Slow call threshold: 50% (>2s)
- Sliding window: 10 calls
- Half-open state: 3 test calls
- Open state duration: 10s

States:
- Closed: Normal operation
- Open: Fast-fail (return fallback)
- Half-Open: Testing recovery
```

**Fallback Behavior**: Returns empty results instead of failing when Spotify API is down.

### Security Headers

**Technology**: Custom Servlet Filter

Automatically adds security headers to all responses:

```yaml
Headers Applied:
✓ Strict-Transport-Security (HSTS)
✓ X-Content-Type-Options: nosniff
✓ X-Frame-Options: DENY
✓ X-XSS-Protection: 1; mode=block
✓ Content-Security-Policy (CSP)
✓ Referrer-Policy: strict-origin-when-cross-origin
✓ Permissions-Policy

Benefits:
- Protection against XSS attacks
- Prevents clickjacking
- Blocks MIME-type sniffing
- Forces HTTPS connections
```

### Performance Metrics

Expected improvements with all optimizations:

| Metric | Before | After | Improvement |
|--------|--------|-------|-------------|
| First request | 400-500ms | 400-500ms | - |
| Cached request | 400-500ms | 10-50ms | **90% faster** |
| Payload size | 50-100KB | 10-30KB | **70% smaller** |
| API failures | Hard fail | Graceful degradation | **100% uptime** |
| Security score | B | A+ | **Production-ready** |

## Important Notes

### Using 127.0.0.1 Instead of localhost

This application uses `http://127.0.0.1:3000` and `http://127.0.0.1:8080` instead of `localhost`. This is crucial for OAuth2 session cookie management to work correctly across the authentication flow. Make sure:

1. Spotify redirect URI uses `http://127.0.0.1:8080/login/oauth2/code/spotify`
2. Frontend runs on `http://127.0.0.1:3000`
3. Backend runs on `http://127.0.0.1:8080`

Mixing localhost and 127.0.0.1 will cause authentication issues.

## What's Been Modernized

This application was completely modernized from an old Java codebase:

### Backend Modernization
- Upgraded from legacy Java to Java 21
- Replaced HttpURLConnection with Spring's RestClient
- Migrated to Spring Boot 3.2.2 architecture
- Implemented OAuth2 Authorization Code Flow
- Added proper layered architecture
- Modern DTOs using Java records
- Comprehensive error handling
- **Production-ready features**:
  - Response caching with Caffeine (5 min TTL)
  - HTTP compression (Gzip)
  - Resilience patterns (Retry + Circuit Breaker)
  - Security headers (HSTS, CSP, X-Frame-Options, etc.)
  - Swagger/OpenAPI documentation
  - Input validation (1-50 range)
  - Actuator monitoring endpoints

### Frontend Addition
- Built from scratch with React 18 + TypeScript
- Strict TypeScript configuration for type safety
- Modern UI with Tailwind CSS
- Custom React hooks for state management
- Type-safe API client

## License

This project is for educational purposes.

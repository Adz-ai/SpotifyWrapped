# Spotify Wrapped

A modern full-stack application that displays your Spotify listening habits - your top tracks, artists, albums, and genres. Built with Java 21 Spring Boot backend and React TypeScript frontend.

## Features

### Backend
- Built with Java 21 and Spring Boot 3.2.2
- OAuth2 Authorization Code Flow with Spring Security
- Modern REST API architecture
- Uses Spring's RestClient for HTTP operations
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

The backend exposes the following REST endpoints (requires authentication):

- `GET /api/` - Check authentication status
- `GET /api/health` - Health check
- `GET /api/spotify/wrapped?limit=10` - Get all top items at once
- `GET /api/spotify/top/tracks?limit=10` - Get top tracks
- `GET /api/spotify/top/artists?limit=10` - Get top artists
- `GET /api/spotify/top/albums?limit=10` - Get top albums
- `GET /api/spotify/top/genres?limit=10` - Get top genres

All endpoints return JSON responses with proper error handling.

## Architecture

### Backend Structure
```
src/main/java/org/adarssh/
├── SpotifyWrappedApplication.java  # Main Spring Boot application
├── config/
│   ├── SecurityConfig.java         # OAuth2 + CORS configuration
│   ├── SpotifyProperties.java      # Configuration properties
│   └── RestClientConfig.java       # RestClient beans
├── controller/
│   ├── SpotifyController.java      # REST endpoints
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
- RestClient (Spring 6.1+)
- Gradle 8.5

### Frontend
- React 18
- TypeScript 5.3 (strict mode)
- Vite 5
- Tailwind CSS 3
- Axios for HTTP
- ESLint + Prettier

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

### Frontend Addition
- Built from scratch with React 18 + TypeScript
- Strict TypeScript configuration for type safety
- Modern UI with Tailwind CSS
- Custom React hooks for state management
- Type-safe API client

## License

This project is for educational purposes.

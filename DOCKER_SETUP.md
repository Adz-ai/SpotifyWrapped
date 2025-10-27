# Docker Setup Guide

This guide explains how to run the Spotify Wrapped application using Docker and Docker Compose.

## Prerequisites

- Docker Engine 20.10+ or Docker Desktop
- Docker Compose V2 (comes with Docker Desktop)
- Spotify Developer Account with OAuth2 credentials

## Architecture

The application consists of two services:
- **Backend**: Spring Boot 3.2.2 application (Java 21) running on port 8080
- **Frontend**: React 18 + TypeScript + Vite application served by nginx on port 3000

Both services are bound to `127.0.0.1` to ensure OAuth2 cookie consistency.

## When to Use Docker

Docker is intended for **production-like deployments and testing**. For local development, it's recommended to run the backend and frontend in separate terminals for faster iteration and hot reload:

```bash
# Terminal 1: Backend
export SPOTIFY_CLIENT_ID=your_client_id
export SPOTIFY_CLIENT_SECRET=your_client_secret
./gradlew bootRun

# Terminal 2: Frontend
cd frontend
npm run dev
```

See [RUN_FULL_STACK.md](RUN_FULL_STACK.md) for detailed local development instructions.

## Quick Start

### 1. Set Environment Variables

Create a `.env` file in the project root with your Spotify credentials:

```bash
SPOTIFY_CLIENT_ID=your_client_id_here
SPOTIFY_CLIENT_SECRET=your_client_secret_here
```

**Important**: Ensure your Spotify app's redirect URI is set to:
```
http://127.0.0.1:8080/login/oauth2/code/spotify
```

### 2. Run with Docker Compose

Build and start both services:

```bash
docker-compose up
```

This will:
- Build optimized production images for both services
- Start the backend on http://127.0.0.1:8080
- Start the frontend on http://127.0.0.1:3000
- Use nginx to serve the frontend with optimal caching and compression

Access the application at: **http://127.0.0.1:3000**

## Commands

### Build Images

```bash
docker-compose build
```

Rebuild without cache:
```bash
docker-compose build --no-cache
```

### Start Services

```bash
# With logs (foreground)
docker-compose up

# Detached mode (background)
docker-compose up -d

# Rebuild and start
docker-compose up --build
```

### Stop Services

```bash
# Stop containers (keeps volumes)
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### View Logs

```bash
# All services
docker-compose logs -f

# Backend only
docker-compose logs -f backend

# Frontend only
docker-compose logs -f frontend

# Last 100 lines
docker-compose logs --tail=100 -f
```

### Restart Services

```bash
# Restart all services
docker-compose restart

# Restart backend only
docker-compose restart backend

# Restart frontend only
docker-compose restart frontend
```

### Execute Commands in Containers

```bash
# Backend shell
docker-compose exec backend sh

# Frontend shell
docker-compose exec frontend sh
```

## Configuration

### Backend Configuration

The backend accepts the following environment variables:

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `SPOTIFY_CLIENT_ID` | Yes | - | Spotify OAuth2 Client ID |
| `SPOTIFY_CLIENT_SECRET` | Yes | - | Spotify OAuth2 Client Secret |
| `SPRING_PROFILES_ACTIVE` | No | `default` | Spring Boot profile |
| `JAVA_OPTS` | No | See docker-compose.yml | JVM options (memory, GC settings) |

### Frontend Configuration

The frontend is built as static files and served by nginx. The Vite build process uses:
- Optimized production build
- Gzip compression
- Static asset caching
- API proxy to backend service

## Networking

### Port Bindings
- Backend: http://127.0.0.1:8080
- Frontend: http://127.0.0.1:3000
- Internal Docker network: `spotify-network` (bridge)

### Container Communication
- Frontend nginx proxies API requests to backend using the service name `backend`
- The `backend` service is accessible within the Docker network as `http://backend:8080`

### Host Access
- Both services are accessible from the host machine via `127.0.0.1`
- This ensures OAuth2 cookies work correctly with Spotify's redirect URI
- **Important**: Always use `http://127.0.0.1:3000`, not `http://localhost:3000`

## Health Checks

### Backend Health Check
```bash
curl http://127.0.0.1:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### Frontend Health Check
```bash
curl http://127.0.0.1:3000
```

Should return the HTML of the React app.

## Troubleshooting

### Backend won't start
1. Check if port 8080 is already in use:
   ```bash
   lsof -i :8080
   ```
2. Verify environment variables are set:
   ```bash
   docker-compose config
   ```
3. Check backend logs:
   ```bash
   docker-compose logs backend
   ```

### Frontend won't start
1. Check if port 3000 is already in use:
   ```bash
   lsof -i :3000
   ```
2. Check frontend logs:
   ```bash
   docker-compose logs frontend
   ```
3. Verify the backend is healthy:
   ```bash
   docker-compose ps
   ```

### OAuth2 redirect not working
1. Verify your Spotify app's redirect URI is set to `http://127.0.0.1:8080/login/oauth2/code/spotify`
2. Ensure you're accessing the app via `http://127.0.0.1:3000` (not `http://localhost:3000`)
3. Check that cookies are enabled in your browser
4. Clear browser cookies and try again

### Build fails
1. Ensure you have enough disk space:
   ```bash
   docker system df
   ```
2. Clean up unused Docker resources:
   ```bash
   docker system prune -a
   ```
3. Try building with verbose output:
   ```bash
   docker-compose build --progress=plain
   ```

### Cannot connect to backend from frontend
1. Verify both containers are on the same network:
   ```bash
   docker network inspect spotifywrapped_spotify-network
   ```
2. Check if backend is healthy:
   ```bash
   docker-compose ps
   ```
3. Check nginx logs:
   ```bash
   docker-compose logs frontend
   ```

## Performance Optimization

### Production Builds
- Backend uses multi-stage build to reduce image size (~200MB final image)
- Frontend uses nginx with gzip compression and static asset caching
- JVM is configured with G1GC for better garbage collection

### Resource Limits

You can add resource limits in `docker-compose.yml`:

```yaml
services:
  backend:
    deploy:
      resources:
        limits:
          cpus: '1'
          memory: 1G
        reservations:
          cpus: '0.5'
          memory: 512M
```

### Image Cleanup

Remove unused images to free disk space:
```bash
# Remove dangling images
docker image prune

# Remove all unused images
docker image prune -a
```

## Security Considerations

1. **Non-root user**: Backend container runs as non-root user `spotify`
2. **Environment variables**: Never commit `.env` file with real credentials
3. **Network isolation**: Services communicate via internal Docker network
4. **Logging**: Log rotation configured (10MB max, 3 files)
5. **Health checks**: Automatic container health monitoring

## Additional Resources

- [Main README](README.md)

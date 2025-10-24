# Spotify Developer Dashboard Setup Guide

## Overview

This guide will walk you through setting up your Spotify application to work with the Spotify Wrapped API.

## Important Note About Authentication

**Current Implementation Limitation**: The app currently uses **Client Credentials Flow**, which only provides access to public Spotify catalog data. However, endpoints like `/me/top/tracks` and `/me/top/artists` require **User Authorization** (Authorization Code Flow) to access personal user data.

This means the current setup will authenticate successfully but fail when trying to access user-specific data. See the "Next Steps" section for how to implement proper user authorization.

## Step 1: Create a Spotify Developer Account

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Log in with your Spotify account (or create one if you don't have it)
3. Accept the Terms of Service if prompted

## Step 2: Create a New App

1. Click the **"Create app"** button
2. Fill in the application details:
   - **App name**: `Spotify Wrapped API` (or any name you prefer)
   - **App description**: `Personal Spotify statistics and wrapped data`
   - **Website**: `http://localhost:8080` (for local development)
   - **Redirect URI**: `http://localhost:8080/callback` (important for future user auth)

3. Check the **"Web API"** checkbox under "Which API/SDKs are you planning to use?"
4. Agree to the Developer Terms of Service
5. Click **"Save"**

## Step 3: Get Your Credentials

1. After creating the app, you'll be taken to your app's dashboard
2. Click on **"Settings"** button (top right)
3. You'll see your credentials:
   - **Client ID**: A long string like `abc123def456...`
   - **Client Secret**: Click **"View client secret"** to reveal it

**Keep these credentials secure!** Don't commit them to git or share them publicly.

## Step 4: Configure Your Application

### Option A: Using Environment Variables (Recommended)

```bash
# Export credentials in your terminal
export SPOTIFY_CLIENT_ID="your_client_id_here"
export SPOTIFY_CLIENT_SECRET="your_client_secret_here"

# Run the application
./gradlew bootRun
```

### Option B: Using .env File

1. Copy the example file:
   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and add your credentials:
   ```
   SPOTIFY_CLIENT_ID=your_client_id_here
   SPOTIFY_CLIENT_SECRET=your_client_secret_here
   ```

3. Load the environment variables:
   ```bash
   # On Unix/Linux/Mac
   export $(cat .env | xargs)
   ./gradlew bootRun
   ```

### Option C: IntelliJ IDEA / IDE Configuration

1. Open Run/Debug Configurations
2. Add environment variables:
   ```
   SPOTIFY_CLIENT_ID=your_client_id_here;SPOTIFY_CLIENT_SECRET=your_client_secret_here
   ```

## Step 5: Test Your Setup

1. Start the application:
   ```bash
   ./gradlew bootRun
   ```

2. Test the health endpoint:
   ```bash
   curl http://localhost:8080/api/health
   ```

   Expected response:
   ```json
   {
     "status": "UP",
     "timestamp": "2025-01-15T10:30:00Z"
   }
   ```

3. Test authentication (this will work):
   ```bash
   curl http://localhost:8080/api/spotify/top/tracks?limit=3
   ```

   **Expected response (with current Client Credentials flow)**:
   ```json
   {
     "status": 401,
     "error": "Authentication failed",
     "message": "Failed to authenticate with Spotify API"
   }
   ```
   OR you might get a 403 error because Client Credentials can't access user data.

## Why It's Not Working: Understanding OAuth Flows

### Current Implementation: Client Credentials Flow

- Used for: Server-to-server authentication
- Access to: Public catalog data only (search, get track/artist/album info)
- **Cannot access**: User-specific data like `/me/top/tracks`

### What You Need: Authorization Code Flow

- Used for: User authorization
- Access to: User's personal data
- Requires: User to log in and grant permissions
- Scopes needed:
  - `user-top-read` - Read access to user's top artists and tracks
  - `user-read-recently-played` - Read recently played tracks

## Next Steps: Implementing User Authorization

To make this app fully functional, you need to implement the Authorization Code Flow. Here's what needs to be added:

### 1. Update Spotify Dashboard Settings

1. Go to your app settings in [Spotify Dashboard](https://developer.spotify.com/dashboard)
2. Add **Redirect URIs**:
   - `http://localhost:8080/callback`
   - `http://localhost:8080/login/oauth2/code/spotify` (for Spring Security OAuth2)

### 2. Add Required Dependencies

Add to `build.gradle.kts`:
```kotlin
implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
implementation("org.springframework.boot:spring-boot-starter-security")
```

### 3. Update application.yml

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

### 4. Implementation Flow

1. User visits your app
2. App redirects to Spotify login
3. User logs in and grants permissions
4. Spotify redirects back with authorization code
5. App exchanges code for access token
6. App uses token to access user data

## Temporary Workaround: Manual Token

For testing purposes, you can manually get a token:

1. Go to [Spotify Web API Console](https://developer.spotify.com/console/get-current-user-top-artists/)
2. Click "Get Token"
3. Select scopes: `user-top-read`, `user-read-recently-played`
4. Copy the token (valid for 1 hour)
5. Test with curl:
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN_HERE" \
     "https://api.spotify.com/v1/me/top/tracks?limit=5"
   ```

## Common Issues

### 1. "Invalid client" error
- Double-check your Client ID and Client Secret
- Make sure there are no extra spaces or quotes
- Client Secret must be revealed in the dashboard

### 2. "Invalid redirect URI" error
- Ensure the redirect URI in your request matches exactly what's in the dashboard
- URIs are case-sensitive
- Include the protocol (http:// or https://)

### 3. "Insufficient client scope" error
- This means you're using Client Credentials flow for endpoints that require user authorization
- You need to implement Authorization Code flow (see Next Steps above)

### 4. Environment variables not loading
```bash
# Check if variables are set
echo $SPOTIFY_CLIENT_ID
echo $SPOTIFY_CLIENT_SECRET

# If empty, they're not exported correctly
```

## Security Best Practices

1. **Never commit credentials to git**
   - The `.env` file is already in `.gitignore`
   - Use environment variables in production

2. **Rotate your Client Secret periodically**
   - You can generate a new secret in the Spotify Dashboard
   - Update your environment variables

3. **Use HTTPS in production**
   - Spotify requires HTTPS for redirect URIs in production
   - Use a reverse proxy like nginx or a cloud provider

4. **Limit redirect URIs**
   - Only add redirect URIs you actually use
   - Remove development URIs in production

## Resources

- [Spotify Web API Documentation](https://developer.spotify.com/documentation/web-api)
- [Spotify Authorization Guide](https://developer.spotify.com/documentation/web-api/concepts/authorization)
- [Spotify API Scopes](https://developer.spotify.com/documentation/web-api/concepts/scopes)
- [Spring Security OAuth2 Client](https://spring.io/guides/tutorials/spring-boot-oauth2/)

## Need Help?

If you encounter issues:
1. Check the application logs for detailed error messages
2. Verify your credentials in the Spotify Dashboard
3. Ensure all redirect URIs are correctly configured
4. Make sure you're using the correct OAuth flow for your use case

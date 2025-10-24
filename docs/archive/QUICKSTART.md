# Quick Start Checklist

Follow these steps to get your Spotify Wrapped API running:

## ✅ Step 1: Spotify Developer Dashboard (5 minutes)

- [ ] Go to https://developer.spotify.com/dashboard
- [ ] Log in with your Spotify account
- [ ] Click "Create app" button
- [ ] Fill in the form:
  - App name: `Spotify Wrapped API`
  - App description: `Personal Spotify statistics`
  - Website: `http://localhost:8080`
  - Redirect URI: `http://localhost:8080/callback`
  - Check "Web API"
- [ ] Click "Save"
- [ ] Click "Settings" to view your app settings
- [ ] Copy your **Client ID**
- [ ] Click "View client secret" and copy your **Client Secret**

## ✅ Step 2: Configure Your Application (2 minutes)

- [ ] Open terminal in project directory
- [ ] Run these commands (replace with your actual credentials):

```bash
export SPOTIFY_CLIENT_ID="paste_your_client_id_here"
export SPOTIFY_CLIENT_SECRET="paste_your_client_secret_here"
```

**Or** create a `.env` file:
```bash
cp .env.example .env
# Then edit .env with your credentials
```

## ✅ Step 3: Build and Run (2 minutes)

- [ ] Make gradlew executable (if needed):
```bash
chmod +x ./gradlew
```

- [ ] Build the project:
```bash
./gradlew clean build
```

- [ ] Run the application:
```bash
./gradlew bootRun
```

- [ ] Wait for the message: `Started SpotifyWrappedApplication`

## ✅ Step 4: Test It Works (1 minute)

- [ ] Open a new terminal window
- [ ] Test the health endpoint:
```bash
curl http://localhost:8080/api/health
```

Expected output:
```json
{
  "status" : "UP",
  "timestamp" : "2025-01-15T10:30:00Z"
}
```

## ⚠️ Known Limitation

The app will authenticate successfully, but **user-specific endpoints won't work yet** because they require user authorization (not just client credentials).

You'll see errors like:
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Insufficient client scope"
}
```

**This is expected!** See [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) for how to implement full user authorization with OAuth.

## 🎉 Success!

If you got a successful response from the health endpoint, your setup is correct!

## Next Steps

1. Read [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) to understand authentication flows
2. Implement Authorization Code Flow for user data access (see the guide)
3. Or use the manual token workaround for testing (see guide)

## Having Issues?

| Issue | Solution |
|-------|----------|
| `BUILD FAILED` | Make sure Java 21 is installed: `java -version` |
| `Invalid client` | Double-check Client ID and Secret (no spaces) |
| Environment variables not working | Use `echo $SPOTIFY_CLIENT_ID` to verify |
| Port 8080 already in use | Stop other apps using port 8080 or change in `application.yml` |

See [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) for detailed troubleshooting.

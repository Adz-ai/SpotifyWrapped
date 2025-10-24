# Testing Guide - Using 127.0.0.1

## ✅ Spotify Dashboard Setup

Add this redirect URI to your Spotify app settings:

```
http://127.0.0.1:8080/login/oauth2/code/spotify
```

**Important:** Use `127.0.0.1` (NOT `localhost`) everywhere because Spotify doesn't accept localhost.

## 🚀 Start the App

```bash
./run.sh
```

Wait for the message: `Started SpotifyWrappedApplication`

## 🧪 Testing Steps

### Step 1: Check Health
```bash
curl http://127.0.0.1:8080/api/health
```

Expected:
```json
{
  "status": "UP",
  "timestamp": "..."
}
```

### Step 2: Visit Home Page

**In your browser**, go to:
```
http://127.0.0.1:8080
```

You should see:
```json
{
  "authenticated": false,
  "message": "Welcome to Spotify Wrapped API! Please log in with Spotify.",
  "loginUrl": "/oauth2/authorization/spotify"
}
```

### Step 3: Login with Spotify

**In your browser**, navigate to:
```
http://127.0.0.1:8080/oauth2/authorization/spotify
```

This will:
1. ✅ Redirect to Spotify login
2. ✅ Ask you to grant permissions
3. ✅ Redirect back to `http://127.0.0.1:8080/login/oauth2/code/spotify`
4. ✅ Process the OAuth2 callback
5. ✅ Redirect to `http://127.0.0.1:8080/api/spotify/wrapped`
6. ✅ Show your Spotify wrapped data!

### Step 4: Test Endpoints (After Login)

Once logged in, try these in your browser:

```
# Complete wrapped summary
http://127.0.0.1:8080/api/spotify/wrapped

# Your top 5 tracks
http://127.0.0.1:8080/api/spotify/top/tracks?limit=5

# Your top 10 artists
http://127.0.0.1:8080/api/spotify/top/artists?limit=10

# Your top albums
http://127.0.0.1:8080/api/spotify/top/albums?limit=5

# Your top genres
http://127.0.0.1:8080/api/spotify/top/genres?limit=10

# Check home page (should show authenticated)
http://127.0.0.1:8080/
```

## ✅ Success Criteria

You'll know it's working when:

1. ✅ Login redirects to Spotify
2. ✅ After granting permissions, you're redirected back
3. ✅ You see **your actual Spotify data** with real song names
4. ✅ No 401, 403, or 500 errors
5. ✅ Home page shows `"authenticated": true`

## 🐛 Troubleshooting

### Error: "redirect_uri_mismatch"

**Problem:** The redirect URI doesn't match exactly

**Solution:**
1. Check Spotify Dashboard has: `http://127.0.0.1:8080/login/oauth2/code/spotify`
2. Make sure you're accessing via `http://127.0.0.1:8080` (NOT localhost)
3. Check there are no typos or extra spaces
4. Make sure you clicked "Save" in Spotify Dashboard

### Error: "Invalid client"

**Problem:** Credentials not loaded

**Solution:**
```bash
# Check if variables are set
echo $SPOTIFY_CLIENT_ID
echo $SPOTIFY_CLIENT_SECRET

# If empty, reload them
export $(cat .env | xargs)
./gradlew bootRun
```

### Can't Access Endpoints After Login

**Problem:** Session/cookie issues

**Solution:**
1. Make sure you're using the same browser session
2. Clear cookies and try again
3. Restart the app: Ctrl+C then `./run.sh`
4. Re-login via `/oauth2/authorization/spotify`

### Port 8080 Already in Use

**Problem:** Another app is using port 8080

**Solution:**
```bash
# Find what's using port 8080
lsof -i :8080

# Kill it
kill -9 <PID>

# Or change the port in application.yml:
server:
  port: 8081
```

Then use `http://127.0.0.1:8081` and update Spotify redirect URI accordingly.

## 🔄 Logout

To logout and test the login flow again:

```
http://127.0.0.1:8080/logout
```

## 📝 Notes

- **Always use `127.0.0.1`** - Don't use localhost
- **Use a browser** for testing - OAuth2 uses cookies/sessions
- **One browser tab** - Multiple tabs can cause session issues
- **Token expires** - Tokens typically last 1 hour, then you need to re-login

## 🎉 Expected Result

After successful login, visiting `http://127.0.0.1:8080/api/spotify/wrapped?limit=5` should show something like:

```json
{
  "topTracks": {
    "type": "tracks",
    "count": 5,
    "items": [
      {
        "id": "...",
        "name": "Your Favorite Song",
        "artists": [...],
        "popularity": 95
      },
      ...
    ]
  },
  "topArtists": {
    "type": "artists",
    "count": 5,
    "items": [
      {
        "id": "...",
        "name": "Your Favorite Artist",
        "genres": ["pop", "rock"],
        "popularity": 98
      },
      ...
    ]
  },
  "topAlbums": {...},
  "topGenres": {...}
}
```

## 🚀 Quick Commands

```bash
# Start app
./run.sh

# In browser
http://127.0.0.1:8080/oauth2/authorization/spotify

# After login, test
http://127.0.0.1:8080/api/spotify/wrapped?limit=10

# Logout
http://127.0.0.1:8080/logout
```

---

**Ready to test?** Start the app and visit `http://127.0.0.1:8080/oauth2/authorization/spotify` in your browser!

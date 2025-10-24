# OAuth2 Setup Instructions - ACTION REQUIRED

## ✅ Code Changes Complete!

OAuth2 user authorization has been implemented. Now you need to update your Spotify Developer Dashboard.

## 🔧 Step 1: Update Spotify Dashboard (REQUIRED)

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Click on your app
3. Click "Settings" button (top right)
4. Scroll down to **"Redirect URIs"** section
5. Add this URI:
   ```
   http://localhost:8080/login/oauth2/code/spotify
   ```
6. Click "Add"
7. Click "Save" at the bottom

**Without this redirect URI, OAuth2 login will fail!**

## 🚀 Step 2: Test the OAuth2 Flow

### Start the Application

```bash
# Load environment variables and start
./run.sh

# Or manually:
export $(cat .env | xargs)
./gradlew bootRun
```

### Test the Login Flow

1. **Open your browser** and go to:
   ```
   http://localhost:8080
   ```

2. **You'll see a JSON response** with a login URL:
   ```json
   {
     "authenticated": false,
     "message": "Welcome to Spotify Wrapped API! Please log in with Spotify.",
     "loginUrl": "/oauth2/authorization/spotify"
   }
   ```

3. **Navigate to the login URL**:
   ```
   http://localhost:8080/oauth2/authorization/spotify
   ```

4. **You'll be redirected to Spotify** to log in and authorize the app

5. **Grant permissions** (user-top-read, user-read-recently-played)

6. **You'll be redirected back** to your app and logged in!

7. **After successful login**, you'll be redirected to:
   ```
   http://localhost:8080/api/spotify/wrapped
   ```
   And see your actual Spotify data!

### Test Individual Endpoints

Once logged in, try these endpoints:

```bash
# Get your top tracks
curl http://localhost:8080/api/spotify/top/tracks?limit=5

# Get your top artists
curl http://localhost:8080/api/spotify/top/artists?limit=5

# Get your top albums
curl http://localhost:8080/api/spotify/top/albums?limit=5

# Get your top genres
curl http://localhost:8080/api/spotify/top/genres?limit=5

# Get everything at once
curl http://localhost:8080/api/spotify/wrapped?limit=10
```

**Note:** If using curl, you need to include cookies/session. It's easier to test in a browser first.

## 🎯 What Changed?

### Before (Client Credentials Flow)
- ❌ App authenticated with Spotify using client credentials
- ❌ Could only access public catalog data
- ❌ Could NOT access user-specific data like top tracks

### After (Authorization Code Flow)
- ✅ Users log in with their Spotify account
- ✅ Users grant permission to access their data
- ✅ App gets user-specific access token
- ✅ Can now access top tracks, artists, albums, genres

## 🔐 OAuth2 Flow Explained

```
1. User visits your app
   ↓
2. User clicks "Login with Spotify" (or visits /oauth2/authorization/spotify)
   ↓
3. Redirected to Spotify login page
   ↓
4. User logs in and grants permissions
   ↓
5. Spotify redirects back to: http://localhost:8080/login/oauth2/code/spotify
   ↓
6. App exchanges authorization code for access token
   ↓
7. User is authenticated and can access their data!
```

## 📱 Browser-Based Testing

The easiest way to test is with a browser:

1. Start the app: `./run.sh`
2. Open browser: `http://localhost:8080`
3. Click the login URL or manually navigate to: `http://localhost:8080/oauth2/authorization/spotify`
4. Log in with Spotify
5. You'll be redirected back and see your wrapped data!

## 🔍 Troubleshooting

### "redirect_uri_mismatch" Error
**Solution:** Make sure you added the exact redirect URI to your Spotify Dashboard:
```
http://localhost:8080/login/oauth2/code/spotify
```
No trailing slash, exact match!

### "Invalid client" Error
**Solution:** Check your environment variables are loaded:
```bash
echo $SPOTIFY_CLIENT_ID
echo $SPOTIFY_CLIENT_SECRET
```

### 401 Unauthorized After Login
**Solution:** This might mean the token expired. Log out and log back in:
```
http://localhost:8080/logout
```

### Can't Access Endpoints with curl
**Solution:** OAuth2 uses sessions/cookies. Use a browser for testing, or use curl with cookie handling:
```bash
curl -c cookies.txt -b cookies.txt http://localhost:8080/oauth2/authorization/spotify
# Follow redirects and save cookies, then:
curl -b cookies.txt http://localhost:8080/api/spotify/top/tracks
```

## 📋 Checklist

- [ ] Updated Spotify Dashboard with redirect URI
- [ ] Restarted the application
- [ ] Visited http://localhost:8080 in browser
- [ ] Logged in via /oauth2/authorization/spotify
- [ ] Successfully granted permissions
- [ ] Redirected back to app
- [ ] Can access /api/spotify/wrapped
- [ ] See actual Spotify data!

## 🎉 Success Criteria

You'll know it's working when:

1. You can log in with Spotify
2. After login, `/api/spotify/top/tracks` returns YOUR actual top tracks
3. No more 403 or 500 errors
4. Real data appears with your favorite songs/artists!

## 🔒 Security Notes

- Access tokens are stored in your session (server-side)
- Tokens expire (typically 1 hour)
- Users need to re-authenticate when tokens expire
- Logout is available at `/logout`

## 📚 Next Steps

Once OAuth2 is working:

1. Add token refresh logic (for long-lived sessions)
2. Create a proper frontend UI
3. Add more Spotify endpoints
4. Implement caching for better performance
5. Add time range options (short_term, medium_term, long_term)

---

**Having Issues?** Check the application logs for detailed error messages.

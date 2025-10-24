# Spotify Dashboard Checklist

## 📋 What You Need to Add

Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard) and add these settings:

### 1. App Settings

- **App Name:** Spotify Wrapped API (or your choice)
- **App Description:** Personal Spotify statistics
- **Website:** `http://127.0.0.1:8080`

### 2. Redirect URIs ⭐ CRITICAL

Add this **exact** URI:

```
http://127.0.0.1:8080/login/oauth2/code/spotify
```

**Checklist:**
- [ ] Starts with `http://` (with two slashes)
- [ ] Contains `127.0.0.1` (NOT localhost)
- [ ] Includes `:8080` (the port number)
- [ ] Ends with `/login/oauth2/code/spotify` (exact path)
- [ ] No trailing slash at the end
- [ ] Clicked "Add" button
- [ ] Clicked "Save" button at bottom of page

### 3. Which API/SDKs

- [ ] Web API (checked)

## ✅ Final Check

Your Redirect URIs section should show:

```
http://127.0.0.1:8080/login/oauth2/code/spotify
```

And possibly also (if you added it earlier):

```
http://127.0.0.1:8080/callback
```

Both are fine to have, but the first one is the one that will be used.

## ⚠️ Common Mistakes

❌ `http:127.0.0.1:8080/login/oauth2/code/spotify` - Missing slashes
❌ `http://localhost:8080/login/oauth2/code/spotify` - Used localhost instead of 127.0.0.1
❌ `http://127.0.0.1/login/oauth2/code/spotify` - Missing port :8080
❌ `http://127.0.0.1:8080/login/oauth2/code/spotify/` - Extra trailing slash
❌ `https://127.0.0.1:8080/login/oauth2/code/spotify` - Used https instead of http

✅ `http://127.0.0.1:8080/login/oauth2/code/spotify` - CORRECT!

## 🔑 Get Your Credentials

While in Settings:

1. Copy your **Client ID**
2. Click "View client secret"
3. Copy your **Client Secret**

Make sure these match what's in your `.env` file:

```bash
SPOTIFY_CLIENT_ID=your_actual_client_id
SPOTIFY_CLIENT_SECRET=your_actual_client_secret
```

## 🧪 Test It

After saving:

1. Start your app: `./run.sh`
2. Open browser: `http://127.0.0.1:8080/oauth2/authorization/spotify`
3. Log in with Spotify
4. Grant permissions
5. You should be redirected back and see your data!

## 🆘 If Login Fails

Check the error message:

- **"redirect_uri_mismatch"** → Your redirect URI doesn't match exactly
- **"invalid_client"** → Your Client ID/Secret is wrong
- **"access_denied"** → You clicked "Cancel" on Spotify login
- **"unauthorized_client"** → Your app isn't configured correctly

## 📸 Visual Reference

Your Spotify Dashboard Settings should look like:

```
Basic Information
  App name: Spotify Wrapped API
  App description: Personal Spotify statistics

Website
  http://127.0.0.1:8080

Redirect URIs
  http://127.0.0.1:8080/login/oauth2/code/spotify  [Remove]
  [Add new redirect URI]

  [Add]

Which API/SDKs are you planning to use?
  ☑ Web API
  ☐ Web Playback SDK
  ☐ iOS SDK
  ☐ Android SDK

[Cancel] [Save]
```

---

✅ Once you've added the redirect URI and saved, you're ready to test!

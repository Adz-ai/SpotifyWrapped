# 🚀 Run the Full Stack Application

Quick guide to run both backend (Spring Boot) and frontend (React) together.

## ✅ Prerequisites

- [x] Java 21 installed
- [x] Node.js 18+ installed
- [x] Spotify Developer Dashboard configured
- [x] `.env` file with credentials

## 🎯 Quick Start (2 Steps!)

### Step 1: Start Backend

```bash
# In the root directory
./run.sh
```

Wait for this message:
```
Started SpotifyWrappedApplication in X.XXX seconds
```

✅ Backend is running on `http://127.0.0.1:8080`

### Step 2: Start Frontend

Open a **new terminal** and run:

```bash
# Navigate to frontend directory
cd frontend

# Start the frontend
npm run dev
```

Wait for this message:
```
  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
```

✅ Frontend is running on `http://localhost:3000`

### Step 3: Open Browser

Navigate to: **`http://localhost:3000`**

🎉 You should see the login page!

## 🎨 What to Expect

### First Visit (Not Logged In)
1. See beautiful Spotify-branded login page
2. Click "Login with Spotify"
3. Redirected to Spotify for authorization
4. Grant permissions
5. Redirected back to your app

### After Login
- **Dashboard loads** with your Spotify data
- **Top Tracks** - Your favorite songs with album art
- **Top Artists** - With photos and genres
- **Top Genres** - As colorful tags
- **Stats Summary** - Quick overview

### Features You Can Use
- **Change Limit** - Dropdown to select top 5/10/20/50
- **Logout** - Button in top-right
- **Responsive** - Works on mobile, tablet, desktop

## 📋 Both Terminals Side-by-Side

```
┌─────────────────────────────────┬─────────────────────────────────┐
│ Terminal 1: Backend             │ Terminal 2: Frontend            │
├─────────────────────────────────┼─────────────────────────────────┤
│ $ ./run.sh                      │ $ cd frontend                   │
│                                 │ $ npm run dev                   │
│ Loading env variables...        │                                 │
│ Starting Spring Boot...         │ VITE v5.x.x ready in XXXms      │
│                                 │                                 │
│ Started SpotifyWrappedApp ✅    │ Local: http://localhost:3000 ✅  │
│                                 │                                 │
│ [Logs from backend...]          │ [Logs from frontend...]         │
└─────────────────────────────────┴─────────────────────────────────┘
```

## 🔧 Ports in Use

- **8080** - Spring Boot backend API
- **3000** - React frontend (Vite dev server)

The frontend automatically proxies API requests to backend!

## 🛑 Stopping the Apps

### Stop Frontend
In Terminal 2: Press `Ctrl+C`

### Stop Backend
In Terminal 1: Press `Ctrl+C`

## 🔄 Restarting

Just run the same commands again!

## 🧪 Quick Test Checklist

After starting both apps:

- [ ] Open `http://localhost:3000`
- [ ] See login page with Spotify branding
- [ ] Click "Login with Spotify"
- [ ] Redirected to Spotify authorization
- [ ] Grant permissions
- [ ] Redirected back to app
- [ ] See dashboard with your data
- [ ] Top tracks display with album art
- [ ] Top artists display with images
- [ ] Top genres as tags
- [ ] Can change limit dropdown
- [ ] Can logout

## 🐛 Common Issues

### Backend Won't Start

**Problem:** `Invalid client` or `must not be blank`

**Solution:** Make sure your `.env` file has valid credentials:
```bash
cat .env
# Should show:
SPOTIFY_CLIENT_ID=your_actual_client_id
SPOTIFY_CLIENT_SECRET=your_actual_client_secret
```

### Frontend Won't Start

**Problem:** `Port 3000 already in use`

**Solution:**
```bash
# Kill process using port 3000
lsof -ti:3000 | xargs kill -9

# Or change port in frontend/vite.config.ts
```

### Can't Login

**Problem:** `redirect_uri_mismatch`

**Solution:** Check Spotify Dashboard has this redirect URI:
```
http://127.0.0.1:8080/login/oauth2/code/spotify
```

### No Data After Login

**Problem:** API errors in console

**Solution:**
1. Check backend is running: `curl http://127.0.0.1:8080/api/health`
2. Make sure you logged in successfully
3. Check browser console for errors
4. Try logout and login again

### Build Errors

**Problem:** TypeScript or ESLint errors

**Solution:**
```bash
cd frontend
npm run type-check  # Check types
npm run lint        # Check linting
npm run lint:fix    # Auto-fix issues
```

## 📊 Development Workflow

### Making Backend Changes

1. Edit Java files
2. Spring Boot auto-reloads (with devtools)
3. Refresh browser

### Making Frontend Changes

1. Edit React/TypeScript files
2. Vite hot-reloads instantly
3. See changes immediately (no refresh needed!)

## 🎯 URLs Reference

| Service | URL | Purpose |
|---------|-----|---------|
| Frontend | http://localhost:3000 | Main UI |
| Backend | http://127.0.0.1:8080 | API |
| Health Check | http://127.0.0.1:8080/api/health | Backend status |
| Login | http://localhost:3000 → redirects to Spotify | OAuth flow |
| Logout | Click button in UI | Clears session |

## 💡 Pro Tips

### Tip 1: Keep Both Terminals Visible
Use tmux or split terminal panes to see both logs simultaneously.

### Tip 2: Check Backend Logs
If something isn't working, check the backend terminal for errors.

### Tip 3: Clear Browser Cache
If you see stale data, clear cache or use incognito mode.

### Tip 4: Use Browser DevTools
Open Console (F12) to see network requests and any frontend errors.

## 🎓 Next Steps

Now that it's running:

1. **Play with the UI** - Try different limits, explore your data
2. **Check the Code** - See how types flow from backend to frontend
3. **Make Changes** - Try editing colors, adding features
4. **Deploy** - When ready, deploy to production
5. **Share** - Let friends use your app!

## 📚 More Documentation

- [FRONTEND_SETUP.md](FRONTEND_SETUP.md) - Detailed frontend guide
- [frontend/README.md](frontend/README.md) - Frontend documentation
- [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md) - Spotify Dashboard setup
- [README.md](README.md) - Main project documentation

## 🎉 Success Criteria

You'll know everything is working when:

1. ✅ Backend starts without errors
2. ✅ Frontend starts and opens automatically
3. ✅ Login page appears at `http://localhost:3000`
4. ✅ Can log in with Spotify
5. ✅ Dashboard loads with real data
6. ✅ Can see your top tracks/artists/genres
7. ✅ Images and album art display correctly
8. ✅ No errors in either terminal
9. ✅ No errors in browser console

---

**🎵 Enjoy your Spotify Wrapped! 🎵**

Having issues? Check the troubleshooting sections in:
- [FRONTEND_SETUP.md](FRONTEND_SETUP.md)
- [TESTING_GUIDE.md](TESTING_GUIDE.md)
- [SPOTIFY_SETUP.md](SPOTIFY_SETUP.md)

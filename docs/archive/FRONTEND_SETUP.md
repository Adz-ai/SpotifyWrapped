# Frontend Setup & Running Guide

## 🎉 What's Been Built

A beautiful, modern React + TypeScript frontend with:

✅ **Strict TypeScript** - Full type safety with no compromises
✅ **ESLint + Prettier** - Code quality enforced from day 1
✅ **Tailwind CSS** - Spotify-branded, responsive UI
✅ **Custom Hooks** - Clean data fetching and auth management
✅ **Type-Safe API Client** - Axios client with full TypeScript support
✅ **Production Ready** - Builds successfully with zero warnings

## 🚀 Quick Start

### 1. Start the Backend

In the root directory:

```bash
# Terminal 1 - Backend
./run.sh
```

Wait for: `Started SpotifyWrappedApplication`

### 2. Start the Frontend

In a new terminal:

```bash
# Terminal 2 - Frontend
cd frontend
npm run dev
```

The frontend will start on `http://localhost:3000`

### 3. Open in Browser

Navigate to: `http://localhost:3000`

## 🎨 What You'll See

### Login Page
- Beautiful Spotify-branded design
- Green "Login with Spotify" button
- Responsive layout

### After Login
- **Top Tracks** - Your favorite songs with album art
- **Top Artists** - With profile pictures and genres
- **Top Genres** - As colorful tags
- **Stats Cards** - Quick summary of your data
- **Configurable Limits** - Choose top 5, 10, 20, or 50

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/          # React components
│   │   ├── Login.tsx       # Login page
│   │   ├── Dashboard.tsx   # Main dashboard
│   │   ├── TrackCard.tsx   # Track display
│   │   ├── ArtistCard.tsx  # Artist display
│   │   └── LoadingSpinner.tsx
│   ├── hooks/              # Custom React hooks
│   │   ├── useAuth.ts      # Authentication state
│   │   └── useSpotifyData.ts # Data fetching
│   ├── services/           # API client
│   │   └── api.ts          # Type-safe axios client
│   ├── types/              # TypeScript types
│   │   └── spotify.ts      # API response types
│   ├── App.tsx             # Main app
│   └── main.tsx            # Entry point
├── package.json            # Dependencies
├── tsconfig.json           # TypeScript config (STRICT!)
├── .eslintrc.cjs           # ESLint config
└── vite.config.ts          # Vite config with proxy
```

## 🔧 Available Commands

```bash
# Development server
npm run dev

# Type checking
npm run type-check

# Linting
npm run lint
npm run lint:fix

# Code formatting
npm run format

# Production build
npm run build

# Preview production build
npm run preview
```

## ⚙️ Configuration Highlights

### TypeScript (STRICT MODE)

```json
{
  "strict": true,
  "noUnusedLocals": true,
  "noUnusedParameters": true,
  "noUncheckedIndexedAccess": true,
  "noImplicitReturns": true,
  "exactOptionalPropertyTypes": true
}
```

This means:
- ✅ No implicit any
- ✅ No unused variables or parameters
- ✅ Array access is properly typed
- ✅ Optional properties are strictly typed
- ✅ All code paths must return a value

### ESLint

Configured with TypeScript strict rules and React best practices.

### Vite Proxy

All API calls are automatically proxied to `http://127.0.0.1:8080`:

```typescript
'/api/*'     → Backend
'/oauth2/*'  → Backend OAuth
'/login/*'   → Backend login
'/logout'    → Backend logout
```

## 🎯 Usage Flow

1. **Open** `http://localhost:3000`
2. **Click** "Login with Spotify"
3. **Authorize** on Spotify (redirects to Spotify)
4. **Redirected Back** with your data!
5. **Explore** your top tracks, artists, and genres

## 🐛 Troubleshooting

### Port 3000 Already in Use

Change the port in `frontend/vite.config.ts`:

```typescript
server: {
  port: 3001,
}
```

### Backend Not Running

Make sure the Spring Boot backend is running:

```bash
curl http://127.0.0.1:8080/api/health
```

Should return:
```json
{"status":"UP","timestamp":"..."}
```

### Build Errors

Run type checking to see detailed errors:

```bash
npm run type-check
```

### CORS Issues

The Vite proxy handles this automatically. Make sure you're accessing via `http://localhost:3000` (not `http://127.0.0.1:3000`).

### Authentication Not Working

1. Make sure backend is running
2. Check you're logged in via backend first
3. Clear cookies and try again
4. Check browser console for errors

## 📊 Features

### Authentication
- Automatic auth status check on load
- Session-based authentication with cookies
- Login/logout functionality
- Protected routes

### Data Display
- Real-time data from Spotify API
- Configurable result limits (5/10/20/50)
- Beautiful card layouts
- Album art and artist images
- Genre tags
- Popularity scores

### User Experience
- Loading states
- Error handling
- Responsive design (mobile/tablet/desktop)
- Smooth transitions
- Custom scrollbar
- Spotify green accent color

## 🎨 UI Screenshots (What You'll See)

### Login Page
- Centered login card
- Spotify logo (SVG)
- Green button
- Dark gradient background

### Dashboard Header
- Spotify logo
- "Your Spotify Wrapped" title
- Username display
- Limit selector dropdown
- Logout button

### Stats Cards
- Top Tracks count
- Top Artists count
- Top Albums count
- Unique Genres count

### Track Cards
- Rank number
- Album artwork (64x64px)
- Track name
- Artist names
- Album name
- Duration
- Popularity badge

### Artist Cards
- Rank badge (top-left corner)
- Artist photo (circular, 128x128px)
- Artist name
- Top 3 genres
- Popularity score

### Genre Tags
- Colored pills with green background
- Numbered ranking
- Genre name

## 🔐 Security

- No credentials stored in frontend
- Session cookies handled by backend
- HTTPS ready for production
- CORS properly configured via proxy

## 🚢 Production Deployment

### Build for Production

```bash
npm run build
```

Output: `frontend/dist/`

### Serve Production Build

```bash
npm run preview
```

### Deploy Options

**Option 1: Serve with Backend**
- Copy `dist/*` to backend `src/main/resources/static/`
- Spring Boot will serve it automatically

**Option 2: Separate Hosting**
- Deploy dist folder to Vercel/Netlify/S3
- Update CORS settings in backend
- Update OAuth redirect URIs

## 📝 Code Quality

All code follows:
- **TypeScript strict mode** - Zero type errors
- **ESLint rules** - Zero warnings
- **Prettier formatting** - Consistent style
- **React best practices** - Hooks, functional components
- **Accessibility** - Semantic HTML

## 🎓 Technologies Used

- **React 18** - Latest React with hooks
- **TypeScript 5.3** - With strict settings
- **Vite 5** - Lightning-fast build tool
- **Tailwind CSS 3** - Utility-first CSS
- **Axios** - HTTP client
- **React Hooks** - State management

## 💡 Next Steps

Now that the frontend is working, you can:

1. **Customize Colors** - Edit `tailwind.config.js`
2. **Add More Features** - Time ranges, recently played
3. **Improve UI** - Add animations, charts
4. **Add Tests** - Jest + React Testing Library
5. **Deploy** - Vercel, Netlify, or with backend

## 🎉 Success!

If you see the login page at `http://localhost:3000`, you're all set!

Click "Login with Spotify" and enjoy your personalized Spotify Wrapped! 🎵

---

**Need help?** Check the browser console for errors or see [frontend/README.md](frontend/README.md) for more details.

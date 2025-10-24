# Spotify Wrapped Frontend

Modern React + TypeScript frontend for the Spotify Wrapped API.

## 🚀 Features

- **Strict TypeScript** - Full type safety with strict compiler settings
- **ESLint + Prettier** - Code quality and formatting enforced
- **Tailwind CSS** - Modern, responsive UI with Spotify branding
- **Vite** - Lightning-fast development and build times
- **React Hooks** - Custom hooks for data fetching and authentication
- **API Client** - Type-safe axios client with error handling

## 📋 Prerequisites

- Node.js 18+ and npm
- Backend API running on `http://127.0.0.1:8080`

## 🛠️ Installation

```bash
# Install dependencies
npm install
```

## 🏃 Running the App

### Development Mode

```bash
npm run dev
```

The app will start on `http://localhost:3000` and proxy API requests to the backend.

### Build for Production

```bash
npm run build
```

### Preview Production Build

```bash
npm run preview
```

## 🧪 Code Quality

### Type Checking

```bash
npm run type-check
```

### Linting

```bash
# Check for issues
npm run lint

# Auto-fix issues
npm run lint:fix
```

### Formatting

```bash
npm run format
```

## 📁 Project Structure

```
frontend/
├── src/
│   ├── components/         # React components
│   │   ├── Login.tsx      # Login page
│   │   ├── Dashboard.tsx  # Main dashboard
│   │   ├── TrackCard.tsx  # Track display component
│   │   ├── ArtistCard.tsx # Artist display component
│   │   └── LoadingSpinner.tsx
│   ├── hooks/             # Custom React hooks
│   │   ├── useAuth.ts     # Authentication hook
│   │   └── useSpotifyData.ts # Data fetching hook
│   ├── services/          # API services
│   │   └── api.ts         # Spotify API client
│   ├── types/             # TypeScript type definitions
│   │   └── spotify.ts     # API response types
│   ├── App.tsx            # Main app component
│   ├── main.tsx           # Entry point
│   └── index.css          # Global styles
├── index.html             # HTML template
├── package.json           # Dependencies and scripts
├── tsconfig.json          # TypeScript configuration (STRICT)
├── .eslintrc.cjs          # ESLint configuration
├── .prettierrc.json       # Prettier configuration
├── vite.config.ts         # Vite configuration
└── tailwind.config.js     # Tailwind CSS configuration
```

## 🎨 UI Components

### Login Page
- Spotify-branded login button
- Responsive design
- OAuth2 flow initiation

### Dashboard
- Top tracks with album art
- Top artists with images
- Top genres as tags
- Stats summary cards
- Configurable limit (5/10/20/50)
- Logout functionality

### Track Card
- Album artwork
- Track name and artists
- Duration and popularity
- Rank indicator

### Artist Card
- Artist photo
- Name and genres
- Popularity score
- Rank badge

## 🔧 Configuration

### TypeScript

The project uses **strict TypeScript** settings:
- `strict: true`
- `noUnusedLocals: true`
- `noUnusedParameters: true`
- `noUncheckedIndexedAccess: true`
- `exactOptionalPropertyTypes: true`
- And more...

### ESLint

Configured with:
- TypeScript strict type checking
- React hooks rules
- React refresh plugin
- Prettier integration

### Vite Proxy

API requests are proxied to the backend:
- `/api/*` → `http://127.0.0.1:8080`
- `/oauth2/*` → `http://127.0.0.1:8080`
- `/login/*` → `http://127.0.0.1:8080`
- `/logout` → `http://127.0.0.1:8080`

## 🎯 Usage Flow

1. **Start Backend** - Ensure the Spring Boot API is running
2. **Start Frontend** - Run `npm run dev`
3. **Navigate** - Open `http://localhost:3000`
4. **Login** - Click "Login with Spotify"
5. **Authorize** - Grant permissions on Spotify
6. **View Data** - See your top tracks, artists, and genres!

## 🔐 Authentication

The app uses **session-based authentication** with cookies. The frontend automatically:
- Checks authentication status on load
- Redirects to Spotify OAuth2 login
- Maintains session across page refreshes
- Handles logout

## 📊 Data Fetching

Custom React hooks manage data:

```typescript
// Check if user is authenticated
const { isAuthenticated, user, login, logout } = useAuth();

// Fetch Spotify wrapped data
const { data, isLoading, error } = useSpotifyData(limit);
```

## 🚨 Error Handling

- API errors are caught and displayed
- Type-safe error responses
- User-friendly error messages
- Loading states

## 🎨 Styling

- **Tailwind CSS** for utility-first styling
- **Spotify color palette** (green, black, gray)
- **Custom scrollbar** styled to match theme
- **Smooth transitions** on all interactions
- **Responsive design** for mobile/tablet/desktop

## 🔄 Hot Module Replacement

Vite provides instant HMR for:
- React components
- CSS changes
- Type updates

## 📦 Production Build

The build output is optimized:
- Tree-shaking for smaller bundles
- Code splitting
- Minification
- Asset optimization

## 🐛 Debugging

Enable debug logging in the browser console:
```typescript
// In api.ts, add console.log statements
console.log('API Response:', response.data);
```

## 🔧 Troubleshooting

### Port 3000 Already in Use

Change the port in `vite.config.ts`:
```typescript
server: {
  port: 3001,
}
```

### Backend Connection Issues

Verify the backend is running:
```bash
curl http://127.0.0.1:8080/api/health
```

### TypeScript Errors

Run type checking:
```bash
npm run type-check
```

### ESLint Errors

Auto-fix most issues:
```bash
npm run lint:fix
```

## 📚 Technologies

- **React 18** - UI framework
- **TypeScript 5.3** - Type safety
- **Vite 5** - Build tool
- **Tailwind CSS 3** - Styling
- **Axios** - HTTP client
- **ESLint** - Code linting
- **Prettier** - Code formatting

## 🎓 Learning Resources

- [React Docs](https://react.dev/)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Vite Guide](https://vitejs.dev/guide/)
- [Tailwind CSS](https://tailwindcss.com/docs)

## 🤝 Contributing

1. Run type checking: `npm run type-check`
2. Run linting: `npm run lint`
3. Format code: `npm run format`
4. Test the build: `npm run build`

---

Built with ❤️ using React, TypeScript, and Tailwind CSS

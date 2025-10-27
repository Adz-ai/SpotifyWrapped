import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Determine backend URL based on environment
// In Docker, use service name 'backend', otherwise use 127.0.0.1
const backendUrl = process.env.VITE_API_URL || 'http://127.0.0.1:8080';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // Listen on all interfaces (allows both localhost and 127.0.0.1)
    port: 3000,
    proxy: {
      '/api': {
        target: backendUrl,
        changeOrigin: true,
      },
      '/oauth2': {
        target: backendUrl,
        changeOrigin: true,
      },
      '/login': {
        target: backendUrl,
        changeOrigin: true,
      },
      '/logout': {
        target: backendUrl,
        changeOrigin: true,
      },
    },
  },
});

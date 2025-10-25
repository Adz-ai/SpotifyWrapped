import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0', // Listen on all interfaces (allows both localhost and 127.0.0.1)
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      '/oauth2': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      '/login': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
      '/logout': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true,
      },
    },
  },
});

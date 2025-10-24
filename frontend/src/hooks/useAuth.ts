import { useState, useEffect } from 'react';
import { spotifyApi } from '../services/api';
import type { HomeResponse } from '../types/spotify';

interface UseAuthReturn {
  isAuthenticated: boolean;
  user: string | null;
  isLoading: boolean;
  error: string | null;
  login: () => void;
  logout: () => void;
  refresh: () => Promise<void>;
}

export function useAuth(): UseAuthReturn {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const checkAuthStatus = async (): Promise<void> => {
    try {
      setIsLoading(true);
      setError(null);
      const response: HomeResponse = await spotifyApi.getHome();
      setIsAuthenticated(response.authenticated);
      setUser(response.user ?? null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to check authentication status');
      setIsAuthenticated(false);
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void checkAuthStatus();
  }, []);

  const login = (): void => {
    window.location.href = spotifyApi.getLoginUrl();
  };

  const logout = (): void => {
    window.location.href = spotifyApi.getLogoutUrl();
  };

  const refresh = async (): Promise<void> => {
    await checkAuthStatus();
  };

  return {
    isAuthenticated,
    user,
    isLoading,
    error,
    login,
    logout,
    refresh,
  };
}

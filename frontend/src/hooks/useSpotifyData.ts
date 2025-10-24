import { useState, useEffect } from 'react';
import { spotifyApi } from '../services/api';
import type { SpotifyWrappedResponse } from '../types/spotify';

interface UseSpotifyDataReturn {
  data: SpotifyWrappedResponse | null;
  isLoading: boolean;
  error: string | null;
  refetch: (newLimit?: number) => Promise<void>;
}

export function useSpotifyData(limit = 10): UseSpotifyDataReturn {
  const [data, setData] = useState<SpotifyWrappedResponse | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async (fetchLimit: number): Promise<void> => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await spotifyApi.getWrapped(fetchLimit);
      setData(response);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch Spotify data');
      setData(null);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    void fetchData(limit);
  }, [limit]);

  const refetch = async (newLimit?: number): Promise<void> => {
    await fetchData(newLimit ?? limit);
  };

  return {
    data,
    isLoading,
    error,
    refetch,
  };
}

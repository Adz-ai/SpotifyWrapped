import axios from 'axios';
import type { AxiosInstance, AxiosError } from 'axios';
import type {
  SpotifyWrappedResponse,
  UserTopItemsResponse,
  TrackDto,
  ArtistDto,
  AlbumDto,
  HomeResponse,
  HealthResponse,
  ErrorResponse,
} from '../types/spotify';

class SpotifyApiClient {
  private client: AxiosInstance;

  constructor() {
    this.client = axios.create({
      baseURL: '/api',
      headers: {
        'Content-Type': 'application/json',
      },
      withCredentials: true, // Important for session cookies
    });
  }

  /**
   * Handle API errors and extract error message
   */
  private handleError(error: unknown): never {
    if (axios.isAxiosError(error)) {
      const axiosError = error as AxiosError<ErrorResponse>;
      const message =
        axiosError.response?.data?.message || axiosError.message || 'An unexpected error occurred';
      throw new Error(message);
    }
    throw new Error('An unexpected error occurred');
  }

  /**
   * Get health status
   */
  async getHealth(): Promise<HealthResponse> {
    try {
      const response = await this.client.get<HealthResponse>('/health');
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get home/auth status
   */
  async getHome(): Promise<HomeResponse> {
    try {
      const response = await this.client.get<HomeResponse>('/');
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get user's complete Spotify Wrapped data
   */
  async getWrapped(limit = 10): Promise<SpotifyWrappedResponse> {
    try {
      const response = await this.client.get<SpotifyWrappedResponse>('/spotify/wrapped', {
        params: { limit },
      });
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get user's top tracks
   */
  async getTopTracks(limit = 10): Promise<UserTopItemsResponse<TrackDto>> {
    try {
      const response = await this.client.get<UserTopItemsResponse<TrackDto>>(
        '/spotify/top/tracks',
        {
          params: { limit },
        }
      );
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get user's top artists
   */
  async getTopArtists(limit = 10): Promise<UserTopItemsResponse<ArtistDto>> {
    try {
      const response = await this.client.get<UserTopItemsResponse<ArtistDto>>(
        '/spotify/top/artists',
        {
          params: { limit },
        }
      );
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get user's top albums
   */
  async getTopAlbums(limit = 10): Promise<UserTopItemsResponse<AlbumDto>> {
    try {
      const response = await this.client.get<UserTopItemsResponse<AlbumDto>>(
        '/spotify/top/albums',
        {
          params: { limit },
        }
      );
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get user's top genres
   */
  async getTopGenres(limit = 10): Promise<UserTopItemsResponse<string>> {
    try {
      const response = await this.client.get<UserTopItemsResponse<string>>('/spotify/top/genres', {
        params: { limit },
      });
      return response.data;
    } catch (error) {
      return this.handleError(error);
    }
  }

  /**
   * Get Spotify login URL
   * Must go directly to backend (not through proxy) to maintain session
   */
  getLoginUrl(): string {
    return 'http://127.0.0.1:8080/oauth2/authorization/spotify';
  }

  /**
   * Get logout URL
   * Must go directly to backend (not through proxy) to maintain session
   */
  getLogoutUrl(): string {
    return 'http://127.0.0.1:8080/logout';
  }
}

// Export a singleton instance
export const spotifyApi = new SpotifyApiClient();

export interface ExternalUrls {
  spotify: string;
}

export interface ImageDto {
  url: string;
  height: number | null;
  width: number | null;
}

export interface ArtistDto {
  id: string;
  name: string;
  genres: string[];
  popularity: number;
  external_urls: ExternalUrls;
  images: ImageDto[];
}

export interface AlbumDto {
  id: string;
  name: string;
  album_type: string;
  release_date: string;
  artists: ArtistDto[];
  images: ImageDto[];
  external_urls: ExternalUrls;
}

export interface TrackDto {
  id: string;
  name: string;
  album: AlbumDto;
  artists: ArtistDto[];
  popularity: number;
  duration_ms: number;
  external_urls: ExternalUrls;
}

export interface UserTopItemsResponse<T> {
  type: string;
  count: number;
  items: T[];
}

export interface SpotifyWrappedResponse {
  topTracks: UserTopItemsResponse<TrackDto>;
  topArtists: UserTopItemsResponse<ArtistDto>;
  topAlbums: UserTopItemsResponse<AlbumDto>;
  topGenres: UserTopItemsResponse<string>;
}

export interface HealthResponse {
  status: string;
  timestamp: string;
}

export interface HomeResponse {
  authenticated: boolean;
  user?: string;
  message: string;
  loginUrl?: string;
  endpoints?: {
    wrapped: string;
    topTracks: string;
    topArtists: string;
    topAlbums: string;
    topGenres: string;
    logout: string;
  };
}

export interface ErrorResponse {
  status: number;
  error: string;
  message: string;
  path: string;
  timestamp: string;
}

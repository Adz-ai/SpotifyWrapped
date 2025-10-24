import type { FC } from 'react';
import { useState } from 'react';
import { useSpotifyData } from '../hooks/useSpotifyData';
import { TrackCard } from './TrackCard';
import { ArtistCard } from './ArtistCard';
import { LoadingSpinner } from './LoadingSpinner';

interface DashboardProps {
  user: string | null;
  onLogout: () => void;
}

export const Dashboard: FC<DashboardProps> = ({ user, onLogout }) => {
  const [limit, setLimit] = useState(10);
  const { data, isLoading, error } = useSpotifyData(limit);

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-spotify-black via-gray-900 to-spotify-black flex items-center justify-center p-4">
        <div className="bg-red-900/20 border border-red-500 text-red-200 px-6 py-4 rounded-lg max-w-md">
          <h3 className="font-bold mb-2">Error Loading Data</h3>
          <p>{error}</p>
        </div>
      </div>
    );
  }

  if (!data) {
    return null;
  }

  const topGenres = data.topGenres.items.slice(0, 10);

  return (
    <div className="min-h-screen bg-gradient-to-br from-spotify-black via-gray-900 to-spotify-black">
      {/* Header */}
      <header className="bg-gray-800/50 backdrop-blur-sm border-b border-gray-700 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center space-x-4">
              <svg
                className="w-10 h-10 text-spotify-green"
                fill="currentColor"
                viewBox="0 0 168 168"
              >
                <path d="M83.996 0C37.613 0 0 37.614 0 83.998 0 130.383 37.613 168 83.996 168c46.38 0 83.997-37.617 83.997-83.99 0-46.405-37.617-84.01-83.997-84.01zm38.539 121.085c-1.496 2.463-4.69 3.25-7.145 1.76-19.613-11.977-44.266-14.676-73.297-8.049-2.795.638-5.56-1.043-6.191-3.837-.634-2.787 1.04-5.557 3.833-6.186 31.698-7.246 58.857-4.117 80.37 9.294 2.466 1.483 3.26 4.676 1.77 7.13zm10.199-22.665c-1.89 3.07-5.905 4.038-8.958 2.153-22.448-13.805-56.685-17.813-83.249-9.738-3.53 1.068-7.248-1.317-8.312-4.838-1.062-3.523 1.31-7.253 4.838-8.317 30.408-9.236 68.07-4.758 94.014 11.13 3.06 1.884 4.027 5.897 2.137 8.966zm.876-23.598c-26.945-16.003-71.407-17.48-97.118-9.669-4.248 1.29-8.723-1.105-10.013-5.35-1.29-4.248 1.104-8.722 5.35-10.01 29.532-8.978 78.445-7.245 109.46 11.196 3.722 2.206 4.943 7.016 2.73 10.73-2.22 3.728-7.02 4.95-10.73 2.736z" />
              </svg>
              <div>
                <h1 className="text-2xl font-bold text-white">Your Spotify Wrapped</h1>
                {user && <p className="text-sm text-gray-400">Welcome, {user}</p>}
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <select
                value={limit}
                onChange={(e) => setLimit(Number(e.target.value))}
                className="bg-gray-700 text-white rounded-lg px-4 py-2 border border-gray-600 focus:outline-none focus:ring-2 focus:ring-spotify-green"
              >
                <option value={5}>Top 5</option>
                <option value={10}>Top 10</option>
                <option value={20}>Top 20</option>
                <option value={50}>Top 50</option>
              </select>

              <button
                onClick={onLogout}
                className="bg-gray-700 hover:bg-gray-600 text-white font-medium py-2 px-4 rounded-lg transition-colors"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Summary */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
          <div className="bg-gray-800 rounded-lg p-6 border border-gray-700">
            <div className="text-3xl font-bold text-spotify-green mb-1">
              {data.topTracks.count}
            </div>
            <div className="text-gray-400 text-sm">Top Tracks</div>
          </div>
          <div className="bg-gray-800 rounded-lg p-6 border border-gray-700">
            <div className="text-3xl font-bold text-spotify-green mb-1">
              {data.topArtists.count}
            </div>
            <div className="text-gray-400 text-sm">Top Artists</div>
          </div>
          <div className="bg-gray-800 rounded-lg p-6 border border-gray-700">
            <div className="text-3xl font-bold text-spotify-green mb-1">
              {data.topAlbums.count}
            </div>
            <div className="text-gray-400 text-sm">Top Albums</div>
          </div>
          <div className="bg-gray-800 rounded-lg p-6 border border-gray-700">
            <div className="text-3xl font-bold text-spotify-green mb-1">
              {data.topGenres.count}
            </div>
            <div className="text-gray-400 text-sm">Unique Genres</div>
          </div>
        </div>

        {/* Top Tracks */}
        <section className="mb-12">
          <h2 className="text-3xl font-bold text-white mb-6">🎵 Your Top Tracks</h2>
          <div className="space-y-3">
            {data.topTracks.items.map((track, index) => (
              <TrackCard key={track.id} track={track} rank={index + 1} />
            ))}
          </div>
        </section>

        {/* Top Artists */}
        <section className="mb-12">
          <h2 className="text-3xl font-bold text-white mb-6">🎤 Your Top Artists</h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5 gap-6">
            {data.topArtists.items.map((artist, index) => (
              <ArtistCard key={artist.id} artist={artist} rank={index + 1} />
            ))}
          </div>
        </section>

        {/* Top Genres */}
        <section className="mb-12">
          <h2 className="text-3xl font-bold text-white mb-6">🎸 Your Top Genres</h2>
          <div className="bg-gray-800 rounded-lg p-6 border border-gray-700">
            <div className="flex flex-wrap gap-3">
              {topGenres.map((genre, index) => (
                <div
                  key={genre}
                  className="bg-spotify-green/20 hover:bg-spotify-green/30 text-spotify-green px-4 py-2 rounded-full text-sm font-medium transition-colors cursor-default"
                >
                  #{index + 1} {genre}
                </div>
              ))}
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};

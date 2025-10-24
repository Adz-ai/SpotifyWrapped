import type { FC } from 'react';

export const LoadingSpinner: FC = () => {
  return (
    <div className="min-h-screen bg-gradient-to-br from-spotify-black via-gray-900 to-spotify-black flex items-center justify-center">
      <div className="text-center">
        <div className="inline-block animate-spin rounded-full h-16 w-16 border-t-4 border-b-4 border-spotify-green mb-4"></div>
        <p className="text-white text-lg">Loading your Spotify data...</p>
      </div>
    </div>
  );
};

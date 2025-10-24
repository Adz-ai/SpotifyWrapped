import type { FC } from 'react';
import type { TrackDto } from '../types/spotify';

interface TrackCardProps {
  track: TrackDto;
  rank: number;
}

export const TrackCard: FC<TrackCardProps> = ({ track, rank }) => {
  const albumImage = track.album.images[0]?.url ?? '';
  const artistNames = track.artists.map((artist) => artist.name).join(', ');
  const durationMinutes = Math.floor(track.duration_ms / 60000);
  const durationSeconds = Math.floor((track.duration_ms % 60000) / 1000);

  return (
    <div className="bg-gray-800 rounded-lg p-4 hover:bg-gray-750 transition-colors duration-200 border border-gray-700">
      <div className="flex items-center space-x-4">
        <div className="flex-shrink-0 text-2xl font-bold text-gray-500 w-8">#{rank}</div>

        {albumImage && (
          <img
            src={albumImage}
            alt={track.album.name}
            className="w-16 h-16 rounded-md shadow-lg"
          />
        )}

        <div className="flex-1 min-w-0">
          <h3 className="text-white font-semibold truncate">{track.name}</h3>
          <p className="text-gray-400 text-sm truncate">{artistNames}</p>
          <div className="flex items-center space-x-3 mt-1">
            <span className="text-xs text-gray-500">{track.album.name}</span>
            <span className="text-xs text-gray-600">•</span>
            <span className="text-xs text-gray-500">
              {durationMinutes}:{durationSeconds.toString().padStart(2, '0')}
            </span>
          </div>
        </div>

        <div className="flex-shrink-0">
          <div className="bg-spotify-green/20 text-spotify-green px-3 py-1 rounded-full text-sm font-medium">
            {track.popularity}
          </div>
        </div>
      </div>
    </div>
  );
};

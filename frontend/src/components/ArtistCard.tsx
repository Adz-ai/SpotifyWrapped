import type { FC } from 'react';
import type { ArtistDto } from '../types/spotify';

interface ArtistCardProps {
  artist: ArtistDto;
  rank: number;
}

export const ArtistCard: FC<ArtistCardProps> = ({ artist, rank }) => {
  const artistImage = artist.images[0]?.url ?? '';
  const genres = artist.genres.slice(0, 3).join(', ');

  return (
    <div className="bg-gray-800 rounded-lg p-6 hover:bg-gray-750 transition-all duration-200 border border-gray-700 hover:border-spotify-green/50 group">
      <div className="flex flex-col items-center text-center">
        <div className="relative mb-4">
          <div className="absolute -top-2 -left-2 bg-spotify-green text-white w-8 h-8 rounded-full flex items-center justify-center font-bold text-sm shadow-lg">
            {rank}
          </div>
          {artistImage ? (
            <img
              src={artistImage}
              alt={artist.name}
              className="w-32 h-32 rounded-full object-cover shadow-xl ring-4 ring-gray-700 group-hover:ring-spotify-green/50 transition-all"
            />
          ) : (
            <div className="w-32 h-32 rounded-full bg-gray-700 flex items-center justify-center">
              <svg
                className="w-16 h-16 text-gray-500"
                fill="currentColor"
                viewBox="0 0 20 20"
              >
                <path
                  fillRule="evenodd"
                  d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
                  clipRule="evenodd"
                />
              </svg>
            </div>
          )}
        </div>

        <h3 className="text-white font-bold text-lg mb-2 truncate w-full">{artist.name}</h3>

        {genres && <p className="text-gray-400 text-sm mb-3 truncate w-full">{genres}</p>}

        <div className="flex items-center space-x-2">
          <div className="bg-spotify-green/20 text-spotify-green px-3 py-1 rounded-full text-xs font-medium">
            Popularity: {artist.popularity}
          </div>
        </div>
      </div>
    </div>
  );
};

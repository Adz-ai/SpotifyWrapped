import type { FC } from 'react';

interface LoginProps {
  onLogin: () => void;
}

export const Login: FC<LoginProps> = ({ onLogin }) => {
  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-spotify-black via-gray-900 to-spotify-black">
      <div className="max-w-md w-full mx-4">
        <div className="bg-gray-800 rounded-2xl shadow-2xl p-8 border border-gray-700">
          <div className="text-center">
            <div className="mb-8">
              <svg
                className="w-20 h-20 mx-auto text-spotify-green"
                fill="currentColor"
                viewBox="0 0 168 168"
              >
                <path d="M83.996 0C37.613 0 0 37.614 0 83.998 0 130.383 37.613 168 83.996 168c46.38 0 83.997-37.617 83.997-83.99 0-46.405-37.617-84.01-83.997-84.01zm38.539 121.085c-1.496 2.463-4.69 3.25-7.145 1.76-19.613-11.977-44.266-14.676-73.297-8.049-2.795.638-5.56-1.043-6.191-3.837-.634-2.787 1.04-5.557 3.833-6.186 31.698-7.246 58.857-4.117 80.37 9.294 2.466 1.483 3.26 4.676 1.77 7.13zm10.199-22.665c-1.89 3.07-5.905 4.038-8.958 2.153-22.448-13.805-56.685-17.813-83.249-9.738-3.53 1.068-7.248-1.317-8.312-4.838-1.062-3.523 1.31-7.253 4.838-8.317 30.408-9.236 68.07-4.758 94.014 11.13 3.06 1.884 4.027 5.897 2.137 8.966zm.876-23.598c-26.945-16.003-71.407-17.48-97.118-9.669-4.248 1.29-8.723-1.105-10.013-5.35-1.29-4.248 1.104-8.722 5.35-10.01 29.532-8.978 78.445-7.245 109.46 11.196 3.722 2.206 4.943 7.016 2.73 10.73-2.22 3.728-7.02 4.95-10.73 2.736z" />
              </svg>
            </div>

            <h1 className="text-4xl font-bold text-white mb-2">Spotify Wrapped</h1>
            <p className="text-gray-400 mb-8">
              Discover your top tracks, artists, and music taste
            </p>

            <button
              onClick={onLogin}
              className="w-full bg-spotify-green hover:bg-green-500 text-white font-bold py-4 px-6 rounded-full transition-all duration-200 transform hover:scale-105 shadow-lg hover:shadow-xl"
            >
              Login with Spotify
            </button>

            <p className="mt-6 text-sm text-gray-500">
              We'll need access to your top tracks and artists
            </p>
          </div>
        </div>

        <div className="mt-8 text-center">
          <p className="text-gray-600 text-sm">
            Built with React, TypeScript, Spring Boot & Java 21
          </p>
        </div>
      </div>
    </div>
  );
};

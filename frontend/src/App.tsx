import type { FC } from 'react';
import { useAuth } from './hooks/useAuth';
import { Login } from './components/Login';
import { Dashboard } from './components/Dashboard';
import { LoadingSpinner } from './components/LoadingSpinner';

export const App: FC = () => {
  const { isAuthenticated, user, isLoading, login, logout } = useAuth();

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Login onLogin={login} />;
  }

  return <Dashboard user={user} onLogout={logout} />;
};

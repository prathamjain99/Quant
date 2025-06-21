import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/api';

interface User {
  id: string;
  username: string;
  email: string;
  role: string;
  name: string;
  isActive: boolean;
  isEmailVerified: boolean;
  lastLogin?: string;
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
  loading: boolean;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    initializeAuth();
  }, []);

  const initializeAuth = async () => {
    try {
      const token = localStorage.getItem('token');
      const userData = localStorage.getItem('user');
      
      if (token && userData) {
        // Validate token with backend
        const response = await authAPI.validate();
        if (response.data.valid) {
          setUser(JSON.parse(userData));
        } else {
          // Token is invalid, clear storage
          clearAuthData();
        }
      }
    } catch (error) {
      console.error('Auth initialization error:', error);
      clearAuthData();
    } finally {
      setLoading(false);
    }
  };

  const login = async (username: string, password: string) => {
    try {
      console.log('Attempting login with:', { username });
      
      const response = await authAPI.login({ username, password });
      console.log('Login response:', response.data);

      const { token, refreshToken, user: userData, expiresAt } = response.data;
      
      if (!token || !userData) {
        throw new Error('Invalid response from server');
      }
      
      // Store authentication data
      localStorage.setItem('token', token);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('user', JSON.stringify(userData));
      localStorage.setItem('tokenExpiry', expiresAt);
      
      setUser(userData);
      
      // Set up token refresh timer
      setupTokenRefresh(expiresAt);
      
    } catch (error: any) {
      console.error('Login error:', error);
      if (error.response?.status === 400 || error.response?.status === 401) {
        throw new Error(error.response.data.message || 'Invalid username or password');
      } else if (error.code === 'ECONNREFUSED' || error.code === 'ERR_NETWORK') {
        throw new Error('Unable to connect to server. Please ensure the backend services are running.');
      }
      throw new Error('Login failed. Please try again.');
    }
  };

  const refreshToken = async () => {
    try {
      const refreshTokenValue = localStorage.getItem('refreshToken');
      if (!refreshTokenValue) {
        throw new Error('No refresh token available');
      }

      const response = await authAPI.refresh({ refreshToken: refreshTokenValue });
      const { token, expiresAt } = response.data;

      localStorage.setItem('token', token);
      localStorage.setItem('tokenExpiry', expiresAt);

      setupTokenRefresh(expiresAt);
    } catch (error) {
      console.error('Token refresh failed:', error);
      logout();
    }
  };

  const setupTokenRefresh = (expiresAt: string) => {
    const expiryTime = new Date(expiresAt).getTime();
    const currentTime = Date.now();
    const timeUntilRefresh = expiryTime - currentTime - 60000; // Refresh 1 minute before expiry

    if (timeUntilRefresh > 0) {
      setTimeout(() => {
        refreshToken();
      }, timeUntilRefresh);
    }
  };

  const logout = async () => {
    try {
      // Notify server about logout
      await authAPI.logout();
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      clearAuthData();
    }
  };

  const clearAuthData = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    localStorage.removeItem('tokenExpiry');
    setUser(null);
  };

  const isAuthenticated = !!user;

  return (
    <AuthContext.Provider value={{ 
      user, 
      login, 
      logout, 
      refreshToken, 
      loading, 
      isAuthenticated 
    }}>
      {children}
    </AuthContext.Provider>
  );
};
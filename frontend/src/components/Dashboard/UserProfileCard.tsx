import React from 'react';
import { motion } from 'framer-motion';
import { User, Mail, Calendar, Shield, Clock } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface UserProfileCardProps {
  userProfile?: any;
  loading?: boolean;
}

const UserProfileCard: React.FC<UserProfileCardProps> = ({ userProfile, loading }) => {
  const { user } = useAuth();

  if (loading) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <div className="animate-pulse">
          <div className="flex items-center space-x-4">
            <div className="h-16 w-16 bg-gray-700 rounded-full"></div>
            <div className="space-y-2">
              <div className="h-4 bg-gray-700 rounded w-32"></div>
              <div className="h-3 bg-gray-700 rounded w-24"></div>
            </div>
          </div>
        </div>
      </motion.div>
    );
  }

  const profile = userProfile || user;

  const getRoleColor = (role: string) => {
    switch (role?.toLowerCase()) {
      case 'admin': return 'text-red-400 bg-red-600/20';
      case 'portfolio_manager': return 'text-green-400 bg-green-600/20';
      case 'researcher': return 'text-purple-400 bg-purple-600/20';
      case 'client': return 'text-blue-400 bg-blue-600/20';
      default: return 'text-gray-400 bg-gray-600/20';
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  };

  const formatDateTime = (dateString: string) => {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-colors"
    >
      <div className="flex items-start justify-between mb-4">
        <h3 className="text-lg font-semibold text-white flex items-center">
          <User className="h-5 w-5 mr-2 text-blue-400" />
          User Profile
        </h3>
        <div className={`px-3 py-1 rounded-full text-xs font-medium ${getRoleColor(profile?.role)}`}>
          {profile?.role?.replace('_', ' ').toUpperCase() || 'USER'}
        </div>
      </div>

      <div className="flex items-center space-x-4 mb-6">
        <div className="h-16 w-16 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center">
          <span className="text-white text-xl font-bold">
            {profile?.name?.charAt(0)?.toUpperCase() || profile?.username?.charAt(0)?.toUpperCase() || 'U'}
          </span>
        </div>
        <div>
          <h4 className="text-xl font-bold text-white">{profile?.name || 'Unknown User'}</h4>
          <p className="text-gray-400">@{profile?.username || 'unknown'}</p>
        </div>
      </div>

      <div className="space-y-3">
        <div className="flex items-center space-x-3">
          <Mail className="h-4 w-4 text-gray-400" />
          <span className="text-gray-300">{profile?.email || 'No email'}</span>
        </div>

        <div className="flex items-center space-x-3">
          <Calendar className="h-4 w-4 text-gray-400" />
          <span className="text-gray-300">
            Joined {formatDate(profile?.createdAt)}
          </span>
        </div>

        <div className="flex items-center space-x-3">
          <Clock className="h-4 w-4 text-gray-400" />
          <span className="text-gray-300">
            Last login: {formatDateTime(profile?.lastLogin)}
          </span>
        </div>

        <div className="flex items-center space-x-3">
          <Shield className="h-4 w-4 text-gray-400" />
          <div className="flex items-center space-x-2">
            <span className="text-gray-300">Account Status:</span>
            <span className={`px-2 py-1 rounded text-xs ${
              profile?.isActive ? 'bg-green-600/20 text-green-400' : 'bg-red-600/20 text-red-400'
            }`}>
              {profile?.isActive ? 'Active' : 'Inactive'}
            </span>
          </div>
        </div>

        {profile?.department && (
          <div className="flex items-center space-x-3">
            <div className="h-4 w-4 text-gray-400 flex items-center justify-center">
              <div className="h-2 w-2 bg-gray-400 rounded-full"></div>
            </div>
            <span className="text-gray-300">Department: {profile.department}</span>
          </div>
        )}

        {profile?.activeSessions !== undefined && (
          <div className="flex items-center space-x-3">
            <div className="h-4 w-4 text-gray-400 flex items-center justify-center">
              <div className="h-2 w-2 bg-green-400 rounded-full animate-pulse"></div>
            </div>
            <span className="text-gray-300">
              Active Sessions: {profile.activeSessions}
            </span>
          </div>
        )}
      </div>
    </motion.div>
  );
};

export default UserProfileCard;
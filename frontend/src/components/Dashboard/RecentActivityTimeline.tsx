import React from 'react';
import { motion } from 'framer-motion';
import { 
  Clock, 
  Plus, 
  TestTube, 
  Package, 
  TrendingUp, 
  FileText,
  User,
  Shield,
  Activity
} from 'lucide-react';

interface RecentActivityTimelineProps {
  activities?: any[];
  loading?: boolean;
}

const RecentActivityTimeline: React.FC<RecentActivityTimelineProps> = ({ activities, loading }) => {
  if (loading) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
          <Clock className="h-5 w-5 mr-2 text-blue-400" />
          Recent Activity
        </h3>
        <div className="space-y-4">
          {[...Array(5)].map((_, index) => (
            <div key={index} className="animate-pulse flex items-start space-x-3">
              <div className="h-8 w-8 bg-gray-700 rounded-full"></div>
              <div className="flex-1 space-y-2">
                <div className="h-4 bg-gray-700 rounded w-3/4"></div>
                <div className="h-3 bg-gray-700 rounded w-1/2"></div>
              </div>
            </div>
          ))}
        </div>
      </motion.div>
    );
  }

  const getActivityIcon = (activityType: string) => {
    switch (activityType) {
      case 'STRATEGY_CREATED':
      case 'STRATEGY_UPDATED':
        return Plus;
      case 'BACKTEST_STARTED':
      case 'BACKTEST_COMPLETED':
        return TestTube;
      case 'PRODUCT_CREATED':
        return Package;
      case 'TRADE_BOOKED':
        return TrendingUp;
      case 'REPORT_GENERATED':
        return FileText;
      case 'LOGIN':
      case 'LOGOUT':
        return User;
      case 'PROFILE_UPDATED':
      case 'PASSWORD_CHANGED':
        return Shield;
      default:
        return Activity;
    }
  };

  const getActivityColor = (activityType: string) => {
    switch (activityType) {
      case 'STRATEGY_CREATED':
      case 'STRATEGY_UPDATED':
        return 'text-blue-400 bg-blue-600/20';
      case 'BACKTEST_STARTED':
      case 'BACKTEST_COMPLETED':
        return 'text-yellow-400 bg-yellow-600/20';
      case 'PRODUCT_CREATED':
        return 'text-green-400 bg-green-600/20';
      case 'TRADE_BOOKED':
        return 'text-purple-400 bg-purple-600/20';
      case 'REPORT_GENERATED':
        return 'text-indigo-400 bg-indigo-600/20';
      case 'LOGIN':
        return 'text-green-400 bg-green-600/20';
      case 'LOGOUT':
        return 'text-red-400 bg-red-600/20';
      case 'PROFILE_UPDATED':
      case 'PASSWORD_CHANGED':
        return 'text-orange-400 bg-orange-600/20';
      default:
        return 'text-gray-400 bg-gray-600/20';
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.4 }}
      className="bg-gray-800 border border-gray-700 rounded-xl p-6"
    >
      <h3 className="text-xl font-semibold text-white mb-6 flex items-center">
        <Clock className="h-5 w-5 mr-2 text-blue-400" />
        Recent Activity
      </h3>

      {activities && activities.length > 0 ? (
        <div className="space-y-4">
          {activities.map((activity, index) => {
            const Icon = getActivityIcon(activity.activityType);
            const colorClass = getActivityColor(activity.activityType);
            
            return (
              <motion.div
                key={activity.id || index}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                transition={{ delay: index * 0.1 }}
                className="flex items-start space-x-3 p-3 bg-gray-700/30 rounded-lg hover:bg-gray-700/50 transition-colors"
              >
                <div className={`p-2 rounded-full ${colorClass}`}>
                  <Icon className="h-4 w-4" />
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-white text-sm font-medium">
                    {activity.description}
                  </p>
                  <div className="flex items-center justify-between mt-1">
                    <p className="text-gray-400 text-xs">
                      {activity.timeAgo || 'Unknown time'}
                    </p>
                    {activity.entityType && (
                      <span className="px-2 py-1 bg-gray-600/50 text-gray-300 text-xs rounded">
                        {activity.entityType.replace('_', ' ').toLowerCase()}
                      </span>
                    )}
                  </div>
                </div>
              </motion.div>
            );
          })}
        </div>
      ) : (
        <div className="text-center py-8">
          <Activity className="h-12 w-12 text-gray-600 mx-auto mb-3" />
          <p className="text-gray-400 text-sm">No recent activity</p>
          <p className="text-gray-500 text-xs mt-1">
            Start using the platform to see your activity here
          </p>
        </div>
      )}

      {activities && activities.length > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-700">
          <button className="text-blue-400 hover:text-blue-300 text-sm font-medium transition-colors">
            View All Activity →
          </button>
        </div>
      )}
    </motion.div>
  );
};

export default RecentActivityTimeline;
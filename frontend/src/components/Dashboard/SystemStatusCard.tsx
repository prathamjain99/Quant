import React from 'react';
import { motion } from 'framer-motion';
import { 
  Server, 
  Database, 
  Activity, 
  Cpu, 
  HardDrive, 
  Users,
  CheckCircle,
  AlertTriangle,
  XCircle,
  RefreshCw
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface SystemStatusCardProps {
  systemStatus?: any;
  loading?: boolean;
  onRefresh?: () => void;
}

const SystemStatusCard: React.FC<SystemStatusCardProps> = ({ 
  systemStatus, 
  loading, 
  onRefresh 
}) => {
  const { user } = useAuth();

  // Only show to admin users
  if (user?.role !== 'admin') {
    return null;
  }

  if (loading) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-xl font-semibold text-white flex items-center">
            <Server className="h-5 w-5 mr-2 text-blue-400" />
            System Status
          </h3>
          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-500"></div>
        </div>
        <div className="space-y-4">
          {[...Array(4)].map((_, index) => (
            <div key={index} className="animate-pulse flex items-center space-x-3">
              <div className="h-8 w-8 bg-gray-700 rounded-lg"></div>
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

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'ONLINE':
      case 'HEALTHY':
        return CheckCircle;
      case 'WARNING':
        return AlertTriangle;
      case 'OFFLINE':
      case 'ERROR':
        return XCircle;
      default:
        return Activity;
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'ONLINE':
      case 'HEALTHY':
        return 'text-green-400';
      case 'WARNING':
        return 'text-yellow-400';
      case 'OFFLINE':
      case 'ERROR':
        return 'text-red-400';
      default:
        return 'text-gray-400';
    }
  };

  const services = systemStatus ? [
    {
      name: 'Database',
      status: systemStatus.database?.status || 'UNKNOWN',
      details: systemStatus.database ? 
        `${systemStatus.database.activeConnections}/${systemStatus.database.maxConnections} connections` :
        'No data',
      icon: Database,
      responseTime: systemStatus.database?.responseTimeMs
    },
    {
      name: 'Backtest Engine',
      status: systemStatus.backtestEngine?.status || 'UNKNOWN',
      details: systemStatus.backtestEngine?.version || 'Unknown version',
      icon: Activity,
      responseTime: systemStatus.backtestEngine?.responseTimeMs
    },
    {
      name: 'Analytics Engine',
      status: systemStatus.analyticsEngine?.status || 'UNKNOWN',
      details: systemStatus.analyticsEngine?.version || 'Unknown version',
      icon: Activity,
      responseTime: systemStatus.analyticsEngine?.responseTimeMs
    },
    {
      name: 'Market Data Service',
      status: systemStatus.marketDataService?.status || 'UNKNOWN',
      details: systemStatus.marketDataService?.version || 'Unknown version',
      icon: Activity,
      responseTime: systemStatus.marketDataService?.responseTimeMs
    }
  ] : [];

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.6 }}
      className="bg-gray-800 border border-gray-700 rounded-xl p-6"
    >
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-xl font-semibold text-white flex items-center">
          <Server className="h-5 w-5 mr-2 text-blue-400" />
          System Status
        </h3>
        <div className="flex items-center space-x-3">
          {systemStatus && (
            <div className="flex items-center space-x-2">
              <div className={`h-2 w-2 rounded-full ${
                systemStatus.overallStatus === 'HEALTHY' ? 'bg-green-500' :
                systemStatus.overallStatus === 'WARNING' ? 'bg-yellow-500' : 'bg-red-500'
              }`}></div>
              <span className={`text-sm font-medium ${getStatusColor(systemStatus.overallStatus)}`}>
                {systemStatus.overallStatus}
              </span>
            </div>
          )}
          {onRefresh && (
            <motion.button
              whileHover={{ scale: 1.1 }}
              whileTap={{ scale: 0.9 }}
              onClick={onRefresh}
              className="p-1 text-gray-400 hover:text-white transition-colors"
            >
              <RefreshCw className="h-4 w-4" />
            </motion.button>
          )}
        </div>
      </div>

      {systemStatus ? (
        <div className="space-y-4">
          {/* Services Status */}
          <div className="space-y-3">
            {services.map((service, index) => {
              const StatusIcon = getStatusIcon(service.status);
              const statusColor = getStatusColor(service.status);
              
              return (
                <motion.div
                  key={service.name}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.1 }}
                  className="flex items-center justify-between p-3 bg-gray-700/30 rounded-lg"
                >
                  <div className="flex items-center space-x-3">
                    <service.icon className="h-5 w-5 text-gray-400" />
                    <div>
                      <p className="text-white text-sm font-medium">{service.name}</p>
                      <p className="text-gray-400 text-xs">{service.details}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2">
                    {service.responseTime && (
                      <span className="text-gray-400 text-xs">
                        {service.responseTime}ms
                      </span>
                    )}
                    <StatusIcon className={`h-4 w-4 ${statusColor}`} />
                  </div>
                </motion.div>
              );
            })}
          </div>

          {/* System Metrics */}
          {systemStatus.metrics && (
            <div className="pt-4 border-t border-gray-700">
              <h4 className="text-white font-medium mb-3">System Metrics</h4>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-400 text-sm flex items-center">
                      <Cpu className="h-4 w-4 mr-1" />
                      CPU Usage
                    </span>
                    <span className="text-white text-sm">
                      {systemStatus.metrics.cpuUsage?.toFixed(1)}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-700 rounded-full h-2">
                    <div 
                      className="bg-blue-500 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${systemStatus.metrics.cpuUsage || 0}%` }}
                    ></div>
                  </div>
                </div>

                <div className="space-y-2">
                  <div className="flex items-center justify-between">
                    <span className="text-gray-400 text-sm flex items-center">
                      <HardDrive className="h-4 w-4 mr-1" />
                      Memory
                    </span>
                    <span className="text-white text-sm">
                      {systemStatus.metrics.memoryUsage?.toFixed(1)}%
                    </span>
                  </div>
                  <div className="w-full bg-gray-700 rounded-full h-2">
                    <div 
                      className="bg-green-500 h-2 rounded-full transition-all duration-300"
                      style={{ width: `${systemStatus.metrics.memoryUsage || 0}%` }}
                    ></div>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <span className="text-gray-400 text-sm flex items-center">
                    <Users className="h-4 w-4 mr-1" />
                    Active Users
                  </span>
                  <span className="text-white text-sm">
                    {systemStatus.metrics.activeUsers || 0}
                  </span>
                </div>

                <div className="flex items-center justify-between">
                  <span className="text-gray-400 text-sm">
                    Total Sessions
                  </span>
                  <span className="text-white text-sm">
                    {systemStatus.metrics.totalSessions || 0}
                  </span>
                </div>
              </div>
            </div>
          )}
        </div>
      ) : (
        <div className="text-center py-8">
          <Server className="h-12 w-12 text-gray-600 mx-auto mb-3" />
          <p className="text-gray-400 text-sm">System status unavailable</p>
          <p className="text-gray-500 text-xs mt-1">
            Unable to fetch system status information
          </p>
        </div>
      )}
    </motion.div>
  );
};

export default SystemStatusCard;
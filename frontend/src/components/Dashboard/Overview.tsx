import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';
import { Wifi, WifiOff, RefreshCw } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import UserProfileCard from './UserProfileCard';
import QuickStatsCards from './QuickStatsCards';
import QuickActionsPanel from './QuickActionsPanel';
import RecentActivityTimeline from './RecentActivityTimeline';
import MarketPreviewCards from './MarketPreviewCards';
import SystemStatusCard from './SystemStatusCard';
import { dashboardAPI } from '../../services/api';
import toast from 'react-hot-toast';

const Overview: React.FC = () => {
  const { user } = useAuth();
  const [loading, setLoading] = useState(true);
  const [connectionStatus, setConnectionStatus] = useState<'connected' | 'disconnected' | 'checking'>('checking');
  
  // Dashboard data states
  const [userProfile, setUserProfile] = useState<any>(null);
  const [userSummary, setUserSummary] = useState<any>(null);
  const [recentActivity, setRecentActivity] = useState<any[]>([]);
  const [marketData, setMarketData] = useState<any[]>([]);
  const [systemStatus, setSystemStatus] = useState<any>(null);

  useEffect(() => {
    initializeDashboard();
  }, []);

  const initializeDashboard = async () => {
    setLoading(true);
    try {
      await Promise.all([
        checkConnection(),
        fetchUserProfile(),
        fetchUserSummary(),
        fetchRecentActivity(),
        fetchMarketData(),
        fetchSystemStatus()
      ]);
    } catch (error) {
      console.error('Dashboard initialization error:', error);
    } finally {
      setLoading(false);
    }
  };

  const checkConnection = async () => {
    try {
      await dashboardAPI.healthCheck();
      setConnectionStatus('connected');
    } catch (error) {
      setConnectionStatus('disconnected');
    }
  };

  const fetchUserProfile = async () => {
    try {
      const response = await dashboardAPI.getUserProfile();
      setUserProfile(response.data);
    } catch (error) {
      console.error('Failed to fetch user profile:', error);
      // Use auth context user as fallback
      setUserProfile(user);
    }
  };

  const fetchUserSummary = async () => {
    try {
      const response = await dashboardAPI.getUserSummary();
      setUserSummary(response.data);
    } catch (error) {
      console.error('Failed to fetch user summary:', error);
      // Set mock data for demo
      setUserSummary({
        strategiesCount: 0,
        backtestsCount: 0,
        analyticsReportsCount: 0,
        portfolioSimulationsCount: 0,
        tradesCount: 0,
        productsCount: 0,
        totalPortfolioValue: 0,
        activeSessions: 1,
        mostUsedFeature: 'None'
      });
    }
  };

  const fetchRecentActivity = async () => {
    try {
      const response = await dashboardAPI.getUserActivity(10);
      setRecentActivity(response.data);
    } catch (error) {
      console.error('Failed to fetch recent activity:', error);
      setRecentActivity([]);
    }
  };

  const fetchMarketData = async () => {
    try {
      const response = await dashboardAPI.getMarketSnapshot();
      setMarketData(response.data);
    } catch (error) {
      console.error('Failed to fetch market data:', error);
      setMarketData([]);
    }
  };

  const fetchSystemStatus = async () => {
    try {
      if (user?.role === 'admin') {
        const response = await dashboardAPI.getSystemStatus();
        setSystemStatus(response.data);
      }
    } catch (error) {
      console.error('Failed to fetch system status:', error);
      setSystemStatus(null);
    }
  };

  const handleRefreshMarketData = async () => {
    try {
      await dashboardAPI.simulateMarketUpdate();
      await fetchMarketData();
      toast.success('Market data updated');
    } catch (error) {
      toast.error('Failed to update market data');
    }
  };

  const handleRefreshSystemStatus = async () => {
    try {
      await fetchSystemStatus();
      toast.success('System status refreshed');
    } catch (error) {
      toast.error('Failed to refresh system status');
    }
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Dashboard Overview</h1>
          <p className="text-gray-400 mt-1">Welcome back, {user?.name?.split(' ')[0] || 'User'}</p>
        </div>
        <div className="flex items-center space-x-4">
          <div className="flex items-center space-x-2 text-sm">
            {connectionStatus === 'connected' ? (
              <>
                <Wifi className="h-4 w-4 text-green-400" />
                <span className="text-green-400">Connected</span>
              </>
            ) : connectionStatus === 'disconnected' ? (
              <>
                <WifiOff className="h-4 w-4 text-red-400" />
                <span className="text-red-400">Disconnected</span>
              </>
            ) : (
              <>
                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-yellow-400"></div>
                <span className="text-yellow-400">Checking...</span>
              </>
            )}
          </div>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={initializeDashboard}
            className="flex items-center space-x-2 px-3 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg text-sm"
          >
            <RefreshCw className="h-4 w-4" />
            <span>Refresh</span>
          </motion.button>
        </div>
      </motion.div>

      {/* Connection Status Alert */}
      {connectionStatus === 'disconnected' && (
        <motion.div
          initial={{ opacity: 0, y: -10 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-red-600/20 border border-red-500/30 rounded-lg p-4"
        >
          <div className="flex items-center space-x-2">
            <WifiOff className="h-5 w-5 text-red-400" />
            <div>
              <h3 className="text-red-400 font-medium">Backend Services Disconnected</h3>
              <p className="text-red-300 text-sm mt-1">
                Some features may not work properly. Please check your backend services.
              </p>
            </div>
          </div>
        </motion.div>
      )}

      {/* Quick Stats */}
      <QuickStatsCards summary={userSummary} loading={loading} />

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column */}
        <div className="lg:col-span-2 space-y-6">
          {/* Quick Actions */}
          <QuickActionsPanel />
          
          {/* Market Preview */}
          <MarketPreviewCards 
            marketData={marketData} 
            loading={loading}
            onRefresh={handleRefreshMarketData}
          />
        </div>

        {/* Right Column */}
        <div className="space-y-6">
          {/* User Profile */}
          <UserProfileCard userProfile={userProfile} loading={loading} />
          
          {/* Recent Activity */}
          <RecentActivityTimeline activities={recentActivity} loading={loading} />
          
          {/* System Status (Admin Only) */}
          <SystemStatusCard 
            systemStatus={systemStatus} 
            loading={loading}
            onRefresh={handleRefreshSystemStatus}
          />
        </div>
      </div>

      {/* Welcome Message for New Users */}
      {userSummary && 
       userSummary.strategiesCount === 0 && 
       userSummary.tradesCount === 0 && 
       userSummary.productsCount === 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.8 }}
          className="bg-gradient-to-r from-blue-600/20 to-purple-600/20 border border-blue-500/30 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-2">Welcome to QuantCrux!</h3>
          <p className="text-blue-300 mb-4">
            Get started by exploring the platform features. Here are some suggestions based on your role:
          </p>
          <div className="space-y-2 text-sm">
            {user?.role === 'researcher' && (
              <>
                <p className="text-blue-200">• Create your first quantitative strategy</p>
                <p className="text-blue-200">• Run backtests to validate your ideas</p>
                <p className="text-blue-200">• Analyze performance metrics</p>
              </>
            )}
            {user?.role === 'portfolio_manager' && (
              <>
                <p className="text-blue-200">• Design structured financial products</p>
                <p className="text-blue-200">• Use the trading desk to book trades</p>
                <p className="text-blue-200">• Monitor portfolio performance</p>
              </>
            )}
            {user?.role === 'client' && (
              <>
                <p className="text-blue-200">• View your portfolio and positions</p>
                <p className="text-blue-200">• Execute trades through the trading desk</p>
                <p className="text-blue-200">• Monitor market data and analytics</p>
              </>
            )}
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default Overview;
import React from 'react';
import { motion } from 'framer-motion';
import { 
  Strategy as StrategyIcon, 
  TestTube, 
  FileText, 
  PieChart, 
  TrendingUp, 
  Package,
  Activity,
  DollarSign
} from 'lucide-react';

interface QuickStatsCardsProps {
  summary?: any;
  loading?: boolean;
}

const QuickStatsCards: React.FC<QuickStatsCardsProps> = ({ summary, loading }) => {
  if (loading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[...Array(4)].map((_, index) => (
          <motion.div
            key={index}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6"
          >
            <div className="animate-pulse">
              <div className="flex items-center justify-between">
                <div className="space-y-2">
                  <div className="h-3 bg-gray-700 rounded w-20"></div>
                  <div className="h-6 bg-gray-700 rounded w-16"></div>
                </div>
                <div className="h-10 w-10 bg-gray-700 rounded-lg"></div>
              </div>
            </div>
          </motion.div>
        ))}
      </div>
    );
  }

  const stats = [
    {
      label: 'Strategies',
      value: summary?.strategiesCount || 0,
      icon: StrategyIcon,
      color: 'text-blue-400',
      bgColor: 'bg-blue-600/20',
      description: 'Created'
    },
    {
      label: 'Backtests',
      value: summary?.backtestsCount || 0,
      icon: TestTube,
      color: 'text-green-400',
      bgColor: 'bg-green-600/20',
      description: 'Completed'
    },
    {
      label: 'Reports',
      value: summary?.analyticsReportsCount || 0,
      icon: FileText,
      color: 'text-yellow-400',
      bgColor: 'bg-yellow-600/20',
      description: 'Generated'
    },
    {
      label: 'Trades',
      value: summary?.tradesCount || 0,
      icon: TrendingUp,
      color: 'text-purple-400',
      bgColor: 'bg-purple-600/20',
      description: 'Executed'
    }
  ];

  // Add additional stats based on user role
  if (summary?.productsCount !== undefined && summary.productsCount > 0) {
    stats.push({
      label: 'Products',
      value: summary.productsCount,
      icon: Package,
      color: 'text-indigo-400',
      bgColor: 'bg-indigo-600/20',
      description: 'Created'
    });
  }

  if (summary?.portfolioSimulationsCount !== undefined && summary.portfolioSimulationsCount > 0) {
    stats.push({
      label: 'Simulations',
      value: summary.portfolioSimulationsCount,
      icon: PieChart,
      color: 'text-pink-400',
      bgColor: 'bg-pink-600/20',
      description: 'Run'
    });
  }

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.slice(0, 4).map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-sm">{stat.label}</p>
                <p className="text-2xl font-bold text-white mt-1">{stat.value}</p>
                <p className="text-gray-500 text-xs mt-1">{stat.description}</p>
              </div>
              <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                <stat.icon className={`h-6 w-6 ${stat.color}`} />
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Additional stats row if available */}
      {stats.length > 4 && (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {stats.slice(4).map((stat, index) => (
            <motion.div
              key={stat.label}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: (index + 4) * 0.1 }}
              className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-colors"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-gray-400 text-sm">{stat.label}</p>
                  <p className="text-2xl font-bold text-white mt-1">{stat.value}</p>
                  <p className="text-gray-500 text-xs mt-1">{stat.description}</p>
                </div>
                <div className={`p-3 rounded-lg ${stat.bgColor}`}>
                  <stat.icon className={`h-6 w-6 ${stat.color}`} />
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}

      {/* Portfolio Value Card */}
      {summary?.totalPortfolioValue !== undefined && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.5 }}
          className="bg-gradient-to-r from-green-600/20 to-blue-600/20 border border-green-500/30 rounded-xl p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-green-400 text-sm font-medium">Total Portfolio Value</p>
              <p className="text-3xl font-bold text-white mt-1">
                ${summary.totalPortfolioValue.toLocaleString(undefined, {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2
                })}
              </p>
              <p className="text-green-300 text-sm mt-1">Current market value</p>
            </div>
            <div className="p-4 bg-green-600/30 rounded-lg">
              <DollarSign className="h-8 w-8 text-green-400" />
            </div>
          </div>
        </motion.div>
      )}

      {/* Most Used Feature */}
      {summary?.mostUsedFeature && summary.mostUsedFeature !== 'None' && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.6 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-4"
        >
          <div className="flex items-center space-x-3">
            <Activity className="h-5 w-5 text-blue-400" />
            <div>
              <p className="text-gray-400 text-sm">Most Used Feature</p>
              <p className="text-white font-medium">{summary.mostUsedFeature}</p>
            </div>
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default QuickStatsCards;
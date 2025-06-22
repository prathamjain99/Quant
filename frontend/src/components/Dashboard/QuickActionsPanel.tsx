import React from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import { 
  Plus, 
  FolderOpen, 
  TestTube, 
  BarChart3, 
  TrendingUp, 
  Package,
  DollarSign,
  FileText
} from 'lucide-react';

const QuickActionsPanel: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const getActionsForRole = () => {
    const baseActions = [
      {
        title: 'View Portfolio',
        description: 'Check your current positions and performance',
        icon: FolderOpen,
        color: 'from-blue-600 to-blue-700',
        hoverColor: 'from-blue-700 to-blue-800',
        path: '/dashboard/portfolio'
      },
      {
        title: 'Analytics',
        description: 'View risk metrics and performance analytics',
        icon: BarChart3,
        color: 'from-purple-600 to-purple-700',
        hoverColor: 'from-purple-700 to-purple-800',
        path: '/dashboard/analytics'
      },
      {
        title: 'Market View',
        description: 'Monitor market data and trends',
        icon: TrendingUp,
        color: 'from-green-600 to-green-700',
        hoverColor: 'from-green-700 to-green-800',
        path: '/dashboard/market'
      },
      {
        title: 'Reports',
        description: 'Generate and view reports',
        icon: FileText,
        color: 'from-gray-600 to-gray-700',
        hoverColor: 'from-gray-700 to-gray-800',
        path: '/dashboard/reports'
      }
    ];

    if (user?.role === 'researcher') {
      return [
        {
          title: 'Create Strategy',
          description: 'Build new quantitative trading strategy',
          icon: Plus,
          color: 'from-blue-600 to-blue-700',
          hoverColor: 'from-blue-700 to-blue-800',
          path: '/dashboard/strategies'
        },
        {
          title: 'View Strategies',
          description: 'Manage your existing strategies',
          icon: FolderOpen,
          color: 'from-indigo-600 to-indigo-700',
          hoverColor: 'from-indigo-700 to-indigo-800',
          path: '/dashboard/strategies'
        },
        {
          title: 'Run Backtest',
          description: 'Test strategy performance against historical data',
          icon: TestTube,
          color: 'from-yellow-600 to-yellow-700',
          hoverColor: 'from-yellow-700 to-yellow-800',
          path: '/dashboard/backtesting'
        },
        ...baseActions
      ];
    }

    if (user?.role === 'portfolio_manager') {
      return [
        {
          title: 'Create Product',
          description: 'Design structured financial product',
          icon: Package,
          color: 'from-green-600 to-green-700',
          hoverColor: 'from-green-700 to-green-800',
          path: '/dashboard/products'
        },
        {
          title: 'Trading Desk',
          description: 'Book trades and manage positions',
          icon: DollarSign,
          color: 'from-emerald-600 to-emerald-700',
          hoverColor: 'from-emerald-700 to-emerald-800',
          path: '/dashboard/trading'
        },
        {
          title: 'Create Strategy',
          description: 'Build quantitative trading strategies',
          icon: Plus,
          color: 'from-blue-600 to-blue-700',
          hoverColor: 'from-blue-700 to-blue-800',
          path: '/dashboard/strategies'
        },
        {
          title: 'Run Backtest',
          description: 'Test strategy performance',
          icon: TestTube,
          color: 'from-yellow-600 to-yellow-700',
          hoverColor: 'from-yellow-700 to-yellow-800',
          path: '/dashboard/backtesting'
        },
        ...baseActions
      ];
    }

    // Client role
    return [
      {
        title: 'Trading Desk',
        description: 'View and book trades',
        icon: DollarSign,
        color: 'from-emerald-600 to-emerald-700',
        hoverColor: 'from-emerald-700 to-emerald-800',
        path: '/dashboard/trading'
      },
      ...baseActions
    ];
  };

  const actions = getActionsForRole();

  const handleActionClick = (path: string) => {
    navigate(path);
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.3 }}
      className="bg-gray-800 border border-gray-700 rounded-xl p-6"
    >
      <h3 className="text-xl font-semibold text-white mb-6">Quick Actions</h3>
      
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
        {actions.map((action, index) => (
          <motion.button
            key={action.title}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: 0.1 * index }}
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            onClick={() => handleActionClick(action.path)}
            className={`p-4 bg-gradient-to-r ${action.color} hover:${action.hoverColor} rounded-lg text-white text-left transition-all duration-200 shadow-lg hover:shadow-xl`}
          >
            <div className="flex items-start space-x-3">
              <action.icon className="h-6 w-6 mt-1 flex-shrink-0" />
              <div className="min-w-0">
                <h4 className="font-semibold text-sm">{action.title}</h4>
                <p className="text-xs opacity-90 mt-1 line-clamp-2">{action.description}</p>
              </div>
            </div>
          </motion.button>
        ))}
      </div>

      {/* Role-specific note */}
      <div className="mt-6 p-3 bg-blue-600/20 border border-blue-500/30 rounded-lg">
        <p className="text-blue-400 text-sm">
          <strong>Role:</strong> {user?.role?.replace('_', ' ').toUpperCase()} - 
          {user?.role === 'researcher' && ' You can create strategies and run backtests'}
          {user?.role === 'portfolio_manager' && ' You have full access to all features including product creation'}
          {user?.role === 'client' && ' You can view portfolios and execute trades'}
          {user?.role === 'admin' && ' You have administrative access to all system features'}
        </p>
      </div>
    </motion.div>
  );
};

export default QuickActionsPanel;
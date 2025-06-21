import React from 'react';
import { NavLink } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuth } from '../../contexts/AuthContext';
import { TrendingUp, BarChart3, Vibrate as Strategy, PieChart, FileText, Briefcase, TestTube, Settings, Activity, Package, DollarSign } from 'lucide-react';

const Sidebar: React.FC = () => {
  const { user } = useAuth();

  const getMenuItems = () => {
    const baseItems = [
      { path: '/dashboard', icon: BarChart3, label: 'Overview', end: true },
      { path: '/dashboard/portfolio', icon: Briefcase, label: 'Portfolio' },
      { path: '/dashboard/analytics', icon: Activity, label: 'Analytics' },
      { path: '/dashboard/reports', icon: FileText, label: 'Reports' }
    ];

    if (user?.role === 'researcher') {
      return [
        ...baseItems.slice(0, 1),
        { path: '/dashboard/strategies', icon: Strategy, label: 'Strategy Builder' },
        { path: '/dashboard/backtesting', icon: TestTube, label: 'Backtesting' },
        ...baseItems.slice(1)
      ];
    }

    if (user?.role === 'portfolio_manager') {
      return [
        ...baseItems.slice(0, 1),
        { path: '/dashboard/products', icon: Package, label: 'Product Creator' },
        { path: '/dashboard/trading', icon: DollarSign, label: 'Trading Desk' },
        { path: '/dashboard/strategies', icon: Strategy, label: 'Strategies' },
        { path: '/dashboard/backtesting', icon: TestTube, label: 'Backtesting' },
        ...baseItems.slice(1)
      ];
    }

    // Client role gets trading desk access
    return [
      ...baseItems.slice(0, 1),
      { path: '/dashboard/trading', icon: DollarSign, label: 'Trading Desk' },
      ...baseItems.slice(1)
    ];
  };

  const menuItems = getMenuItems();

  return (
    <motion.div
      initial={{ x: -250 }}
      animate={{ x: 0 }}
      className="w-64 bg-gray-800 border-r border-gray-700 flex flex-col"
    >
      <div className="p-6 border-b border-gray-700">
        <div className="flex items-center space-x-3">
          <div className="p-2 bg-blue-600 rounded-lg">
            <TrendingUp className="h-6 w-6 text-white" />
          </div>
          <div>
            <h1 className="text-xl font-bold text-white">QuantCrux</h1>
            <p className="text-xs text-gray-400">{user?.role?.replace('_', ' ').toUpperCase()}</p>
          </div>
        </div>
      </div>

      <nav className="flex-1 py-6">
        <div className="space-y-2 px-4">
          {menuItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              end={item.end}
              className={({ isActive }) =>
                `flex items-center space-x-3 px-4 py-3 rounded-lg transition-all duration-200 ${
                  isActive
                    ? 'bg-blue-600 text-white shadow-lg'
                    : 'text-gray-300 hover:bg-gray-700 hover:text-white'
                }`
              }
            >
              <item.icon className="h-5 w-5" />
              <span className="font-medium">{item.label}</span>
            </NavLink>
          ))}
        </div>
      </nav>

      <div className="p-4 border-t border-gray-700">
        <div className="bg-gray-700 rounded-lg p-4">
          <div className="flex items-center space-x-3">
            <div className="h-10 w-10 bg-blue-600 rounded-full flex items-center justify-center">
              <span className="text-white font-semibold">
                {user?.name?.charAt(0) || 'U'}
              </span>
            </div>
            <div>
              <p className="text-white font-medium">{user?.name}</p>
              <p className="text-gray-400 text-sm">{user?.email}</p>
            </div>
          </div>
        </div>
      </div>
    </motion.div>
  );
};

export default Sidebar;
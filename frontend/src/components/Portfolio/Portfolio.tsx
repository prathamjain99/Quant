import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';
import { TrendingUp, TrendingDown, DollarSign, Package, AlertCircle, Calendar } from 'lucide-react';
import { portfolioAPI } from '../../services/api';

const Portfolio: React.FC = () => {
  const [portfolioData, setPortfolioData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchPortfolioData();
  }, []);

  const fetchPortfolioData = async () => {
    try {
      const response = await portfolioAPI.getPortfolio();
      setPortfolioData(response.data);
    } catch (error) {
      console.error('Failed to fetch portfolio data:', error);
      // Create mock data for demo
      const mockData = {
        summary: {
          totalValue: 125000,
          totalInvestment: 100000,
          totalPnl: 25000,
          pnlPercentage: 25.0,
          positionCount: 1
        },
        positions: [
          {
            id: '1',
            product: { 
              name: 'EUR/USD Digital Option', 
              type: 'digital_option',
              underlyingAsset: 'EUR/USD'
            },
            quantity: 1000,
            entryPrice: 100.0,
            currentValue: 125000,
            totalInvestment: 100000,
            unrealizedPnl: 25000
          }
        ]
      };
      setPortfolioData(mockData);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  const pieColors = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6'];
  
  const pieData = portfolioData?.positions.map((position: any, index: number) => ({
    name: position.product?.name || 'Unknown Product',
    value: position.currentValue,
    color: pieColors[index % pieColors.length]
  })) || [];

  const performanceData = portfolioData?.positions.map((position: any) => ({
    name: position.product?.name?.substring(0, 15) + '...' || 'Unknown',
    invested: position.totalInvestment,
    current: position.currentValue,
    pnl: position.currentValue - position.totalInvestment
  })) || [];

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Portfolio Overview</h1>
          <p className="text-gray-400 mt-1">Monitor your investments and track performance</p>
        </div>
        <div className="flex items-center space-x-2 text-sm">
          <Calendar className="h-4 w-4 text-gray-400" />
          <span className="text-gray-400">Last updated: {new Date().toLocaleString()}</span>
        </div>
      </motion.div>

      {/* Portfolio Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Total Value</p>
              <p className="text-2xl font-bold text-white mt-1">
                ${portfolioData?.summary.totalValue?.toLocaleString() || '0'}
              </p>
              <div className="flex items-center mt-2">
                {(portfolioData?.summary.totalPnl || 0) >= 0 ? (
                  <TrendingUp className="h-4 w-4 text-green-400 mr-1" />
                ) : (
                  <TrendingDown className="h-4 w-4 text-red-400 mr-1" />
                )}
                <span className={`text-sm ${(portfolioData?.summary.totalPnl ||0) >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                  {portfolioData?.summary.pnlPercentage?.toFixed(2) || '0'}%
                </span>
              </div>
            </div>
            <div className="p-3 bg-blue-600/20 rounded-lg">
              <DollarSign className="h-6 w-6 text-blue-400" />
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Total P&L</p>
              <p className={`text-2xl font-bold mt-1 ${(portfolioData?.summary.totalPnl || 0) >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                ${portfolioData?.summary.totalPnl?.toLocaleString() || '0'}
              </p>
              <p className="text-gray-400 text-sm mt-2">vs Entry</p>
            </div>
            <div className={`p-3 rounded-lg ${(portfolioData?.summary.totalPnl || 0) >= 0 ? 'bg-green-600/20' : 'bg-red-600/20'}`}>
              {(portfolioData?.summary.totalPnl || 0) >= 0 ? (
                <TrendingUp className="h-6 w-6 text-green-400" />
              ) : (
                <TrendingDown className="h-6 w-6 text-red-400" />
              )}
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Positions</p>
              <p className="text-2xl font-bold text-white mt-1">
                {portfolioData?.positions?.length || 0}
              </p>
              <p className="text-gray-400 text-sm mt-2">Active</p>
            </div>
            <div className="p-3 bg-yellow-600/20 rounded-lg">
              <Package className="h-6 w-6 text-yellow-400" />
            </div>
          </div>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <div className="flex items-center justify-between">
            <div>
              <p className="text-gray-400 text-sm">Invested</p>
              <p className="text-2xl font-bold text-white mt-1">
                ${portfolioData?.summary.totalInvestment?.toLocaleString() || '0'}
              </p>
              <p className="text-gray-400 text-sm mt-2">Capital</p>
            </div>
            <div className="p-3 bg-purple-600/20 rounded-lg">
              <AlertCircle className="h-6 w-6 text-purple-400" />
            </div>
          </div>
        </motion.div>
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.4 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4">Portfolio Allocation</h3>
          {pieData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <Pie
                  data={pieData}
                  cx="50%"
                  cy="50%"
                  innerRadius={60}
                  outerRadius={120}
                  paddingAngle={5}
                  dataKey="value"
                >
                  {pieData.map((entry: any, index: number) => (
                    <Cell key={`cell-${index}`} fill={entry.color} />
                  ))}
                </Pie>
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#1F2937', 
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#fff'
                  }}
                  formatter={(value: any) => [`$${value.toLocaleString()}`, 'Value']}
                />
              </PieChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-400">
              No positions to display
            </div>
          )}
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.5 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4">Performance Comparison</h3>
          {performanceData.length > 0 ? (
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={performanceData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis 
                  dataKey="name" 
                  stroke="#9CA3AF"
                  tick={{ fontSize: 12 }}
                  angle={-45}
                  textAnchor="end"
                  height={80}
                />
                <YAxis stroke="#9CA3AF" tick={{ fontSize: 12 }} />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#1F2937', 
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#fff'
                  }}
                />
                <Bar dataKey="invested" fill="#6B7280" name="Invested" />
                <Bar dataKey="current" fill="#3B82F6" name="Current Value" />
              </BarChart>
            </ResponsiveContainer>
          ) : (
            <div className="h-64 flex items-center justify-center text-gray-400">
              No performance data to display
            </div>
          )}
        </motion.div>
      </div>

      {/* Positions Table */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4">Detailed Positions</h3>
        
        {portfolioData?.positions?.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-700">
                  <th className="text-left py-3 px-4 text-gray-400 font-medium">Product</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">Quantity</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">Entry Price</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">Current Value</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">P&L</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">P&L %</th>
                </tr>
              </thead>
              <tbody>
                {portfolioData.positions.map((position: any, index: number) => {
                  const pnl = position.unrealizedPnl || (position.currentValue - position.totalInvestment);
                  const pnlPercent = position.totalInvestment > 0 ? (pnl / position.totalInvestment) * 100 : 0;
                  
                  return (
                    <tr key={position.id || index} className="border-b border-gray-700/50 hover:bg-gray-700/30">
                      <td className="py-4 px-4">
                        <div>
                          <p className="text-white font-medium">
                            {position.product?.name || 'Unknown Product'}
                          </p>
                          <p className="text-gray-400 text-sm">
                            {position.product?.type?.replace('_', ' ').toUpperCase() || 'N/A'}
                          </p>
                        </div>
                      </td>
                      <td className="text-right py-4 px-4 text-white">
                        {position.quantity?.toLocaleString() || 'N/A'}
                      </td>
                      <td className="text-right py-4 px-4 text-white">
                        ${position.entryPrice?.toFixed(2) || 'N/A'}
                      </td>
                      <td className="text-right py-4 px-4 text-white">
                        ${position.currentValue?.toLocaleString() || 'N/A'}
                      </td>
                      <td className={`text-right py-4 px-4 font-medium ${pnl >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                        ${pnl.toLocaleString()}
                      </td>
                      <td className={`text-right py-4 px-4 font-medium ${pnl >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                        {pnlPercent.toFixed(2)}%
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <Package className="h-16 w-16 text-gray-600 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-400 mb-2">No Positions</h3>
            <p className="text-gray-500">You don't have any active positions yet. Start by creating strategies or investing in products.</p>
          </div>
        )}
      </motion.div>
    </div>
  );
};

export default Portfolio;
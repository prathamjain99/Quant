import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { Play, Download, Calendar, TrendingUp, Target, AlertTriangle } from 'lucide-react';
import { strategyAPI, backtestAPI } from '../../services/api';
import toast from 'react-hot-toast';

const Backtesting: React.FC = () => {
  const [strategies, setStrategies] = useState<any[]>([]);
  const [selectedStrategy, setSelectedStrategy] = useState('');
  const [backtestParams, setBacktestParams] = useState({
    symbol: 'SPY',
    startDate: '2023-01-01',
    endDate: '2024-01-01',
    initialCapital: 10000
  });
  const [backtestResult, setBacktestResult] = useState<any>(null);
  const [running, setRunning] = useState(false);

  useEffect(() => {
    fetchStrategies();
  }, []);

  const fetchStrategies = async () => {
    try {
      const response = await strategyAPI.getStrategies();
      setStrategies(response.data);
    } catch (error) {
      console.error('Failed to fetch strategies:', error);
    }
  };

  const runBacktest = async () => {
    if (!selectedStrategy) {
      toast.error('Please select a strategy');
      return;
    }

    setRunning(true);
    try {
      const response = await backtestAPI.runBacktest({
        strategyId: selectedStrategy,
        ...backtestParams
      });
      setBacktestResult(response.data);
      toast.success('Backtest completed successfully!');
    } catch (error) {
      toast.error('Failed to run backtest');
      console.error('Backtest error:', error);
    } finally {
      setRunning(false);
    }
  };

  const metrics = backtestResult ? [
    {
      label: 'Total Return',
      value: `${(parseFloat(backtestResult.results.total_return) * 100).toFixed(2)}%`,
      positive: parseFloat(backtestResult.results.total_return) >= 0,
      icon: TrendingUp
    },
    {
      label: 'Sharpe Ratio',
      value: backtestResult.results.sharpe_ratio,
      positive: parseFloat(backtestResult.results.sharpe_ratio) >= 1,
      icon: Target
    },
    {
      label: 'Max Drawdown',
      value: `${(parseFloat(backtestResult.results.max_drawdown) * 100).toFixed(2)}%`,
      positive: parseFloat(backtestResult.results.max_drawdown) <= 0.1,
      icon: AlertTriangle
    },
    {
      label: 'Win Rate',
      value: `${(parseFloat(backtestResult.results.win_rate) * 100).toFixed(1)}%`,
      positive: parseFloat(backtestResult.results.win_rate) >= 0.5,
      icon: Target
    }
  ] : [];

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Strategy Backtesting</h1>
          <p className="text-gray-400 mt-1">Test your quantitative strategies against historical data</p>
        </div>
        <div className="flex items-center space-x-3">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={runBacktest}
            disabled={running}
            className="flex items-center space-x-2 px-4 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg font-medium disabled:opacity-50"
          >
            <Play className="h-4 w-4" />
            <span>{running ? 'Running...' : 'Run Backtest'}</span>
          </motion.button>
          {backtestResult && (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              className="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium"
            >
              <Download className="h-4 w-4" />
              <span>Export Results</span>
            </motion.button>
          )}
        </div>
      </motion.div>

      {/* Backtest Configuration */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
          <Calendar className="h-5 w-5 mr-2" />
          Backtest Configuration
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Strategy</label>
            <select
              value={selectedStrategy}
              onChange={(e) => setSelectedStrategy(e.target.value)}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Select a strategy</option>
              {strategies.map((strategy) => (
                <option key={strategy.id} value={strategy.id}>
                  {strategy.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Symbol</label>
            <input
              type="text"
              value={backtestParams.symbol}
              onChange={(e) => setBacktestParams(prev => ({ ...prev, symbol: e.target.value }))}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">Start Date</label>
            <input
              type="date"
              value={backtestParams.startDate}
              onChange={(e) => setBacktestParams(prev => ({ ...prev, startDate: e.target.value }))}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">End Date</label>
            <input
              type="date"
              value={backtestParams.endDate}
              onChange={(e) => setBacktestParams(prev => ({ ...prev, endDate: e.target.value }))}
              className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>
      </motion.div>

      {/* Results */}
      {backtestResult && (
        <>
          {/* Performance Metrics */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4"
          >
            {metrics.map((metric, index) => (
              <div
                key={metric.label}
                className="bg-gray-800 border border-gray-700 rounded-xl p-6"
              >
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-gray-400 text-sm">{metric.label}</p>
                    <p className="text-2xl font-bold text-white mt-1">{metric.value}</p>
                  </div>
                  <div className={`p-3 rounded-lg ${metric.positive ? 'bg-green-600/20' : 'bg-red-600/20'}`}>
                    <metric.icon className={`h-6 w-6 ${metric.positive ? 'text-green-400' : 'text-red-400'}`} />
                  </div>
                </div>
              </div>
            ))}
          </motion.div>

          {/* Equity Curve */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6"
          >
            <h3 className="text-xl font-semibold text-white mb-4">Equity Curve</h3>
            <ResponsiveContainer width="100%" height={400}>
              <LineChart data={backtestResult.equity_curve || backtestResult.equityCurve}>
                <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
                <XAxis 
                  dataKey="date" 
                  stroke="#9CA3AF"
                  tick={{ fontSize: 12 }}
                  tickFormatter={(value) => new Date(value).toLocaleDateString().slice(0, -5)}
                />
                <YAxis stroke="#9CA3AF" tick={{ fontSize: 12 }} />
                <Tooltip 
                  contentStyle={{ 
                    backgroundColor: '#1F2937', 
                    border: '1px solid #374151',
                    borderRadius: '8px',
                    color: '#fff'
                  }}
                  labelFormatter={(value) => `Date: ${new Date(value).toLocaleDateString()}`}
                  formatter={(value: any) => [`$${value.toLocaleString()}`, 'Portfolio Value']}
                />
                <Line
                  type="monotone"
                  dataKey="value"
                  stroke="#10B981"
                  strokeWidth={2}
                  dot={false}
                />
              </LineChart>
            </ResponsiveContainer>
          </motion.div>

          {/* Trade Statistics */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="grid grid-cols-1 lg:grid-cols-2 gap-6"
          >
            <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
              <h3 className="text-xl font-semibold text-white mb-4">Trade Statistics</h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-400">Total Trades</span>
                  <span className="text-white font-medium">{backtestResult.results.total_trades}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-400">Profitable Trades</span>
                  <span className="text-green-400 font-medium">{backtestResult.results.profitable_trades}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-400">Losing Trades</span>
                  <span className="text-red-400 font-medium">
                    {backtestResult.results.total_trades - backtestResult.results.profitable_trades}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-400">Win Rate</span>
                  <span className="text-white font-medium">
                    {(parseFloat(backtestResult.results.win_rate) * 100).toFixed(1)}%
                  </span>
                </div>
              </div>
            </div>

            <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
              <h3 className="text-xl font-semibold text-white mb-4">Risk Metrics</h3>
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-gray-400">Sharpe Ratio</span>
                  <span className="text-white font-medium">{backtestResult.results.sharpe_ratio}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-400">Max Drawdown</span>
                  <span className="text-red-400 font-medium">
                    {(parseFloat(backtestResult.results.max_drawdown) * 100).toFixed(2)}%
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-400">Total Return</span>
                  <span className={`font-medium ${parseFloat(backtestResult.results.total_return) >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                    {(parseFloat(backtestResult.results.total_return) * 100).toFixed(2)}%
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-400">Final Value</span>
                  <span className="text-white font-medium">
                    ${backtestResult.results.final_value?.toLocaleString()}
                  </span>
                </div>
              </div>
            </div>
          </motion.div>
        </>
      )}

      {!backtestResult && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center"
        >
          <TrendingUp className="h-16 w-16 text-gray-600 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-400 mb-2">No Backtest Results</h3>
          <p className="text-gray-500">Select a strategy and run a backtest to see performance metrics and equity curves.</p>
        </motion.div>
      )}
    </div>
  );
};

export default Backtesting;
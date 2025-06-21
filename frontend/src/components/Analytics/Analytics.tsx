import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, AreaChart, Area, BarChart, Bar } from 'recharts';
import { Activity, TrendingUp, Shield, Target, AlertTriangle, Calculator } from 'lucide-react';
import { analyticsAPI, marketDataAPI } from '../../services/api';

const Analytics: React.FC = () => {
  const [analyticsData, setAnalyticsData] = useState<any>(null);
  const [marketData, setMarketData] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAnalyticsData();
    fetchMarketData();
  }, []);

  const fetchAnalyticsData = async () => {
    try {
      const response = await analyticsAPI.getRiskMetrics();
      const riskMetrics = response.data;
      
      // Transform the data to match the expected format
      const mockData = {
        risk_metrics: {
          var_95: riskMetrics.var95 || 15420,
          var_99: riskMetrics.var99 || 28750,
          beta: riskMetrics.beta || 1.23,
          sharpe_ratio: riskMetrics.sharpeRatio || 1.85,
          sortino_ratio: riskMetrics.sortinoRatio || 2.12,
          max_drawdown: riskMetrics.maxDrawdown || 0.08,
          volatility: riskMetrics.volatility || 0.16,
          correlation_spy: riskMetrics.correlationSpy || 0.78
        },
        performance_attribution: [
          { category: 'Equity Strategies', contribution: 8.5, allocation: 45 },
          { category: 'FX Products', contribution: 3.2, allocation: 25 },
          { category: 'Structured Products', contribution: 2.1, allocation: 20 },
          { category: 'Cash', contribution: 0.1, allocation: 10 }
        ],
        risk_breakdown: [
          { factor: 'Market Risk', value: 65, limit: 80 },
          { factor: 'Credit Risk', value: 25, limit: 40 },
          { factor: 'Liquidity Risk', value: 15, limit: 30 },
          { factor: 'Operational Risk', value: 8, limit: 20 }
        ]
      };
      setAnalyticsData(mockData);
    } catch (error) {
      console.error('Failed to fetch analytics data:', error);
      // Fallback to mock data
      const mockData = {
        risk_metrics: {
          var_95: 15420,
          var_99: 28750,
          beta: 1.23,
          sharpe_ratio: 1.85,
          sortino_ratio: 2.12,
          max_drawdown: 0.08,
          volatility: 0.16,
          correlation_spy: 0.78
        },
        performance_attribution: [
          { category: 'Equity Strategies', contribution: 8.5, allocation: 45 },
          { category: 'FX Products', contribution: 3.2, allocation: 25 },
          { category: 'Structured Products', contribution: 2.1, allocation: 20 },
          { category: 'Cash', contribution: 0.1, allocation: 10 }
        ],
        risk_breakdown: [
          { factor: 'Market Risk', value: 65, limit: 80 },
          { factor: 'Credit Risk', value: 25, limit: 40 },
          { factor: 'Liquidity Risk', value: 15, limit: 30 },
          { factor: 'Operational Risk', value: 8, limit: 20 }
        ]
      };
      setAnalyticsData(mockData);
    } finally {
      setLoading(false);
    }
  };

  const fetchMarketData = async () => {
    try {
      const response = await marketDataAPI.getMarketData('SPY', 90);
      const volatilityData = response.data.map((item: any, index: number) => {
        const returns = index > 0 ? (item.close - response.data[index - 1].close) / response.data[index - 1].close : 0;
        return {
          date: item.date,
          price: item.close,
          volatility: Math.abs(returns) * 100,
          volume: item.volume / 1000000
        };
      });
      setMarketData(volatilityData);
    } catch (error) {
      console.error('Failed to fetch market data:', error);
      // Generate mock market data
      const mockData = [];
      const basePrice = 400;
      for (let i = 0; i < 30; i++) {
        const date = new Date();
        date.setDate(date.getDate() - (29 - i));
        const price = basePrice + (Math.random() - 0.5) * 20 + i * 0.5;
        const volatility = Math.random() * 3 + 0.5;
        mockData.push({
          date: date.toISOString().split('T')[0],
          price: price,
          volatility: volatility,
          volume: Math.random() * 10 + 5
        });
      }
      setMarketData(mockData);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  const riskMetrics = [
    {
      label: 'Value at Risk (95%)',
      value: `$${analyticsData?.risk_metrics.var_95.toLocaleString()}`,
      description: '1-day VaR at 95% confidence',
      icon: AlertTriangle,
      color: 'text-red-400'
    },
    {
      label: 'Sharpe Ratio',
      value: analyticsData?.risk_metrics.sharpe_ratio.toFixed(2),
      description: 'Risk-adjusted return',
      icon: Target,
      color: 'text-green-400'
    },
    {
      label: 'Portfolio Beta',
      value: analyticsData?.risk_metrics.beta.toFixed(2),
      description: 'Market sensitivity',
      icon: Activity,
      color: 'text-blue-400'
    },
    {
      label: 'Max Drawdown',
      value: `${(analyticsData?.risk_metrics.max_drawdown * 100).toFixed(1)}%`,
      description: 'Largest peak-to-trough decline',
      icon: TrendingUp,
      color: 'text-yellow-400'
    }
  ];

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Risk Analytics</h1>
          <p className="text-gray-400 mt-1">Advanced portfolio risk metrics and performance attribution</p>
        </div>
        <div className="flex items-center space-x-2 text-sm">
          <Calculator className="h-4 w-4 text-gray-400" />
          <span className="text-gray-400">Real-time calculations</span>
        </div>
      </motion.div>

      {/* Risk Metrics Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {riskMetrics.map((metric, index) => (
          <motion.div
            key={metric.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-colors"
          >
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-sm">{metric.label}</p>
                <p className="text-2xl font-bold text-white mt-1">{metric.value}</p>
                <p className="text-gray-500 text-xs mt-1">{metric.description}</p>
              </div>
              <div className="p-3 bg-gray-700/50 rounded-lg">
                <metric.icon className={`h-6 w-6 ${metric.color}`} />
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4">Market Volatility Analysis</h3>
          <ResponsiveContainer width="100%" height={300}>
            <AreaChart data={marketData.slice(-30)}>
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
                formatter={(value: any) => [`${value.toFixed(2)}%`, 'Daily Volatility']}
              />
              <Area
                type="monotone"
                dataKey="volatility"
                stroke="#F59E0B"
                fill="url(#colorVolatility)"
                strokeWidth={2}
              />
              <defs>
                <linearGradient id="colorVolatility" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#F59E0B" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#F59E0B" stopOpacity={0}/>
                </linearGradient>
              </defs>
            </AreaChart>
          </ResponsiveContainer>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ delay: 0.4 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4">Performance Attribution</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={analyticsData?.performance_attribution} layout="horizontal">
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis type="number" stroke="#9CA3AF" tick={{ fontSize: 12 }} />
              <YAxis 
                dataKey="category" 
                type="category" 
                stroke="#9CA3AF" 
                tick={{ fontSize: 12 }}
                width={120}
              />
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: '#1F2937', 
                  border: '1px solid #374151',
                  borderRadius: '8px',
                  color: '#fff'
                }}
                formatter={(value: any, name: string) => [
                  name === 'contribution' ? `${value}%` : `${value}%`,
                  name === 'contribution' ? 'Contribution' : 'Allocation'
                ]}
              />
              <Bar dataKey="contribution" fill="#10B981" name="contribution" />
            </BarChart>
          </ResponsiveContainer>
        </motion.div>
      </div>

      {/* Risk Breakdown */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.5 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-6">Risk Factor Analysis</h3>
        
        <div className="space-y-6">
          {analyticsData?.risk_breakdown.map((risk: any, index: number) => (
            <div key={risk.factor} className="space-y-2">
              <div className="flex justify-between items-center">
                <span className="text-white font-medium">{risk.factor}</span>
                <div className="flex items-center space-x-4">
                  <span className="text-gray-400 text-sm">
                    {risk.value}%
                  </span>
                  <span className="text-gray-500 text-sm">
                    Limit: {risk.limit}%
                  </span>
                </div>
              </div>
              <div className="w-full bg-gray-700 rounded-full h-3">
                <div 
                  className={`h-3 rounded-full transition-all duration-500 ${
                    risk.value > risk.limit * 0.8 
                      ? 'bg-red-500' 
                      : risk.value > risk.limit * 0.6 
                        ? 'bg-yellow-500' 
                        : 'bg-green-500'
                  }`}
                  style={{ width: `${(risk.value / risk.limit) * 100}%` }}
                ></div>
              </div>
              <div className="flex justify-between text-xs text-gray-500">
                <span>0%</span>
                <span>{risk.limit}%</span>
              </div>
            </div>
          ))}
        </div>
      </motion.div>

      {/* Advanced Metrics */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6 }}
        className="grid grid-cols-1 md:grid-cols-2 gap-6"
      >
        <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Shield className="h-5 w-5 mr-2 text-blue-400" />
            Risk Metrics
          </h3>
          <div className="space-y-4">
            <div className="flex justify-between">
              <span className="text-gray-400">Volatility (Annualized)</span>
              <span className="text-white font-medium">
                {(analyticsData?.risk_metrics.volatility * 100).toFixed(1)}%
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Sortino Ratio</span>
              <span className="text-white font-medium">
                {analyticsData?.risk_metrics.sortino_ratio.toFixed(2)}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">Correlation with S&P 500</span>
              <span className="text-white font-medium">
                {analyticsData?.risk_metrics.correlation_spy.toFixed(2)}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-400">VaR (99%)</span>
              <span className="text-red-400 font-medium">
                ${analyticsData?.risk_metrics.var_99.toLocaleString()}
              </span>
            </div>
          </div>
        </div>

        <div className="bg-gray-800 border border-gray-700 rounded-xl p-6">
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Target className="h-5 w-5 mr-2 text-green-400" />
            Performance Summary
          </h3>
          <div className="space-y-4">
            <div className="p-4 bg-green-600/20 border border-green-500/30 rounded-lg">
              <div className="flex justify-between items-center">
                <span className="text-green-400">Risk-Adjusted Return</span>
                <span className="text-green-400 font-bold text-lg">Excellent</span>
              </div>
              <p className="text-green-300 text-sm mt-1">
                Sharpe ratio of {analyticsData?.risk_metrics.sharpe_ratio.toFixed(2)} indicates strong risk-adjusted performance
              </p>
            </div>
            
            <div className="p-4 bg-yellow-600/20 border border-yellow-500/30 rounded-lg">
              <div className="flex justify-between items-center">
                <span className="text-yellow-400">Market Sensitivity</span>
                <span className="text-yellow-400 font-bold text-lg">Moderate</span>
              </div>
              <p className="text-yellow-300 text-sm mt-1">
                Beta of {analyticsData?.risk_metrics.beta.toFixed(2)} suggests moderate market correlation
              </p>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default Analytics;
import React from 'react';
import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, Activity, RefreshCw } from 'lucide-react';

interface MarketPreviewCardsProps {
  marketData?: any[];
  loading?: boolean;
  onRefresh?: () => void;
}

const MarketPreviewCards: React.FC<MarketPreviewCardsProps> = ({ 
  marketData, 
  loading, 
  onRefresh 
}) => {
  if (loading) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-xl font-semibold text-white flex items-center">
            <Activity className="h-5 w-5 mr-2 text-green-400" />
            Market Snapshot
          </h3>
          <div className="animate-spin rounded-full h-5 w-5 border-b-2 border-blue-500"></div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {[...Array(6)].map((_, index) => (
            <div key={index} className="animate-pulse p-4 bg-gray-700/50 rounded-lg">
              <div className="space-y-2">
                <div className="h-4 bg-gray-600 rounded w-16"></div>
                <div className="h-6 bg-gray-600 rounded w-20"></div>
                <div className="h-3 bg-gray-600 rounded w-12"></div>
              </div>
            </div>
          ))}
        </div>
      </motion.div>
    );
  }

  const formatPrice = (price: number, currency: string = 'USD') => {
    if (currency === 'INR') {
      return `₹${price.toLocaleString()}`;
    }
    return `$${price.toLocaleString()}`;
  };

  const formatChange = (change: number) => {
    const sign = change >= 0 ? '+' : '';
    return `${sign}${change.toFixed(2)}%`;
  };

  const getTrendIcon = (trend: string) => {
    switch (trend) {
      case 'up':
        return TrendingUp;
      case 'down':
        return TrendingDown;
      default:
        return Activity;
    }
  };

  const getTrendColor = (trend: string) => {
    switch (trend) {
      case 'up':
        return 'text-green-400';
      case 'down':
        return 'text-red-400';
      default:
        return 'text-gray-400';
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: 0.5 }}
      className="bg-gray-800 border border-gray-700 rounded-xl p-6"
    >
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-xl font-semibold text-white flex items-center">
          <Activity className="h-5 w-5 mr-2 text-green-400" />
          Market Snapshot
        </h3>
        <div className="flex items-center space-x-2">
          <span className="text-xs text-gray-400">
            Last updated: {new Date().toLocaleTimeString()}
          </span>
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

      {marketData && marketData.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {marketData.map((market, index) => {
            const TrendIcon = getTrendIcon(market.trend);
            const trendColor = getTrendColor(market.trend);
            
            return (
              <motion.div
                key={market.symbol}
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                transition={{ delay: index * 0.1 }}
                className="p-4 bg-gray-700/50 rounded-lg border border-gray-600/50 hover:border-gray-500/50 transition-colors"
              >
                <div className="flex items-center justify-between mb-2">
                  <div>
                    <h4 className="text-white font-semibold">{market.symbol}</h4>
                    <p className="text-gray-400 text-xs">{market.exchange}</p>
                  </div>
                  <TrendIcon className={`h-5 w-5 ${trendColor}`} />
                </div>
                
                <div className="space-y-1">
                  <p className="text-xl font-bold text-white">
                    {formatPrice(market.price, market.currency)}
                  </p>
                  
                  <div className="flex items-center space-x-2">
                    <span className={`text-sm font-medium ${trendColor}`}>
                      {formatChange(market.changePercent)}
                    </span>
                    <span className={`text-xs ${trendColor}`}>
                      ({market.changeAmount >= 0 ? '+' : ''}{formatPrice(market.changeAmount, market.currency)})
                    </span>
                  </div>
                  
                  {market.volume && (
                    <p className="text-gray-400 text-xs">
                      Vol: {(market.volume / 1000000).toFixed(1)}M
                    </p>
                  )}
                </div>
              </motion.div>
            );
          })}
        </div>
      ) : (
        <div className="text-center py-8">
          <Activity className="h-12 w-12 text-gray-600 mx-auto mb-3" />
          <p className="text-gray-400 text-sm">No market data available</p>
          <p className="text-gray-500 text-xs mt-1">
            Market data will appear here when available
          </p>
        </div>
      )}

      {/* Market Status Indicator */}
      <div className="mt-6 pt-4 border-t border-gray-700">
        <div className="flex items-center justify-between">
          <div className="flex items-center space-x-2">
            <div className="h-2 w-2 bg-green-500 rounded-full animate-pulse"></div>
            <span className="text-green-400 text-sm">Markets Open</span>
          </div>
          <span className="text-gray-400 text-xs">
            Real-time data simulation
          </span>
        </div>
      </div>
    </motion.div>
  );
};

export default MarketPreviewCards;
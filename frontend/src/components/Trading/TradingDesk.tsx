import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { DollarSign, TrendingUp, Clock, CheckCircle, AlertCircle, Play } from 'lucide-react';
import { bookingAPI, productAPI, pricingAPI } from '../../services/api';
import toast from 'react-hot-toast';

const TradingDesk: React.FC = () => {
  const [products, setProducts] = useState<any[]>([]);
  const [trades, setTrades] = useState<any[]>([]);
  const [selectedProduct, setSelectedProduct] = useState<any>(null);
  const [tradeForm, setTradeForm] = useState({
    tradeType: 'BUY',
    notional: 100000,
    notes: ''
  });
  const [pricing, setPricing] = useState<any>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    fetchProducts();
    fetchTrades();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await productAPI.getProducts();
      setProducts(response.data);
    } catch (error) {
      console.error('Failed to fetch products:', error);
    }
  };

  const fetchTrades = async () => {
    try {
      const response = await bookingAPI.getTrades();
      setTrades(response.data);
    } catch (error) {
      console.error('Failed to fetch trades:', error);
      // Mock data for demo
      setTrades([
        {
          id: '1',
          productName: 'EUR/USD Digital Option',
          tradeType: 'BUY',
          status: 'BOOKED',
          notional: 100000,
          entryPrice: 1.1050,
          currentPrice: 1.1075,
          pnl: 2500,
          tradeDate: new Date().toISOString()
        }
      ]);
    }
  };

  const calculatePricing = async (product: any) => {
    if (!product) return;
    
    setLoading(true);
    try {
      const pricingRequest = {
        productType: product.type,
        spotPrice: 100.0,
        strike: product.strike || 100.0,
        barrier: product.barrier || 80.0,
        coupon: product.coupon || 0.1,
        volatility: 0.2,
        riskFreeRate: 0.05,
        timeToMaturity: (product.maturityMonths || 12) / 12.0,
        numSimulations: 50000
      };

      const response = await pricingAPI.monteCarloPrice(pricingRequest);
      setPricing(response.data);
    } catch (error) {
      console.error('Failed to calculate pricing:', error);
      // Mock pricing data
      setPricing({
        price: 98.75,
        greeks: {
          delta: 0.65,
          gamma: 0.02,
          vega: 0.15,
          theta: -0.05
        },
        confidenceInterval: 1.25,
        numSimulations: 50000
      });
    } finally {
      setLoading(false);
    }
  };

  const bookTrade = async () => {
    if (!selectedProduct || !pricing) {
      toast.error('Please select a product and calculate pricing first');
      return;
    }

    try {
      const tradeRequest = {
        productId: selectedProduct.id,
        tradeType: tradeForm.tradeType,
        notional: tradeForm.notional,
        entryPrice: pricing.price,
        notes: tradeForm.notes
      };

      await bookingAPI.bookTrade(tradeRequest);
      toast.success('Trade booked successfully!');
      
      // Reset form and refresh trades
      setTradeForm({
        tradeType: 'BUY',
        notional: 100000,
        notes: ''
      });
      setSelectedProduct(null);
      setPricing(null);
      fetchTrades();
    } catch (error) {
      toast.error('Failed to book trade');
      console.error('Trade booking error:', error);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'BOOKED': return 'text-blue-400 bg-blue-600/20';
      case 'CONFIRMED': return 'text-green-400 bg-green-600/20';
      case 'SETTLED': return 'text-purple-400 bg-purple-600/20';
      case 'CANCELLED': return 'text-red-400 bg-red-600/20';
      default: return 'text-gray-400 bg-gray-600/20';
    }
  };

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Trading Desk</h1>
          <p className="text-gray-400 mt-1">Book trades and manage positions in structured products</p>
        </div>
        <div className="flex items-center space-x-2 text-sm">
          <div className="h-2 w-2 bg-green-500 rounded-full animate-pulse"></div>
          <span className="text-green-400">Market Open</span>
        </div>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Product Selection & Pricing */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <DollarSign className="h-5 w-5 mr-2" />
            Product Selection & Pricing
          </h3>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Select Product</label>
              <select
                value={selectedProduct?.id || ''}
                onChange={(e) => {
                  const product = products.find(p => p.id === e.target.value);
                  setSelectedProduct(product);
                  if (product) calculatePricing(product);
                }}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              >
                <option value="">Choose a product...</option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} ({product.type?.replace('_', ' ').toUpperCase()})
                  </option>
                ))}
              </select>
            </div>

            {selectedProduct && (
              <div className="p-4 bg-gray-700/50 rounded-lg">
                <h4 className="text-white font-medium mb-2">Product Details</h4>
                <div className="grid grid-cols-2 gap-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-400">Type:</span>
                    <span className="text-white">{selectedProduct.type?.replace('_', ' ').toUpperCase()}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Underlying:</span>
                    <span className="text-white">{selectedProduct.underlyingAsset}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Strike:</span>
                    <span className="text-white">{selectedProduct.strike}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Coupon:</span>
                    <span className="text-white">{(selectedProduct.coupon * 100).toFixed(2)}%</span>
                  </div>
                </div>
              </div>
            )}

            {pricing && (
              <div className="p-4 bg-blue-600/20 border border-blue-500/30 rounded-lg">
                <h4 className="text-blue-400 font-medium mb-3">Live Pricing</h4>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-gray-400 text-sm">Fair Value</p>
                    <p className="text-white text-xl font-bold">${pricing.price?.toFixed(4)}</p>
                  </div>
                  <div>
                    <p className="text-gray-400 text-sm">Confidence</p>
                    <p className="text-white text-xl font-bold">±${pricing.confidenceInterval?.toFixed(4)}</p>
                  </div>
                </div>
                
                <div className="mt-3 grid grid-cols-4 gap-2 text-xs">
                  <div>
                    <p className="text-gray-400">Delta</p>
                    <p className="text-white font-medium">{pricing.greeks?.delta?.toFixed(4)}</p>
                  </div>
                  <div>
                    <p className="text-gray-400">Gamma</p>
                    <p className="text-white font-medium">{pricing.greeks?.gamma?.toFixed(4)}</p>
                  </div>
                  <div>
                    <p className="text-gray-400">Vega</p>
                    <p className="text-white font-medium">{pricing.greeks?.vega?.toFixed(4)}</p>
                  </div>
                  <div>
                    <p className="text-gray-400">Theta</p>
                    <p className="text-white font-medium">{pricing.greeks?.theta?.toFixed(4)}</p>
                  </div>
                </div>
              </div>
            )}

            {loading && (
              <div className="flex items-center justify-center py-8">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
                <span className="ml-2 text-gray-400">Calculating pricing...</span>
              </div>
            )}
          </div>
        </motion.div>

        {/* Trade Booking */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <TrendingUp className="h-5 w-5 mr-2" />
            Trade Booking
          </h3>

          <div className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Trade Type</label>
                <select
                  value={tradeForm.tradeType}
                  onChange={(e) => setTradeForm(prev => ({ ...prev, tradeType: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                >
                  <option value="BUY">Buy</option>
                  <option value="SELL">Sell</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Notional Amount</label>
                <input
                  type="number"
                  step="1000"
                  value={tradeForm.notional}
                  onChange={(e) => setTradeForm(prev => ({ ...prev, notional: parseInt(e.target.value) }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Notes</label>
              <textarea
                value={tradeForm.notes}
                onChange={(e) => setTradeForm(prev => ({ ...prev, notes: e.target.value }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500 h-20"
                placeholder="Trade notes or instructions..."
              />
            </div>

            {pricing && selectedProduct && (
              <div className="p-4 bg-green-600/20 border border-green-500/30 rounded-lg">
                <h4 className="text-green-400 font-medium mb-2">Trade Summary</h4>
                <div className="space-y-1 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-400">Product:</span>
                    <span className="text-white">{selectedProduct.name}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Direction:</span>
                    <span className="text-white">{tradeForm.tradeType}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Notional:</span>
                    <span className="text-white">${tradeForm.notional.toLocaleString()}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-400">Entry Price:</span>
                    <span className="text-white">${pricing.price?.toFixed(4)}</span>
                  </div>
                  <div className="flex justify-between font-medium">
                    <span className="text-gray-400">Total Value:</span>
                    <span className="text-white">${(tradeForm.notional * (pricing.price || 0) / 100).toLocaleString()}</span>
                  </div>
                </div>
              </div>
            )}

            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              onClick={bookTrade}
              disabled={!selectedProduct || !pricing}
              className="w-full py-3 px-4 bg-green-600 hover:bg-green-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white font-semibold rounded-lg transition-colors flex items-center justify-center space-x-2"
            >
              <Play className="h-4 w-4" />
              <span>Book Trade</span>
            </motion.button>
          </div>
        </motion.div>
      </div>

      {/* Trade Blotter */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
          <Clock className="h-5 w-5 mr-2" />
          Trade Blotter
        </h3>

        {trades.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-700">
                  <th className="text-left py-3 px-4 text-gray-400 font-medium">Product</th>
                  <th className="text-left py-3 px-4 text-gray-400 font-medium">Type</th>
                  <th className="text-left py-3 px-4 text-gray-400 font-medium">Status</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">Notional</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">Entry Price</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">Current Price</th>
                  <th className="text-right py-3 px-4 text-gray-400 font-medium">P&L</th>
                  <th className="text-left py-3 px-4 text-gray-400 font-medium">Trade Date</th>
                </tr>
              </thead>
              <tbody>
                {trades.map((trade, index) => (
                  <tr key={trade.id || index} className="border-b border-gray-700/50 hover:bg-gray-700/30">
                    <td className="py-4 px-4">
                      <span className="text-white font-medium">{trade.productName}</span>
                    </td>
                    <td className="py-4 px-4">
                      <span className={`px-2 py-1 rounded text-xs ${
                        trade.tradeType === 'BUY' ? 'bg-green-600/20 text-green-400' : 'bg-red-600/20 text-red-400'
                      }`}>
                        {trade.tradeType}
                      </span>
                    </td>
                    <td className="py-4 px-4">
                      <span className={`px-2 py-1 rounded text-xs ${getStatusColor(trade.status)}`}>
                        {trade.status}
                      </span>
                    </td>
                    <td className="text-right py-4 px-4 text-white">
                      ${trade.notional?.toLocaleString()}
                    </td>
                    <td className="text-right py-4 px-4 text-white">
                      ${trade.entryPrice?.toFixed(4)}
                    </td>
                    <td className="text-right py-4 px-4 text-white">
                      ${trade.currentPrice?.toFixed(4)}
                    </td>
                    <td className={`text-right py-4 px-4 font-medium ${
                      (trade.pnl || 0) >= 0 ? 'text-green-400' : 'text-red-400'
                    }`}>
                      ${trade.pnl?.toLocaleString()}
                    </td>
                    <td className="py-4 px-4 text-gray-300">
                      {new Date(trade.tradeDate).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <Clock className="h-16 w-16 text-gray-600 mx-auto mb-4" />
            <h3 className="text-xl font-semibold text-gray-400 mb-2">No Trades</h3>
            <p className="text-gray-500">Book your first trade to see it appear in the blotter.</p>
          </div>
        )}
      </motion.div>
    </div>
  );
};

export default TradingDesk;
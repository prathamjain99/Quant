import React, { useState } from 'react';
import { motion } from 'framer-motion';
import { Save, Calculator, PieChart, TrendingUp, Shield } from 'lucide-react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import { productAPI } from '../../services/api';
import toast from 'react-hot-toast';

const ProductCreator: React.FC = () => {
  const [product, setProduct] = useState({
    name: '',
    type: 'digital_option',
    underlyingAsset: 'EUR/USD',
    strike: 1.10,
    barrier: 1.05,
    coupon: 0.08,
    notional: 100000,
    maturityMonths: 12,
    issuer: 'Dealer 1',
    currency: 'USD'
  });

  const [saving, setSaving] = useState(false);

  const handleSave = async () => {
    if (!product.name.trim()) {
      toast.error('Please enter a product name');
      return;
    }

    setSaving(true);
    try {
      await productAPI.createProduct(product);
      toast.success('Product created successfully!');
      
      // Reset form
      setProduct({
        name: '',
        type: 'digital_option',
        underlyingAsset: 'EUR/USD',
        strike: 1.10,
        barrier: 1.05,
        coupon: 0.08,
        notional: 100000,
        maturityMonths: 12,
        issuer: 'Dealer 1',
        currency: 'USD'
      });
    } catch (error) {
      toast.error('Failed to create product');
      console.error('Product creation error:', error);
    } finally {
      setSaving(false);
    }
  };

  // Mock payoff calculation
  const generatePayoffData = () => {
    const data = [];
    const currentSpot = product.strike;
    
    for (let i = 0.8; i <= 1.3; i += 0.01) {
      const spotPrice = currentSpot * i;
      let payoff = 0;
      
      if (product.type === 'digital_option') {
        payoff = spotPrice > product.strike ? product.notional * product.coupon : 0;
      } else if (product.type === 'barrier_option') {
        if (spotPrice > product.barrier && spotPrice > product.strike) {
          payoff = product.notional * product.coupon;
        } else {
          payoff = 0;
        }
      }
      
      data.push({
        spot: spotPrice,
        payoff: payoff
      });
    }
    
    return data;
  };

  const payoffData = generatePayoffData();

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Product Creator</h1>
          <p className="text-gray-400 mt-1">Design structured financial products and derivatives</p>
        </div>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={handleSave}
          disabled={saving}
          className="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium disabled:opacity-50"
        >
          <Save className="h-4 w-4" />
          <span>{saving ? 'Creating...' : 'Create Product'}</span>
        </motion.button>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Product Configuration */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Calculator className="h-5 w-5 mr-2" />
            Product Details
          </h3>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Product Name</label>
              <input
                type="text"
                value={product.name}
                onChange={(e) => setProduct(prev => ({ ...prev, name: e.target.value }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                placeholder="EUR/USD Digital Option Q1 2024"
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Product Type</label>
                <select
                  value={product.type}
                  onChange={(e) => setProduct(prev => ({ ...prev, type: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                >
                  <option value="digital_option">Digital Option</option>
                  <option value="barrier_option">Barrier Option</option>
                  <option value="dual_currency">Dual Currency Investment</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Underlying Asset</label>
                <select
                  value={product.underlyingAsset}
                  onChange={(e) => setProduct(prev => ({ ...prev, underlyingAsset: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                >
                  <option value="EUR/USD">EUR/USD</option>
                  <option value="GBP/USD">GBP/USD</option>
                  <option value="USD/JPY">USD/JPY</option>
                  <option value="SPY">S&P 500 ETF</option>
                  <option value="AAPL">Apple Inc.</option>
                </select>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Strike Price</label>
                <input
                  type="number"
                  step="0.001"
                  value={product.strike}
                  onChange={(e) => setProduct(prev => ({ ...prev, strike: parseFloat(e.target.value) }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Barrier Level</label>
                <input
                  type="number"
                  step="0.001"
                  value={product.barrier}
                  onChange={(e) => setProduct(prev => ({ ...prev, barrier: parseFloat(e.target.value) }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Coupon Rate (%)</label>
                <input
                  type="number"
                  step="0.01"
                  value={product.coupon * 100}
                  onChange={(e) => setProduct(prev => ({ ...prev, coupon: parseFloat(e.target.value) / 100 }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Maturity (Months)</label>
                <input
                  type="number"
                  value={product.maturityMonths}
                  onChange={(e) => setProduct(prev => ({ ...prev, maturityMonths: parseInt(e.target.value) }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Notional Amount</label>
                <input
                  type="number"
                  step="1000"
                  value={product.notional}
                  onChange={(e) => setProduct(prev => ({ ...prev, notional: parseInt(e.target.value) }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">Issuer</label>
                <select
                  value={product.issuer}
                  onChange={(e) => setProduct(prev => ({ ...prev, issuer: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                >
                  <option value="Dealer 1">Dealer 1</option>
                  <option value="Dealer 2">Dealer 2</option>
                  <option value="Dealer 3">Dealer 3</option>
                </select>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Payoff Diagram */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <PieChart className="h-5 w-5 mr-2" />
            Payoff Diagram
          </h3>

          <ResponsiveContainer width="100%" height={300}>
            <LineChart data={payoffData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" />
              <XAxis 
                dataKey="spot" 
                stroke="#9CA3AF"
                tick={{ fontSize: 12 }}
                tickFormatter={(value) => value.toFixed(3)}
              />
              <YAxis stroke="#9CA3AF" tick={{ fontSize: 12 }} />
              <Tooltip 
                contentStyle={{ 
                  backgroundColor: '#1F2937', 
                  border: '1px solid #374151',
                  borderRadius: '8px',
                  color: '#fff'
                }}
                formatter={(value: any) => [`$${value.toLocaleString()}`, 'Payoff']}
                labelFormatter={(value) => `Spot: ${value}`}
              />
              <Line
                type="monotone"
                dataKey="payoff"
                stroke="#F59E0B"
                strokeWidth={3}
                dot={false}
              />
            </LineChart>
          </ResponsiveContainer>

          <div className="mt-4 grid grid-cols-2 gap-4">
            <div className="p-3 bg-green-600/20 border border-green-500/30 rounded-lg">
              <div className="flex items-center justify-between">
                <span className="text-green-400 text-sm">Max Payout</span>
                <TrendingUp className="h-4 w-4 text-green-400" />
              </div>
              <p className="text-white font-semibold mt-1">
                ${(product.notional * product.coupon).toLocaleString()}
              </p>
            </div>

            <div className="p-3 bg-red-600/20 border border-red-500/30 rounded-lg">
              <div className="flex items-center justify-between">
                <span className="text-red-400 text-sm">Max Loss</span>
                <Shield className="h-4 w-4 text-red-400" />
              </div>
              <p className="text-white font-semibold mt-1">$0</p>
            </div>
          </div>
        </motion.div>
      </div>

      {/* Product Summary */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4">Product Summary</h3>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="space-y-3">
            <h4 className="text-lg font-medium text-blue-400">Product Structure</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-400">Type:</span>
                <span className="text-white">{product.type.replace('_', ' ').toUpperCase()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Underlying:</span>
                <span className="text-white">{product.underlyingAsset}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Strike:</span>
                <span className="text-white">{product.strike}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Barrier:</span>
                <span className="text-white">{product.barrier}</span>
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <h4 className="text-lg font-medium text-yellow-400">Terms & Conditions</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-400">Notional:</span>
                <span className="text-white">${product.notional.toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Coupon:</span>
                <span className="text-white">{(product.coupon * 100).toFixed(2)}%</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Maturity:</span>
                <span className="text-white">{product.maturityMonths} months</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Issuer:</span>
                <span className="text-white">{product.issuer}</span>
              </div>
            </div>
          </div>

          <div className="space-y-3">
            <h4 className="text-lg font-medium text-green-400">Risk Metrics</h4>
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-400">Max Payout:</span>
                <span className="text-green-400">${(product.notional * product.coupon).toLocaleString()}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Breakeven:</span>
                <span className="text-white">{product.strike.toFixed(3)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Max Loss:</span>
                <span className="text-red-400">$0</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-400">Yield to Maturity:</span>
                <span className="text-white">{(product.coupon * 100 / product.maturityMonths * 12).toFixed(2)}%</span>
              </div>
            </div>
          </div>
        </div>
      </motion.div>
    </div>
  );
};

export default ProductCreator;
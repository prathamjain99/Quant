import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { Plus, Save, Settings, TrendingUp, BarChart } from 'lucide-react';
import { strategyAPI } from '../../services/api';
import toast from 'react-hot-toast';

const StrategyBuilder: React.FC = () => {
  const [strategy, setStrategy] = useState({
    name: '',
    description: '',
    assetList: ['SPY'],
    indicators: {
      ema_short: 10,
      ema_long: 20,
      rsi_period: 14,
      macd_fast: 12,
      macd_slow: 26,
      macd_signal: 9
    },
    rules: {
      entry_condition: 'ema_cross_up',
      exit_condition: 'ema_cross_down',
      stop_loss: 5,
      take_profit: 10,
      position_size: 1
    }
  });

  const [strategies, setStrategies] = useState<any[]>([]);
  const [saving, setSaving] = useState(false);

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

  const handleSave = async () => {
    if (!strategy.name.trim()) {
      toast.error('Please enter a strategy name');
      return;
    }

    setSaving(true);
    try {
      await strategyAPI.createStrategy(strategy);
      toast.success('Strategy saved successfully!');
      
      // Reset form
      setStrategy({
        name: '',
        description: '',
        assetList: ['SPY'],
        indicators: {
          ema_short: 10,
          ema_long: 20,
          rsi_period: 14,
          macd_fast: 12,
          macd_slow: 26,
          macd_signal: 9
        },
        rules: {
          entry_condition: 'ema_cross_up',
          exit_condition: 'ema_cross_down',
          stop_loss: 5,
          take_profit: 10,
          position_size: 1
        }
      });
      
      // Refresh strategies list
      fetchStrategies();
    } catch (error) {
      toast.error('Failed to save strategy');
      console.error('Strategy save error:', error);
    } finally {
      setSaving(false);
    }
  };

  const addAsset = () => {
    setStrategy(prev => ({
      ...prev,
      assetList: [...prev.assetList, 'AAPL']
    }));
  };

  const removeAsset = (index: number) => {
    if (strategy.assetList.length > 1) {
      const newAssets = strategy.assetList.filter((_, i) => i !== index);
      setStrategy(prev => ({ ...prev, assetList: newAssets }));
    }
  };

  const updateAsset = (index: number, value: string) => {
    const newAssets = [...strategy.assetList];
    newAssets[index] = value;
    setStrategy(prev => ({ ...prev, assetList: newAssets }));
  };

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Strategy Builder</h1>
          <p className="text-gray-400 mt-1">Design and configure quantitative trading strategies</p>
        </div>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={handleSave}
          disabled={saving}
          className="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium disabled:opacity-50"
        >
          <Save className="h-4 w-4" />
          <span>{saving ? 'Saving...' : 'Save Strategy'}</span>
        </motion.button>
      </motion.div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Basic Configuration */}
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <Settings className="h-5 w-5 mr-2" />
            Basic Configuration
          </h3>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Strategy Name</label>
              <input
                type="text"
                value={strategy.name}
                onChange={(e) => setStrategy(prev => ({ ...prev, name: e.target.value }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                placeholder="Enter strategy name"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Description</label>
              <textarea
                value={strategy.description}
                onChange={(e) => setStrategy(prev => ({ ...prev, description: e.target.value }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500 h-24"
                placeholder="Describe your strategy"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Assets</label>
              <div className="space-y-2">
                {strategy.assetList.map((asset, index) => (
                  <div key={index} className="flex items-center space-x-2">
                    <input
                      type="text"
                      value={asset}
                      onChange={(e) => updateAsset(index, e.target.value)}
                      className="flex-1 px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                    />
                    {strategy.assetList.length > 1 && (
                      <button
                        onClick={() => removeAsset(index)}
                        className="px-3 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg"
                      >
                        ×
                      </button>
                    )}
                  </div>
                ))}
                <button
                  onClick={addAsset}
                  className="w-full px-3 py-2 border-2 border-dashed border-gray-600 rounded-lg text-gray-400 hover:border-blue-500 hover:text-blue-400 transition-colors"
                >
                  <Plus className="h-4 w-4 mx-auto" />
                </button>
              </div>
            </div>
          </div>
        </motion.div>

        {/* Technical Indicators */}
        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
            <BarChart className="h-5 w-5 mr-2" />
            Technical Indicators
          </h3>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">EMA Short</label>
              <input
                type="number"
                value={strategy.indicators.ema_short}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  indicators: { ...prev.indicators, ema_short: parseInt(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">EMA Long</label>
              <input
                type="number"
                value={strategy.indicators.ema_long}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  indicators: { ...prev.indicators, ema_long: parseInt(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">RSI Period</label>
              <input
                type="number"
                value={strategy.indicators.rsi_period}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  indicators: { ...prev.indicators, rsi_period: parseInt(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">MACD Fast</label>
              <input
                type="number"
                value={strategy.indicators.macd_fast}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  indicators: { ...prev.indicators, macd_fast: parseInt(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
        </motion.div>
      </div>

      {/* Trading Rules */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-6"
      >
        <h3 className="text-xl font-semibold text-white mb-4 flex items-center">
          <TrendingUp className="h-5 w-5 mr-2" />
          Trading Rules
        </h3>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Entry Condition</label>
              <select
                value={strategy.rules.entry_condition}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  rules: { ...prev.rules, entry_condition: e.target.value }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              >
                <option value="ema_cross_up">EMA Cross Up</option>
                <option value="rsi_oversold">RSI Oversold</option>
                <option value="macd_bullish">MACD Bullish</option>
              </select>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Exit Condition</label>
              <select
                value={strategy.rules.exit_condition}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  rules: { ...prev.rules, exit_condition: e.target.value }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              >
                <option value="ema_cross_down">EMA Cross Down</option>
                <option value="rsi_overbought">RSI Overbought</option>
                <option value="macd_bearish">MACD Bearish</option>
              </select>
            </div>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Stop Loss (%)</label>
              <input
                type="number"
                step="0.1"
                value={strategy.rules.stop_loss}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  rules: { ...prev.rules, stop_loss: parseFloat(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Take Profit (%)</label>
              <input
                type="number"
                step="0.1"
                value={strategy.rules.take_profit}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  rules: { ...prev.rules, take_profit: parseFloat(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-300 mb-2">Position Size</label>
              <input
                type="number"
                step="0.1"
                value={strategy.rules.position_size}
                onChange={(e) => setStrategy(prev => ({
                  ...prev,
                  rules: { ...prev.rules, position_size: parseFloat(e.target.value) }
                }))}
                className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
              />
            </div>

            <div className="p-4 bg-blue-600/20 border border-blue-500/30 rounded-lg">
              <p className="text-blue-400 text-sm">
                <strong>Note:</strong> This is a simplified strategy builder. 
                Real implementations would include more sophisticated indicators and risk management.
              </p>
            </div>
          </div>
        </div>
      </motion.div>

      {/* Existing Strategies */}
      {strategies.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-6"
        >
          <h3 className="text-xl font-semibold text-white mb-4">Your Strategies</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {strategies.map((strategy, index) => (
              <div key={strategy.id || index} className="p-4 bg-gray-700 rounded-lg">
                <h4 className="text-white font-medium">{strategy.name}</h4>
                <p className="text-gray-400 text-sm mt-1">{strategy.description}</p>
                <div className="mt-2 flex flex-wrap gap-1">
                  {strategy.assetList?.map((asset: string, i: number) => (
                    <span key={i} className="px-2 py-1 bg-blue-600/20 text-blue-400 text-xs rounded">
                      {asset}
                    </span>
                  ))}
                </div>
              </div>
            ))}
          </div>
        </motion.div>
      )}
    </div>
  );
};

export default StrategyBuilder;
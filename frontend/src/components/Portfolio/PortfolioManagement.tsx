import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  Plus, 
  Edit, 
  Trash2, 
  Search, 
  TrendingUp, 
  TrendingDown, 
  DollarSign, 
  Package,
  Calendar,
  BarChart3,
  Eye,
  AlertCircle
} from 'lucide-react';
import { portfolioManagementAPI } from '../../services/api';
import toast from 'react-hot-toast';

interface Portfolio {
  id: number;
  name: string;
  description: string;
  userName: string;
  totalValue: number;
  totalInvestment: number;
  totalPnl: number;
  pnlPercentage: number;
  sharpeRatio: number;
  riskScore: number;
  positionCount: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
  lastCalculated: string;
}

interface PortfolioFormData {
  name: string;
  description: string;
}

const PortfolioManagement: React.FC = () => {
  const [portfolios, setPortfolios] = useState<Portfolio[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [selectedPortfolio, setSelectedPortfolio] = useState<Portfolio | null>(null);
  const [formData, setFormData] = useState<PortfolioFormData>({ name: '', description: '' });
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchPortfolios();
  }, []);

  const fetchPortfolios = async () => {
    try {
      setLoading(true);
      const response = await portfolioManagementAPI.getUserPortfolios();
      setPortfolios(response.data);
    } catch (error) {
      console.error('Failed to fetch portfolios:', error);
      toast.error('Failed to load portfolios');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchPortfolios();
      return;
    }

    try {
      const response = await portfolioManagementAPI.searchPortfolios(searchTerm);
      setPortfolios(response.data);
    } catch (error) {
      console.error('Search failed:', error);
      toast.error('Search failed');
    }
  };

  const handleCreatePortfolio = async () => {
    if (!formData.name.trim()) {
      toast.error('Portfolio name is required');
      return;
    }

    setSubmitting(true);
    try {
      await portfolioManagementAPI.createPortfolio(formData);
      toast.success('Portfolio created successfully!');
      setShowCreateModal(false);
      setFormData({ name: '', description: '' });
      fetchPortfolios();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to create portfolio';
      toast.error(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdatePortfolio = async () => {
    if (!selectedPortfolio || !formData.name.trim()) {
      toast.error('Portfolio name is required');
      return;
    }

    setSubmitting(true);
    try {
      await portfolioManagementAPI.updatePortfolio(selectedPortfolio.id, formData);
      toast.success('Portfolio updated successfully!');
      setShowEditModal(false);
      setSelectedPortfolio(null);
      setFormData({ name: '', description: '' });
      fetchPortfolios();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update portfolio';
      toast.error(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeletePortfolio = async (portfolio: Portfolio) => {
    if (!window.confirm(`Are you sure you want to delete "${portfolio.name}"? This action cannot be undone and will remove all associated trades.`)) {
      return;
    }

    try {
      await portfolioManagementAPI.deletePortfolio(portfolio.id);
      toast.success('Portfolio deleted successfully');
      fetchPortfolios();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to delete portfolio';
      toast.error(errorMessage);
    }
  };

  const openEditModal = (portfolio: Portfolio) => {
    setSelectedPortfolio(portfolio);
    setFormData({ name: portfolio.name, description: portfolio.description || '' });
    setShowEditModal(true);
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(amount);
  };

  const formatPercentage = (value: number) => {
    return `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`;
  };

  const getRiskScoreColor = (score: number) => {
    if (score <= 30) return 'text-green-400';
    if (score <= 60) return 'text-yellow-400';
    return 'text-red-400';
  };

  const filteredPortfolios = portfolios.filter(portfolio =>
    portfolio.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    portfolio.description?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-white">Portfolio Management</h1>
          <p className="text-gray-400 mt-1">Create and manage your investment portfolios</p>
        </div>
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => setShowCreateModal(true)}
          className="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium"
        >
          <Plus className="h-4 w-4" />
          <span>Create Portfolio</span>
        </motion.button>
      </motion.div>

      {/* Search Bar */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="bg-gray-800 border border-gray-700 rounded-xl p-4"
      >
        <div className="flex items-center space-x-4">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 h-5 w-5" />
            <input
              type="text"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              className="w-full pl-10 pr-4 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white placeholder-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Search portfolios by name or description..."
            />
          </div>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={handleSearch}
            className="px-4 py-2 bg-gray-700 hover:bg-gray-600 text-white rounded-lg"
          >
            Search
          </motion.button>
          {searchTerm && (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => {
                setSearchTerm('');
                fetchPortfolios();
              }}
              className="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg"
            >
              Clear
            </motion.button>
          )}
        </div>
      </motion.div>

      {/* Portfolio Grid */}
      {filteredPortfolios.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredPortfolios.map((portfolio, index) => (
            <motion.div
              key={portfolio.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-colors"
            >
              {/* Portfolio Header */}
              <div className="flex items-start justify-between mb-4">
                <div className="flex-1">
                  <h3 className="text-xl font-semibold text-white mb-1">{portfolio.name}</h3>
                  {portfolio.description && (
                    <p className="text-gray-400 text-sm line-clamp-2">{portfolio.description}</p>
                  )}
                </div>
                <div className="flex items-center space-x-2 ml-4">
                  <motion.button
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.9 }}
                    onClick={() => window.open(`/dashboard/portfolios/${portfolio.id}`, '_blank')}
                    className="p-2 text-gray-400 hover:text-blue-400 transition-colors"
                    title="View Details"
                  >
                    <Eye className="h-4 w-4" />
                  </motion.button>
                  <motion.button
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.9 }}
                    onClick={() => openEditModal(portfolio)}
                    className="p-2 text-gray-400 hover:text-yellow-400 transition-colors"
                    title="Edit Portfolio"
                  >
                    <Edit className="h-4 w-4" />
                  </motion.button>
                  <motion.button
                    whileHover={{ scale: 1.1 }}
                    whileTap={{ scale: 0.9 }}
                    onClick={() => handleDeletePortfolio(portfolio)}
                    className="p-2 text-gray-400 hover:text-red-400 transition-colors"
                    title="Delete Portfolio"
                  >
                    <Trash2 className="h-4 w-4" />
                  </motion.button>
                </div>
              </div>

              {/* Portfolio Metrics */}
              <div className="space-y-4">
                {/* Total Value */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <DollarSign className="h-4 w-4 text-green-400" />
                    <span className="text-gray-400 text-sm">Total Value</span>
                  </div>
                  <span className="text-white font-semibold">
                    {formatCurrency(portfolio.totalValue)}
                  </span>
                </div>

                {/* P&L */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    {portfolio.totalPnl >= 0 ? (
                      <TrendingUp className="h-4 w-4 text-green-400" />
                    ) : (
                      <TrendingDown className="h-4 w-4 text-red-400" />
                    )}
                    <span className="text-gray-400 text-sm">P&L</span>
                  </div>
                  <div className="text-right">
                    <div className={`font-semibold ${portfolio.totalPnl >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                      {formatCurrency(portfolio.totalPnl)}
                    </div>
                    <div className={`text-xs ${portfolio.totalPnl >= 0 ? 'text-green-400' : 'text-red-400'}`}>
                      {formatPercentage(portfolio.pnlPercentage)}
                    </div>
                  </div>
                </div>

                {/* Positions */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <Package className="h-4 w-4 text-blue-400" />
                    <span className="text-gray-400 text-sm">Positions</span>
                  </div>
                  <span className="text-white font-semibold">{portfolio.positionCount}</span>
                </div>

                {/* Risk Score */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <BarChart3 className="h-4 w-4 text-yellow-400" />
                    <span className="text-gray-400 text-sm">Risk Score</span>
                  </div>
                  <span className={`font-semibold ${getRiskScoreColor(portfolio.riskScore)}`}>
                    {portfolio.riskScore.toFixed(1)}
                  </span>
                </div>

                {/* Sharpe Ratio */}
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-2">
                    <TrendingUp className="h-4 w-4 text-purple-400" />
                    <span className="text-gray-400 text-sm">Sharpe Ratio</span>
                  </div>
                  <span className="text-white font-semibold">
                    {portfolio.sharpeRatio.toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Footer */}
              <div className="mt-4 pt-4 border-t border-gray-700">
                <div className="flex items-center justify-between text-xs text-gray-500">
                  <div className="flex items-center space-x-1">
                    <Calendar className="h-3 w-3" />
                    <span>Created {new Date(portfolio.createdAt).toLocaleDateString()}</span>
                  </div>
                  {portfolio.lastCalculated && (
                    <span>Updated {new Date(portfolio.lastCalculated).toLocaleDateString()}</span>
                  )}
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      ) : (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center"
        >
          <Package className="h-16 w-16 text-gray-600 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-400 mb-2">
            {searchTerm ? 'No portfolios found' : 'No portfolios yet'}
          </h3>
          <p className="text-gray-500 mb-6">
            {searchTerm 
              ? 'Try adjusting your search terms or create a new portfolio.'
              : 'Create your first portfolio to start managing your investments.'
            }
          </p>
          {!searchTerm && (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setShowCreateModal(true)}
              className="flex items-center space-x-2 px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium mx-auto"
            >
              <Plus className="h-4 w-4" />
              <span>Create Your First Portfolio</span>
            </motion.button>
          )}
        </motion.div>
      )}

      {/* Create Portfolio Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 w-full max-w-md mx-4"
          >
            <h3 className="text-xl font-semibold text-white mb-4">Create New Portfolio</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Portfolio Name *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter portfolio name"
                  maxLength={100}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500 h-24"
                  placeholder="Enter portfolio description (optional)"
                  maxLength={500}
                />
              </div>
            </div>

            <div className="flex items-center justify-end space-x-3 mt-6">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setShowCreateModal(false);
                  setFormData({ name: '', description: '' });
                }}
                className="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg"
              >
                Cancel
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleCreatePortfolio}
                disabled={submitting || !formData.name.trim()}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white rounded-lg"
              >
                {submitting ? 'Creating...' : 'Create Portfolio'}
              </motion.button>
            </div>
          </motion.div>
        </div>
      )}

      {/* Edit Portfolio Modal */}
      {showEditModal && selectedPortfolio && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 w-full max-w-md mx-4"
          >
            <h3 className="text-xl font-semibold text-white mb-4">Edit Portfolio</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Portfolio Name *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter portfolio name"
                  maxLength={100}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Description
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData(prev => ({ ...prev, description: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500 h-24"
                  placeholder="Enter portfolio description (optional)"
                  maxLength={500}
                />
              </div>
            </div>

            <div className="flex items-center justify-end space-x-3 mt-6">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setShowEditModal(false);
                  setSelectedPortfolio(null);
                  setFormData({ name: '', description: '' });
                }}
                className="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg"
              >
                Cancel
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleUpdatePortfolio}
                disabled={submitting || !formData.name.trim()}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white rounded-lg"
              >
                {submitting ? 'Updating...' : 'Update Portfolio'}
              </motion.button>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default PortfolioManagement;
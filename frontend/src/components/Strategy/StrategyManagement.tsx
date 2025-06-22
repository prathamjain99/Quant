import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { 
  Plus, 
  Edit, 
  Trash2, 
  Search, 
  Eye, 
  Globe, 
  Lock, 
  Code, 
  Tag,
  Calendar,
  User,
  BarChart3,
  Settings,
  Share2,
  EyeOff
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { strategyV2API, StrategyV2, StrategyV2CreateRequest, StrategyV2UpdateRequest } from '../../services/strategyV2API';
import toast from 'react-hot-toast';

const StrategyManagement: React.FC = () => {
  const { user } = useAuth();
  const [strategies, setStrategies] = useState<StrategyV2[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showViewModal, setShowViewModal] = useState(false);
  const [selectedStrategy, setSelectedStrategy] = useState<StrategyV2 | null>(null);
  const [formData, setFormData] = useState<StrategyV2CreateRequest>({
    name: '',
    description: '',
    codeJson: {},
    tags: []
  });
  const [submitting, setSubmitting] = useState(false);
  const [statistics, setStatistics] = useState<any>(null);

  useEffect(() => {
    fetchStrategies();
    if (user?.role === 'researcher') {
      fetchStatistics();
    }
  }, []);

  const fetchStrategies = async () => {
    try {
      setLoading(true);
      const response = await strategyV2API.getStrategies();
      setStrategies(response.data);
    } catch (error) {
      console.error('Failed to fetch strategies:', error);
      toast.error('Failed to load strategies');
    } finally {
      setLoading(false);
    }
  };

  const fetchStatistics = async () => {
    try {
      const response = await strategyV2API.getStatistics();
      setStatistics(response.data);
    } catch (error) {
      console.error('Failed to fetch statistics:', error);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) {
      fetchStrategies();
      return;
    }

    try {
      const response = await strategyV2API.searchStrategies(searchTerm);
      setStrategies(response.data);
    } catch (error) {
      console.error('Search failed:', error);
      toast.error('Search failed');
    }
  };

  const handleCreateStrategy = async () => {
    if (!formData.name.trim()) {
      toast.error('Strategy name is required');
      return;
    }

    setSubmitting(true);
    try {
      await strategyV2API.createStrategy(formData);
      toast.success('Strategy created successfully!');
      setShowCreateModal(false);
      resetForm();
      fetchStrategies();
      if (user?.role === 'researcher') {
        fetchStatistics();
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to create strategy';
      toast.error(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const handleUpdateStrategy = async () => {
    if (!selectedStrategy || !formData.name.trim()) {
      toast.error('Strategy name is required');
      return;
    }

    setSubmitting(true);
    try {
      await strategyV2API.updateStrategy(selectedStrategy.id, formData as StrategyV2UpdateRequest);
      toast.success('Strategy updated successfully!');
      setShowEditModal(false);
      setSelectedStrategy(null);
      resetForm();
      fetchStrategies();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update strategy';
      toast.error(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDeleteStrategy = async (strategy: StrategyV2) => {
    if (!window.confirm(`Are you sure you want to delete "${strategy.name}"? This action cannot be undone.`)) {
      return;
    }

    try {
      await strategyV2API.deleteStrategy(strategy.id);
      toast.success('Strategy deleted successfully');
      fetchStrategies();
      if (user?.role === 'researcher') {
        fetchStatistics();
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to delete strategy';
      toast.error(errorMessage);
    }
  };

  const handlePublishStrategy = async (strategy: StrategyV2) => {
    try {
      await strategyV2API.publishStrategy(strategy.id);
      toast.success('Strategy published successfully');
      fetchStrategies();
      if (user?.role === 'researcher') {
        fetchStatistics();
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to publish strategy';
      toast.error(errorMessage);
    }
  };

  const handleUnpublishStrategy = async (strategy: StrategyV2) => {
    try {
      await strategyV2API.unpublishStrategy(strategy.id);
      toast.success('Strategy unpublished successfully');
      fetchStrategies();
      if (user?.role === 'researcher') {
        fetchStatistics();
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to unpublish strategy';
      toast.error(errorMessage);
    }
  };

  const openEditModal = (strategy: StrategyV2) => {
    setSelectedStrategy(strategy);
    setFormData({
      name: strategy.name,
      description: strategy.description || '',
      codeJson: strategy.codeJson,
      tags: strategy.tags || []
    });
    setShowEditModal(true);
  };

  const openViewModal = (strategy: StrategyV2) => {
    setSelectedStrategy(strategy);
    setShowViewModal(true);
  };

  const resetForm = () => {
    setFormData({
      name: '',
      description: '',
      codeJson: {},
      tags: []
    });
  };

  const addTag = (tag: string) => {
    if (tag.trim() && !formData.tags?.includes(tag.trim())) {
      setFormData(prev => ({
        ...prev,
        tags: [...(prev.tags || []), tag.trim()]
      }));
    }
  };

  const removeTag = (tagToRemove: string) => {
    setFormData(prev => ({
      ...prev,
      tags: prev.tags?.filter(tag => tag !== tagToRemove) || []
    }));
  };

  const getVisibilityIcon = (isPublic: boolean) => {
    return isPublic ? Globe : Lock;
  };

  const getVisibilityColor = (isPublic: boolean) => {
    return isPublic ? 'text-green-400' : 'text-yellow-400';
  };

  const getRoleBasedTitle = () => {
    switch (user?.role) {
      case 'researcher': return 'My Strategies';
      case 'portfolio_manager': return 'All Strategies';
      case 'client': return 'Public Strategies';
      default: return 'Strategies';
    }
  };

  const getRoleBasedDescription = () => {
    switch (user?.role) {
      case 'researcher': return 'Create, manage, and publish your quantitative trading strategies';
      case 'portfolio_manager': return 'View and analyze all strategies from researchers';
      case 'client': return 'Browse public strategies available for implementation';
      default: return 'Strategy management system';
    }
  };

  const filteredStrategies = strategies.filter(strategy =>
    strategy.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    strategy.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    strategy.tags?.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()))
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
          <h1 className="text-3xl font-bold text-white">{getRoleBasedTitle()}</h1>
          <p className="text-gray-400 mt-1">{getRoleBasedDescription()}</p>
        </div>
        {user?.role === 'researcher' && (
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => setShowCreateModal(true)}
            className="flex items-center space-x-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium"
          >
            <Plus className="h-4 w-4" />
            <span>Create Strategy</span>
          </motion.button>
        )}
      </motion.div>

      {/* Statistics (Researcher only) */}
      {user?.role === 'researcher' && statistics && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.1 }}
          className="grid grid-cols-1 md:grid-cols-3 gap-4"
        >
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-sm">Total Strategies</p>
                <p className="text-2xl font-bold text-white">{statistics.totalStrategies}</p>
              </div>
              <BarChart3 className="h-8 w-8 text-blue-400" />
            </div>
          </div>
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-sm">Public Strategies</p>
                <p className="text-2xl font-bold text-green-400">{statistics.publicStrategies}</p>
              </div>
              <Globe className="h-8 w-8 text-green-400" />
            </div>
          </div>
          <div className="bg-gray-800 border border-gray-700 rounded-xl p-4">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-gray-400 text-sm">Private Strategies</p>
                <p className="text-2xl font-bold text-yellow-400">{statistics.privateStrategies}</p>
              </div>
              <Lock className="h-8 w-8 text-yellow-400" />
            </div>
          </div>
        </motion.div>
      )}

      {/* Search Bar */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
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
              placeholder="Search strategies by name, description, or tags..."
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
                fetchStrategies();
              }}
              className="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg"
            >
              Clear
            </motion.button>
          )}
        </div>
      </motion.div>

      {/* Strategy Grid */}
      {filteredStrategies.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredStrategies.map((strategy, index) => {
            const VisibilityIcon = getVisibilityIcon(strategy.isPublic);
            const visibilityColor = getVisibilityColor(strategy.isPublic);
            
            return (
              <motion.div
                key={strategy.id}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                className="bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-colors"
              >
                {/* Strategy Header */}
                <div className="flex items-start justify-between mb-4">
                  <div className="flex-1">
                    <div className="flex items-center space-x-2 mb-2">
                      <h3 className="text-xl font-semibold text-white">{strategy.name}</h3>
                      <VisibilityIcon className={`h-4 w-4 ${visibilityColor}`} />
                    </div>
                    {strategy.description && (
                      <p className="text-gray-400 text-sm line-clamp-2">{strategy.description}</p>
                    )}
                  </div>
                  <div className="flex items-center space-x-2 ml-4">
                    <motion.button
                      whileHover={{ scale: 1.1 }}
                      whileTap={{ scale: 0.9 }}
                      onClick={() => openViewModal(strategy)}
                      className="p-2 text-gray-400 hover:text-blue-400 transition-colors"
                      title="View Details"
                    >
                      <Eye className="h-4 w-4" />
                    </motion.button>
                    {strategy.canEdit && (
                      <motion.button
                        whileHover={{ scale: 1.1 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={() => openEditModal(strategy)}
                        className="p-2 text-gray-400 hover:text-yellow-400 transition-colors"
                        title="Edit Strategy"
                      >
                        <Edit className="h-4 w-4" />
                      </motion.button>
                    )}
                    {strategy.canDelete && (
                      <motion.button
                        whileHover={{ scale: 1.1 }}
                        whileTap={{ scale: 0.9 }}
                        onClick={() => handleDeleteStrategy(strategy)}
                        className="p-2 text-gray-400 hover:text-red-400 transition-colors"
                        title="Delete Strategy"
                      >
                        <Trash2 className="h-4 w-4" />
                      </motion.button>
                    )}
                  </div>
                </div>

                {/* Tags */}
                {strategy.tags && strategy.tags.length > 0 && (
                  <div className="flex flex-wrap gap-2 mb-4">
                    {strategy.tags.slice(0, 3).map((tag, tagIndex) => (
                      <span
                        key={tagIndex}
                        className="px-2 py-1 bg-blue-600/20 text-blue-400 text-xs rounded-full"
                      >
                        {tag}
                      </span>
                    ))}
                    {strategy.tags.length > 3 && (
                      <span className="px-2 py-1 bg-gray-600/20 text-gray-400 text-xs rounded-full">
                        +{strategy.tags.length - 3} more
                      </span>
                    )}
                  </div>
                )}

                {/* Owner Info */}
                <div className="flex items-center justify-between text-sm text-gray-500 mb-4">
                  <div className="flex items-center space-x-2">
                    <User className="h-4 w-4" />
                    <span>{strategy.ownerName}</span>
                  </div>
                  <div className="flex items-center space-x-2">
                    <Calendar className="h-4 w-4" />
                    <span>{new Date(strategy.createdAt).toLocaleDateString()}</span>
                  </div>
                </div>

                {/* Actions */}
                {strategy.canPublish && (
                  <div className="flex items-center space-x-2">
                    {!strategy.isPublic ? (
                      <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => handlePublishStrategy(strategy)}
                        className="flex items-center space-x-2 px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg text-sm flex-1"
                      >
                        <Share2 className="h-4 w-4" />
                        <span>Publish</span>
                      </motion.button>
                    ) : (
                      <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => handleUnpublishStrategy(strategy)}
                        className="flex items-center space-x-2 px-3 py-2 bg-yellow-600 hover:bg-yellow-700 text-white rounded-lg text-sm flex-1"
                      >
                        <EyeOff className="h-4 w-4" />
                        <span>Unpublish</span>
                      </motion.button>
                    )}
                  </div>
                )}

                {/* Published Date */}
                {strategy.isPublic && strategy.publishedAt && (
                  <div className="mt-2 text-xs text-green-400">
                    Published {new Date(strategy.publishedAt).toLocaleDateString()}
                  </div>
                )}
              </motion.div>
            );
          })}
        </div>
      ) : (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gray-800 border border-gray-700 rounded-xl p-12 text-center"
        >
          <Code className="h-16 w-16 text-gray-600 mx-auto mb-4" />
          <h3 className="text-xl font-semibold text-gray-400 mb-2">
            {searchTerm ? 'No strategies found' : 'No strategies yet'}
          </h3>
          <p className="text-gray-500 mb-6">
            {searchTerm 
              ? 'Try adjusting your search terms or create a new strategy.'
              : user?.role === 'researcher' 
                ? 'Create your first strategy to start building quantitative trading algorithms.'
                : 'No strategies are available to view at this time.'
            }
          </p>
          {!searchTerm && user?.role === 'researcher' && (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setShowCreateModal(true)}
              className="flex items-center space-x-2 px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-lg font-medium mx-auto"
            >
              <Plus className="h-4 w-4" />
              <span>Create Your First Strategy</span>
            </motion.button>
          )}
        </motion.div>
      )}

      {/* Create Strategy Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto"
          >
            <h3 className="text-xl font-semibold text-white mb-4">Create New Strategy</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Strategy Name *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter strategy name"
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
                  placeholder="Describe your strategy"
                  maxLength={2000}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Tags
                </label>
                <div className="flex flex-wrap gap-2 mb-2">
                  {formData.tags?.map((tag, index) => (
                    <span
                      key={index}
                      className="flex items-center space-x-1 px-2 py-1 bg-blue-600/20 text-blue-400 text-sm rounded-full"
                    >
                      <span>{tag}</span>
                      <button
                        onClick={() => removeTag(tag)}
                        className="text-blue-400 hover:text-blue-300"
                      >
                        ×
                      </button>
                    </span>
                  ))}
                </div>
                <input
                  type="text"
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      addTag(e.currentTarget.value);
                      e.currentTarget.value = '';
                    }
                  }}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                  placeholder="Type a tag and press Enter"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Strategy Configuration (JSON)
                </label>
                <textarea
                  value={JSON.stringify(formData.codeJson, null, 2)}
                  onChange={(e) => {
                    try {
                      const parsed = JSON.parse(e.target.value);
                      setFormData(prev => ({ ...prev, codeJson: parsed }));
                    } catch (error) {
                      // Invalid JSON, keep the text but don't update codeJson
                    }
                  }}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500 h-32 font-mono text-sm"
                  placeholder='{"indicators": {"sma_period": 20}, "entry_conditions": {}}'
                />
                <p className="text-gray-500 text-xs mt-1">
                  Enter valid JSON configuration for your strategy
                </p>
              </div>
            </div>

            <div className="flex items-center justify-end space-x-3 mt-6">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setShowCreateModal(false);
                  resetForm();
                }}
                className="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg"
              >
                Cancel
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleCreateStrategy}
                disabled={submitting || !formData.name.trim()}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white rounded-lg"
              >
                {submitting ? 'Creating...' : 'Create Strategy'}
              </motion.button>
            </div>
          </motion.div>
        </div>
      )}

      {/* Edit Strategy Modal */}
      {showEditModal && selectedStrategy && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto"
          >
            <h3 className="text-xl font-semibold text-white mb-4">Edit Strategy</h3>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Strategy Name *
                </label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData(prev => ({ ...prev, name: e.target.value }))}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                  placeholder="Enter strategy name"
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
                  placeholder="Describe your strategy"
                  maxLength={2000}
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Tags
                </label>
                <div className="flex flex-wrap gap-2 mb-2">
                  {formData.tags?.map((tag, index) => (
                    <span
                      key={index}
                      className="flex items-center space-x-1 px-2 py-1 bg-blue-600/20 text-blue-400 text-sm rounded-full"
                    >
                      <span>{tag}</span>
                      <button
                        onClick={() => removeTag(tag)}
                        className="text-blue-400 hover:text-blue-300"
                      >
                        ×
                      </button>
                    </span>
                  ))}
                </div>
                <input
                  type="text"
                  onKeyPress={(e) => {
                    if (e.key === 'Enter') {
                      e.preventDefault();
                      addTag(e.currentTarget.value);
                      e.currentTarget.value = '';
                    }
                  }}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500"
                  placeholder="Type a tag and press Enter"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-300 mb-2">
                  Strategy Configuration (JSON)
                </label>
                <textarea
                  value={JSON.stringify(formData.codeJson, null, 2)}
                  onChange={(e) => {
                    try {
                      const parsed = JSON.parse(e.target.value);
                      setFormData(prev => ({ ...prev, codeJson: parsed }));
                    } catch (error) {
                      // Invalid JSON, keep the text but don't update codeJson
                    }
                  }}
                  className="w-full px-3 py-2 bg-gray-700 border border-gray-600 rounded-lg text-white focus:ring-2 focus:ring-blue-500 h-32 font-mono text-sm"
                  placeholder='{"indicators": {"sma_period": 20}, "entry_conditions": {}}'
                />
                <p className="text-gray-500 text-xs mt-1">
                  Enter valid JSON configuration for your strategy
                </p>
              </div>
            </div>

            <div className="flex items-center justify-end space-x-3 mt-6">
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={() => {
                  setShowEditModal(false);
                  setSelectedStrategy(null);
                  resetForm();
                }}
                className="px-4 py-2 bg-gray-600 hover:bg-gray-500 text-white rounded-lg"
              >
                Cancel
              </motion.button>
              <motion.button
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
                onClick={handleUpdateStrategy}
                disabled={submitting || !formData.name.trim()}
                className="px-4 py-2 bg-blue-600 hover:bg-blue-700 disabled:bg-gray-600 disabled:cursor-not-allowed text-white rounded-lg"
              >
                {submitting ? 'Updating...' : 'Update Strategy'}
              </motion.button>
            </div>
          </motion.div>
        </div>
      )}

      {/* View Strategy Modal */}
      {showViewModal && selectedStrategy && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <motion.div
            initial={{ opacity: 0, scale: 0.9 }}
            animate={{ opacity: 1, scale: 1 }}
            className="bg-gray-800 border border-gray-700 rounded-xl p-6 w-full max-w-4xl max-h-[90vh] overflow-y-auto"
          >
            <div className="flex items-center justify-between mb-6">
              <div className="flex items-center space-x-3">
                <h3 className="text-2xl font-semibold text-white">{selectedStrategy.name}</h3>
                {selectedStrategy.isPublic ? (
                  <Globe className="h-5 w-5 text-green-400" />
                ) : (
                  <Lock className="h-5 w-5 text-yellow-400" />
                )}
              </div>
              <button
                onClick={() => setShowViewModal(false)}
                className="text-gray-400 hover:text-white"
              >
                ×
              </button>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <div className="lg:col-span-2 space-y-6">
                {/* Description */}
                {selectedStrategy.description && (
                  <div>
                    <h4 className="text-lg font-medium text-white mb-2">Description</h4>
                    <p className="text-gray-300">{selectedStrategy.description}</p>
                  </div>
                )}

                {/* Strategy Configuration */}
                <div>
                  <h4 className="text-lg font-medium text-white mb-2">Configuration</h4>
                  <pre className="bg-gray-900 border border-gray-600 rounded-lg p-4 text-sm text-gray-300 overflow-x-auto">
                    {JSON.stringify(selectedStrategy.codeJson, null, 2)}
                  </pre>
                </div>
              </div>

              <div className="space-y-6">
                {/* Metadata */}
                <div>
                  <h4 className="text-lg font-medium text-white mb-3">Details</h4>
                  <div className="space-y-3">
                    <div className="flex items-center space-x-2">
                      <User className="h-4 w-4 text-gray-400" />
                      <span className="text-gray-300">Created by {selectedStrategy.ownerName}</span>
                    </div>
                    <div className="flex items-center space-x-2">
                      <Calendar className="h-4 w-4 text-gray-400" />
                      <span className="text-gray-300">
                        Created {new Date(selectedStrategy.createdAt).toLocaleDateString()}
                      </span>
                    </div>
                    {selectedStrategy.publishedAt && (
                      <div className="flex items-center space-x-2">
                        <Globe className="h-4 w-4 text-green-400" />
                        <span className="text-green-300">
                          Published {new Date(selectedStrategy.publishedAt).toLocaleDateString()}
                        </span>
                      </div>
                    )}
                  </div>
                </div>

                {/* Tags */}
                {selectedStrategy.tags && selectedStrategy.tags.length > 0 && (
                  <div>
                    <h4 className="text-lg font-medium text-white mb-3">Tags</h4>
                    <div className="flex flex-wrap gap-2">
                      {selectedStrategy.tags.map((tag, index) => (
                        <span
                          key={index}
                          className="px-3 py-1 bg-blue-600/20 text-blue-400 text-sm rounded-full"
                        >
                          {tag}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Actions */}
                {selectedStrategy.canEdit && (
                  <div className="space-y-3">
                    <h4 className="text-lg font-medium text-white">Actions</h4>
                    <div className="space-y-2">
                      <motion.button
                        whileHover={{ scale: 1.02 }}
                        whileTap={{ scale: 0.98 }}
                        onClick={() => {
                          setShowViewModal(false);
                          openEditModal(selectedStrategy);
                        }}
                        className="w-full flex items-center space-x-2 px-3 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg"
                      >
                        <Edit className="h-4 w-4" />
                        <span>Edit Strategy</span>
                      </motion.button>
                      
                      {!selectedStrategy.isPublic ? (
                        <motion.button
                          whileHover={{ scale: 1.02 }}
                          whileTap={{ scale: 0.98 }}
                          onClick={() => {
                            setShowViewModal(false);
                            handlePublishStrategy(selectedStrategy);
                          }}
                          className="w-full flex items-center space-x-2 px-3 py-2 bg-green-600 hover:bg-green-700 text-white rounded-lg"
                        >
                          <Share2 className="h-4 w-4" />
                          <span>Publish</span>
                        </motion.button>
                      ) : (
                        <motion.button
                          whileHover={{ scale: 1.02 }}
                          whileTap={{ scale: 0.98 }}
                          onClick={() => {
                            setShowViewModal(false);
                            handleUnpublishStrategy(selectedStrategy);
                          }}
                          className="w-full flex items-center space-x-2 px-3 py-2 bg-yellow-600 hover:bg-yellow-700 text-white rounded-lg"
                        >
                          <EyeOff className="h-4 w-4" />
                          <span>Unpublish</span>
                        </motion.button>
                      )}
                    </div>
                  </div>
                )}
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </div>
  );
};

export default StrategyManagement;
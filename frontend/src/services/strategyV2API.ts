import api from './api';

export interface StrategyV2 {
  id: number;
  name: string;
  description?: string;
  codeJson: Record<string, any>;
  tags?: string[];
  isPublic: boolean;
  ownerId: number;
  ownerName: string;
  ownerUsername: string;
  createdAt: string;
  updatedAt: string;
  publishedAt?: string;
  canEdit?: boolean;
  canDelete?: boolean;
  canPublish?: boolean;
}

export interface StrategyV2CreateRequest {
  name: string;
  description?: string;
  codeJson?: Record<string, any>;
  tags?: string[];
}

export interface StrategyV2UpdateRequest {
  name: string;
  description?: string;
  codeJson?: Record<string, any>;
  tags?: string[];
}

export interface StrategyV2Statistics {
  totalStrategies: number;
  publicStrategies: number;
  privateStrategies: number;
}

export const strategyV2API = {
  // Get all strategies (filtered by role)
  getStrategies: (search?: string) => {
    const params = search ? { search } : {};
    return api.get('/api/strategies-v2', { params });
  },

  // Get a specific strategy by ID
  getStrategy: (id: number) =>
    api.get(`/api/strategies-v2/${id}`),

  // Create a new strategy (Researcher only)
  createStrategy: (strategy: StrategyV2CreateRequest) =>
    api.post('/api/strategies-v2', strategy),

  // Update an existing strategy (Researcher only, own strategies)
  updateStrategy: (id: number, strategy: StrategyV2UpdateRequest) =>
    api.put(`/api/strategies-v2/${id}`, strategy),

  // Delete a strategy (Researcher only, own strategies)
  deleteStrategy: (id: number) =>
    api.delete(`/api/strategies-v2/${id}`),

  // Publish a strategy (make it public)
  publishStrategy: (id: number) =>
    api.post(`/api/strategies-v2/${id}/publish`),

  // Unpublish a strategy (make it private)
  unpublishStrategy: (id: number) =>
    api.post(`/api/strategies-v2/${id}/unpublish`),

  // Search strategies
  searchStrategies: (searchTerm: string) =>
    api.get('/api/strategies-v2', { params: { search: searchTerm } }),

  // Get strategy statistics
  getStatistics: () =>
    api.get('/api/strategies-v2/statistics'),

  // Health check
  health: () =>
    api.get('/api/strategies-v2/health'),
};

export default strategyV2API;
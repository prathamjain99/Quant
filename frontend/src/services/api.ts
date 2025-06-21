import axios from 'axios';

// Configure axios defaults
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8081';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
});

// Add request interceptor to include authorization headers
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  
  if (user) {
    const userData = JSON.parse(user);
    config.headers['X-User-Id'] = userData.id;
    config.headers['X-User-Role'] = userData.role;
  }
  
  return config;
});

// Add response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API service functions
export const authAPI = {
  login: (credentials: { username: string; password: string }) =>
    api.post('/api/auth/login', credentials),
  
  register: (userData: any) =>
    api.post('/api/auth/register', userData),
  
  health: () =>
    api.get('/api/auth/health'),
};

export const strategyAPI = {
  getStrategies: () =>
    api.get('/api/strategies'),
  
  createStrategy: (strategy: any) =>
    api.post('/api/strategies', strategy),
  
  getStrategy: (id: string) =>
    api.get(`/api/strategies/${id}`),
};

export const backtestAPI = {
  runBacktest: (params: any) =>
    api.post('/api/backtest', params),
  
  getHistory: () =>
    api.get('/api/backtest/history'),
};

export const productAPI = {
  getProducts: () =>
    api.get('/api/products'),
  
  createProduct: (product: any) =>
    api.post('/api/products', product),
  
  getProduct: (id: string) =>
    api.get(`/api/products/${id}`),
  
  getMyProducts: () =>
    api.get('/api/products/my-products'),
};

export const portfolioAPI = {
  getPortfolio: () =>
    api.get('/api/portfolio'),
  
  createPosition: (params: any) =>
    api.post('/api/portfolio/positions', null, { params }),
};

export const marketDataAPI = {
  getMarketData: (symbol: string, days: number = 252) =>
    api.get(`/api/market-data/${symbol}?days=${days}`),
};

export const userAPI = {
  getProfile: () =>
    api.get('/api/users/profile'),
  
  updateProfile: (profile: any) =>
    api.post('/api/users/profile', profile),
};

export const analyticsAPI = {
  getRiskMetrics: () =>
    api.get('/api/analytics/risk-metrics'),
};

export const reportingAPI = {
  generateReport: (request: any) =>
    api.post('/api/reports/generate', request),
};

export const bookingAPI = {
  bookTrade: (request: any) =>
    api.post('/api/trades/book', request),
  
  getTrades: () =>
    api.get('/api/trades'),
  
  getTrade: (id: string) =>
    api.get(`/api/trades/${id}`),
  
  updateTradeStatus: (id: string, status: string) =>
    api.put(`/api/trades/${id}/status?status=${status}`),
};

export const lifecycleAPI = {
  getTradeEvents: (tradeId: string) =>
    api.get(`/api/lifecycle/events/${tradeId}`),
  
  processFixings: () =>
    api.post('/api/lifecycle/process-fixings'),
  
  checkBarriers: () =>
    api.post('/api/lifecycle/check-barriers'),
};

export const pricingAPI = {
  calculatePrice: (request: any) =>
    api.post('/api/pricing/calculate', request),
  
  monteCarloPrice: (request: any) =>
    api.post('/api/pricing/monte-carlo', request),
};

export default api;
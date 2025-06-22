import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import Sidebar from './Sidebar';
import Header from './Header';
import Overview from './Overview';
import StrategyBuilder from '../Strategy/StrategyBuilder';
import StrategyManagement from '../Strategy/StrategyManagement';
import Backtesting from '../Strategy/Backtesting';
import ProductCreator from '../Products/ProductCreator';
import Portfolio from '../Portfolio/Portfolio';
import PortfolioManagement from '../Portfolio/PortfolioManagement';
import Analytics from '../Analytics/Analytics';
import Reports from '../Reports/Reports';
import TradingDesk from '../Trading/TradingDesk';

const Dashboard: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="flex h-screen bg-gray-900">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto bg-gray-900 p-6">
          <Routes>
            <Route path="/" element={<Overview />} />
            <Route path="/trading" element={<TradingDesk />} />
            <Route path="/strategies-v2" element={<StrategyManagement />} />
            {(user?.role === 'researcher' || user?.role === 'portfolio_manager') && (
              <>
                <Route path="/strategies" element={<StrategyBuilder />} />
                <Route path="/backtesting" element={<Backtesting />} />
              </>
            )}
            {user?.role === 'portfolio_manager' && (
              <Route path="/products" element={<ProductCreator />} />
            )}
            <Route path="/portfolio" element={<Portfolio />} />
            <Route path="/portfolios" element={<PortfolioManagement />} />
            <Route path="/analytics" element={<Analytics />} />
            <Route path="/reports" element={<Reports />} />
          </Routes>
        </main>
      </div>
    </div>
  );
};

export default Dashboard;
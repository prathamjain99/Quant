-- QuantCrux Database Schema and Sample Data
-- PostgreSQL Database Script

-- Create database (run this separately if needed)
-- CREATE DATABASE quantcrux;

-- Connect to the database
-- \c quantcrux;

-- Drop existing tables if they exist (for clean setup)
DROP TABLE IF EXISTS user_sessions CASCADE;
DROP TABLE IF EXISTS trades CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS strategy_assets CASCADE;
DROP TABLE IF EXISTS strategies CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Create Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(120) NOT NULL,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENT',
    is_active BOOLEAN DEFAULT TRUE,
    is_email_verified BOOLEAN DEFAULT FALSE,
    phone_number VARCHAR(15),
    department VARCHAR(50),
    last_login TIMESTAMP,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    
    CONSTRAINT chk_role CHECK (role IN ('CLIENT', 'PORTFOLIO_MANAGER', 'RESEARCHER', 'ADMIN')),
    CONSTRAINT chk_username_format CHECK (username ~ '^[a-zA-Z0-9_]+$'),
    CONSTRAINT chk_email_format CHECK (email ~ '^[^@\s]+@[^@\s]+\.[^@\s]+$')
);

-- Create User Sessions table
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_token VARCHAR(500) UNIQUE NOT NULL,
    refresh_token VARCHAR(100) UNIQUE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Strategies table
CREATE TABLE strategies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Indicators (embedded as columns for simplicity)
    ema_short INTEGER DEFAULT 10,
    ema_long INTEGER DEFAULT 20,
    rsi_period INTEGER DEFAULT 14,
    macd_fast INTEGER DEFAULT 12,
    macd_slow INTEGER DEFAULT 26,
    macd_signal INTEGER DEFAULT 9,
    
    -- Rules (embedded as columns for simplicity)
    entry_condition VARCHAR(50) DEFAULT 'ema_cross_up',
    exit_condition VARCHAR(50) DEFAULT 'ema_cross_down',
    stop_loss DECIMAL(5,2) DEFAULT 5.0,
    take_profit DECIMAL(5,2) DEFAULT 10.0,
    position_size DECIMAL(5,2) DEFAULT 1.0
);

-- Create Strategy Assets table (for many-to-many relationship)
CREATE TABLE strategy_assets (
    strategy_id BIGINT NOT NULL REFERENCES strategies(id) ON DELETE CASCADE,
    asset VARCHAR(20) NOT NULL,
    PRIMARY KEY (strategy_id, asset)
);

-- Create Products table
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    underlying_asset VARCHAR(20),
    strike DECIMAL(15,6) NOT NULL,
    barrier DECIMAL(15,6),
    coupon DECIMAL(8,6) NOT NULL,
    notional DECIMAL(15,2) NOT NULL,
    maturity_months INTEGER,
    issuer VARCHAR(50),
    currency VARCHAR(3) DEFAULT 'USD',
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_product_type CHECK (type IN ('digital_option', 'barrier_option', 'dual_currency'))
);

-- Create Trades table
CREATE TABLE trades (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    trade_type VARCHAR(10) NOT NULL,
    notional DECIMAL(15,2) NOT NULL,
    entry_price DECIMAL(15,6),
    current_price DECIMAL(15,6),
    notes TEXT,
    status VARCHAR(20) DEFAULT 'BOOKED',
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    trade_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_trade_type CHECK (trade_type IN ('BUY', 'SELL')),
    CONSTRAINT chk_trade_status CHECK (status IN ('BOOKED', 'CONFIRMED', 'SETTLED', 'CANCELLED'))
);

-- Create indexes for better performance
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_active ON users(is_active);
CREATE INDEX idx_users_last_login ON users(last_login);

CREATE INDEX idx_sessions_token ON user_sessions(session_token);
CREATE INDEX idx_sessions_refresh_token ON user_sessions(refresh_token);
CREATE INDEX idx_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_sessions_expires_at ON user_sessions(expires_at);
CREATE INDEX idx_sessions_active ON user_sessions(is_active);

CREATE INDEX idx_strategies_user_id ON strategies(user_id);
CREATE INDEX idx_strategies_name ON strategies(name);

CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_type ON products(type);
CREATE INDEX idx_products_underlying ON products(underlying_asset);

CREATE INDEX idx_trades_user_id ON trades(user_id);
CREATE INDEX idx_trades_product_id ON trades(product_id);
CREATE INDEX idx_trades_status ON trades(status);
CREATE INDEX idx_trades_date ON trades(trade_date);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Insert sample users with encrypted passwords
-- Note: These passwords are BCrypt hashed for "password"
INSERT INTO users (username, email, password, name, role, is_active, is_email_verified, phone_number, department, created_by) VALUES
('admin', 'admin@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'System Administrator', 'ADMIN', true, true, '+1-555-0001', 'IT', 'system'),
('client1', 'client1@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'John Client', 'CLIENT', true, true, '+1-555-0101', 'Investment', 'admin'),
('client2', 'client2@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Jane Investor', 'CLIENT', true, true, '+1-555-0102', 'Investment', 'admin'),
('pm1', 'pm1@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Michael Portfolio', 'PORTFOLIO_MANAGER', true, true, '+1-555-0201', 'Portfolio Management', 'admin'),
('pm2', 'pm2@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Sarah Manager', 'PORTFOLIO_MANAGER', true, true, '+1-555-0202', 'Portfolio Management', 'admin'),
('researcher1', 'researcher1@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'David Research', 'RESEARCHER', true, true, '+1-555-0301', 'Quantitative Research', 'admin'),
('researcher2', 'researcher2@quantcrux.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lisa Analyst', 'RESEARCHER', true, true, '+1-555-0302', 'Quantitative Research', 'admin');

-- Insert sample strategies
INSERT INTO strategies (name, description, user_id, ema_short, ema_long, rsi_period, entry_condition, exit_condition, stop_loss, take_profit, position_size) VALUES
('Momentum Strategy', 'Simple momentum strategy using EMA crossover', 6, 10, 20, 14, 'ema_cross_up', 'ema_cross_down', 5.0, 10.0, 1.0),
('Mean Reversion Strategy', 'RSI-based mean reversion strategy', 6, 5, 15, 14, 'rsi_oversold', 'rsi_overbought', 3.0, 8.0, 0.5),
('Trend Following Strategy', 'MACD-based trend following', 7, 12, 26, 14, 'macd_bullish', 'macd_bearish', 4.0, 12.0, 1.5),
('Scalping Strategy', 'Short-term scalping strategy', 7, 5, 10, 7, 'ema_cross_up', 'ema_cross_down', 2.0, 5.0, 2.0);

-- Insert strategy assets
INSERT INTO strategy_assets (strategy_id, asset) VALUES
(1, 'SPY'), (1, 'QQQ'),
(2, 'EUR/USD'), (2, 'GBP/USD'),
(3, 'AAPL'), (3, 'MSFT'), (3, 'GOOGL'),
(4, 'EUR/USD'), (4, 'USD/JPY');

-- Insert sample products
INSERT INTO products (name, type, underlying_asset, strike, barrier, coupon, notional, maturity_months, issuer, currency, user_id) VALUES
('EUR/USD Digital Option Q1 2024', 'digital_option', 'EUR/USD', 1.1000, NULL, 0.08, 100000, 3, 'Dealer 1', 'USD', 4),
('S&P 500 Barrier Note', 'barrier_option', 'SPY', 4000.00, 3600.00, 0.12, 500000, 12, 'Dealer 2', 'USD', 4),
('GBP/USD Dual Currency Investment', 'dual_currency', 'GBP/USD', 1.2500, NULL, 0.06, 250000, 6, 'Dealer 1', 'USD', 5),
('AAPL Digital Call Option', 'digital_option', 'AAPL', 150.00, NULL, 0.10, 100000, 1, 'Dealer 3', 'USD', 5),
('EUR/USD Knock-In Barrier', 'barrier_option', 'EUR/USD', 1.0800, 1.0500, 0.15, 300000, 9, 'Dealer 2', 'USD', 4);

-- Insert sample trades
INSERT INTO trades (product_id, trade_type, notional, entry_price, current_price, notes, status, user_id) VALUES
(1, 'BUY', 100000, 100.50, 102.25, 'Initial position in EUR/USD digital option', 'CONFIRMED', 2),
(2, 'BUY', 250000, 98.75, 101.30, 'Barrier note investment', 'SETTLED', 3),
(3, 'SELL', 150000, 103.20, 99.80, 'Short position in GBP/USD product', 'CONFIRMED', 2),
(4, 'BUY', 75000, 95.40, 97.15, 'AAPL digital option', 'BOOKED', 3),
(1, 'BUY', 50000, 101.75, 102.25, 'Additional EUR/USD exposure', 'CONFIRMED', 2);

-- Update last login times for demo users
UPDATE users SET last_login = CURRENT_TIMESTAMP - INTERVAL '1 hour' WHERE username IN ('client1', 'pm1', 'researcher1');
UPDATE users SET last_login = CURRENT_TIMESTAMP - INTERVAL '2 days' WHERE username IN ('client2', 'pm2');
UPDATE users SET last_login = CURRENT_TIMESTAMP - INTERVAL '1 week' WHERE username = 'researcher2';

-- Create a view for user statistics
CREATE VIEW user_stats AS
SELECT 
    role,
    COUNT(*) as total_users,
    COUNT(CASE WHEN is_active = true THEN 1 END) as active_users,
    COUNT(CASE WHEN last_login > CURRENT_TIMESTAMP - INTERVAL '7 days' THEN 1 END) as recent_logins
FROM users 
GROUP BY role;

-- Create a view for portfolio summary
CREATE VIEW portfolio_summary AS
SELECT 
    u.id as user_id,
    u.username,
    u.name,
    COUNT(t.id) as total_trades,
    SUM(t.notional) as total_notional,
    SUM(CASE WHEN t.trade_type = 'BUY' THEN t.notional ELSE -t.notional END) as net_position,
    AVG(CASE WHEN t.current_price IS NOT NULL AND t.entry_price IS NOT NULL 
        THEN (t.current_price - t.entry_price) / t.entry_price * 100 END) as avg_pnl_percent
FROM users u
LEFT JOIN trades t ON u.id = t.user_id AND t.status IN ('CONFIRMED', 'SETTLED')
GROUP BY u.id, u.username, u.name;

-- Grant permissions (adjust as needed for your setup)
-- GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO quantcrux_user;
-- GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO quantcrux_user;

-- Display sample data
SELECT 'Users created:' as info, COUNT(*) as count FROM users;
SELECT 'Strategies created:' as info, COUNT(*) as count FROM strategies;
SELECT 'Products created:' as info, COUNT(*) as count FROM products;
SELECT 'Trades created:' as info, COUNT(*) as count FROM trades;

-- Show user statistics
SELECT * FROM user_stats ORDER BY role;

-- Show portfolio summary
SELECT * FROM portfolio_summary ORDER BY total_notional DESC;

COMMIT;
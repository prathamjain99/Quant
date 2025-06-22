/*
  # Portfolio Management Module

  1. New Tables
    - `portfolios`
      - `id` (bigserial, primary key)
      - `name` (varchar, portfolio name)
      - `description` (text, portfolio description)
      - `user_id` (bigint, foreign key to users)
      - `total_value` (decimal, computed total portfolio value)
      - `total_investment` (decimal, computed total investment)
      - `total_pnl` (decimal, computed profit/loss)
      - `pnl_percentage` (decimal, computed P&L percentage)
      - `sharpe_ratio` (decimal, computed Sharpe ratio)
      - `risk_score` (decimal, computed risk score)
      - `position_count` (integer, number of positions)
      - `is_active` (boolean, soft delete flag)
      - `created_at` (timestamp)
      - `updated_at` (timestamp)
      - `last_calculated` (timestamp, when metrics were last calculated)

  2. Schema Changes
    - Add `portfolio_id` column to existing `trades` table
    - Create indexes for performance
    - Add constraints and validation

  3. Security
    - Enable RLS on `portfolios` table
    - Add policies for user access control
    - Ensure users can only access their own portfolios

  4. Sample Data
    - Create sample portfolios for demo users
    - Link existing trades to portfolios
    - Calculate initial metrics
*/

-- Create portfolios table
CREATE TABLE IF NOT EXISTS portfolios (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- Computed metrics
    total_value DECIMAL(15,2) DEFAULT 0.00,
    total_investment DECIMAL(15,2) DEFAULT 0.00,
    total_pnl DECIMAL(15,2) DEFAULT 0.00,
    pnl_percentage DECIMAL(8,4) DEFAULT 0.0000,
    sharpe_ratio DECIMAL(8,4) DEFAULT 0.0000,
    risk_score DECIMAL(5,2) DEFAULT 50.00,
    position_count INTEGER DEFAULT 0,
    
    -- Status and timestamps
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_calculated TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_portfolio_name_length CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 100),
    CONSTRAINT chk_portfolio_description_length CHECK (description IS NULL OR LENGTH(description) <= 500),
    CONSTRAINT chk_risk_score_range CHECK (risk_score >= 0 AND risk_score <= 100),
    CONSTRAINT unique_portfolio_name_per_user UNIQUE (user_id, name, is_active)
);

-- Add portfolio_id column to trades table if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'trades' AND column_name = 'portfolio_id'
    ) THEN
        ALTER TABLE trades ADD COLUMN portfolio_id BIGINT REFERENCES portfolios(id) ON DELETE SET NULL;
    END IF;
END $$;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_portfolios_user_id ON portfolios(user_id);
CREATE INDEX IF NOT EXISTS idx_portfolios_name ON portfolios(name);
CREATE INDEX IF NOT EXISTS idx_portfolios_active ON portfolios(is_active);
CREATE INDEX IF NOT EXISTS idx_portfolios_user_active ON portfolios(user_id, is_active);
CREATE INDEX IF NOT EXISTS idx_portfolios_last_calculated ON portfolios(last_calculated);
CREATE INDEX IF NOT EXISTS idx_trades_portfolio_id ON trades(portfolio_id);

-- Enable Row Level Security
ALTER TABLE portfolios ENABLE ROW LEVEL SECURITY;

-- Create RLS policies for portfolios
CREATE POLICY "Users can view their own portfolios"
    ON portfolios
    FOR SELECT
    TO authenticated
    USING (auth.uid()::text = user_id::text);

CREATE POLICY "Users can create their own portfolios"
    ON portfolios
    FOR INSERT
    TO authenticated
    WITH CHECK (auth.uid()::text = user_id::text);

CREATE POLICY "Users can update their own portfolios"
    ON portfolios
    FOR UPDATE
    TO authenticated
    USING (auth.uid()::text = user_id::text)
    WITH CHECK (auth.uid()::text = user_id::text);

CREATE POLICY "Users can delete their own portfolios"
    ON portfolios
    FOR DELETE
    TO authenticated
    USING (auth.uid()::text = user_id::text);

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_portfolios_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_portfolios_updated_at 
    BEFORE UPDATE ON portfolios
    FOR EACH ROW 
    EXECUTE FUNCTION update_portfolios_updated_at();

-- Function to calculate portfolio metrics
CREATE OR REPLACE FUNCTION calculate_portfolio_metrics(portfolio_id_param BIGINT)
RETURNS VOID AS $$
DECLARE
    total_investment_calc DECIMAL(15,2) := 0;
    total_value_calc DECIMAL(15,2) := 0;
    total_pnl_calc DECIMAL(15,2) := 0;
    pnl_percentage_calc DECIMAL(8,4) := 0;
    position_count_calc INTEGER := 0;
    sharpe_ratio_calc DECIMAL(8,4) := 0;
    risk_score_calc DECIMAL(5,2) := 50.00;
BEGIN
    -- Calculate metrics from active trades
    SELECT 
        COALESCE(SUM(notional * entry_price / 100), 0),
        COALESCE(SUM(notional * COALESCE(current_price, entry_price) / 100), 0),
        COUNT(*)
    INTO 
        total_investment_calc,
        total_value_calc,
        position_count_calc
    FROM trades 
    WHERE portfolio_id = portfolio_id_param 
    AND status IN ('CONFIRMED', 'SETTLED');
    
    -- Calculate P&L
    total_pnl_calc := total_value_calc - total_investment_calc;
    
    -- Calculate P&L percentage
    IF total_investment_calc > 0 THEN
        pnl_percentage_calc := (total_pnl_calc / total_investment_calc) * 100;
    END IF;
    
    -- Mock Sharpe ratio calculation (simplified)
    IF total_investment_calc > 0 THEN
        sharpe_ratio_calc := (pnl_percentage_calc / 100) * 2 + (RANDOM() - 0.5) * 0.4;
        sharpe_ratio_calc := GREATEST(-3, LEAST(3, sharpe_ratio_calc));
    END IF;
    
    -- Mock risk score calculation
    risk_score_calc := 50 + (RANDOM() - 0.5) * 20;
    IF position_count_calc > 5 THEN
        risk_score_calc := risk_score_calc - 10;
    END IF;
    IF total_value_calc > 1000000 THEN
        risk_score_calc := risk_score_calc - 5;
    END IF;
    risk_score_calc := GREATEST(0, LEAST(100, risk_score_calc));
    
    -- Update portfolio with calculated metrics
    UPDATE portfolios SET
        total_value = total_value_calc,
        total_investment = total_investment_calc,
        total_pnl = total_pnl_calc,
        pnl_percentage = pnl_percentage_calc,
        sharpe_ratio = sharpe_ratio_calc,
        risk_score = risk_score_calc,
        position_count = position_count_calc,
        last_calculated = CURRENT_TIMESTAMP
    WHERE id = portfolio_id_param;
END;
$$ LANGUAGE plpgsql;

-- Insert sample portfolios for demo users
INSERT INTO portfolios (name, description, user_id, created_at) VALUES
('Growth Portfolio', 'Aggressive growth strategy focusing on high-potential assets', 2, CURRENT_TIMESTAMP - INTERVAL '30 days'),
('Conservative Income', 'Low-risk portfolio focused on steady income generation', 2, CURRENT_TIMESTAMP - INTERVAL '25 days'),
('Balanced Allocation', 'Diversified portfolio balancing growth and income', 3, CURRENT_TIMESTAMP - INTERVAL '20 days'),
('Tech Innovation Fund', 'Technology-focused investment portfolio', 3, CURRENT_TIMESTAMP - INTERVAL '15 days'),
('Quantitative Alpha', 'Algorithm-driven systematic trading portfolio', 4, CURRENT_TIMESTAMP - INTERVAL '10 days'),
('Multi-Asset Strategy', 'Cross-asset class diversified portfolio', 5, CURRENT_TIMESTAMP - INTERVAL '5 days'),
('Research Portfolio', 'Experimental strategies and new product testing', 6, CURRENT_TIMESTAMP - INTERVAL '3 days'),
('Market Neutral Fund', 'Long-short equity market neutral strategy', 7, CURRENT_TIMESTAMP - INTERVAL '1 day');

-- Link existing trades to portfolios (distribute randomly for demo)
UPDATE trades SET portfolio_id = (
    SELECT p.id 
    FROM portfolios p 
    WHERE p.user_id = trades.user_id 
    ORDER BY RANDOM() 
    LIMIT 1
) WHERE portfolio_id IS NULL;

-- Calculate initial metrics for all portfolios
DO $$
DECLARE
    portfolio_record RECORD;
BEGIN
    FOR portfolio_record IN SELECT id FROM portfolios WHERE is_active = TRUE
    LOOP
        PERFORM calculate_portfolio_metrics(portfolio_record.id);
    END LOOP;
END $$;

-- Create a view for portfolio statistics
CREATE OR REPLACE VIEW portfolio_statistics AS
SELECT 
    u.id as user_id,
    u.username,
    u.name as user_name,
    u.role,
    COUNT(p.id) as total_portfolios,
    COALESCE(SUM(p.total_value), 0) as total_portfolio_value,
    COALESCE(AVG(p.pnl_percentage), 0) as avg_pnl_percentage,
    COALESCE(SUM(p.position_count), 0) as total_positions,
    COALESCE(AVG(p.sharpe_ratio), 0) as avg_sharpe_ratio,
    COALESCE(AVG(p.risk_score), 0) as avg_risk_score
FROM users u
LEFT JOIN portfolios p ON u.id = p.user_id AND p.is_active = true
GROUP BY u.id, u.username, u.name, u.role;

-- Create a view for portfolio details with trade information
CREATE OR REPLACE VIEW portfolio_details AS
SELECT 
    p.id,
    p.name,
    p.description,
    p.user_id,
    u.username,
    u.name as user_name,
    p.total_value,
    p.total_investment,
    p.total_pnl,
    p.pnl_percentage,
    p.sharpe_ratio,
    p.risk_score,
    p.position_count,
    p.is_active,
    p.created_at,
    p.updated_at,
    p.last_calculated,
    COUNT(t.id) as actual_trade_count,
    COALESCE(SUM(CASE WHEN t.status IN ('CONFIRMED', 'SETTLED') THEN 1 ELSE 0 END), 0) as active_trade_count
FROM portfolios p
JOIN users u ON p.user_id = u.id
LEFT JOIN trades t ON p.id = t.portfolio_id
WHERE p.is_active = true
GROUP BY p.id, p.name, p.description, p.user_id, u.username, u.name,
         p.total_value, p.total_investment, p.total_pnl, p.pnl_percentage,
         p.sharpe_ratio, p.risk_score, p.position_count, p.is_active,
         p.created_at, p.updated_at, p.last_calculated;

-- Display summary of created data
SELECT 'Portfolios created:' as info, COUNT(*) as count FROM portfolios WHERE is_active = true;
SELECT 'Trades linked to portfolios:' as info, COUNT(*) as count FROM trades WHERE portfolio_id IS NOT NULL;

-- Show portfolio statistics by user
SELECT * FROM portfolio_statistics ORDER BY total_portfolio_value DESC;

-- Show sample portfolio details
SELECT 
    name,
    user_name,
    total_value,
    total_pnl,
    pnl_percentage,
    position_count,
    sharpe_ratio,
    risk_score
FROM portfolio_details 
ORDER BY total_value DESC 
LIMIT 5;

COMMIT;
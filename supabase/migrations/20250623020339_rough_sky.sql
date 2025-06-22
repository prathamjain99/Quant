/*
  # Strategy Management System

  1. New Tables
    - `strategies_v2`
      - `id` (bigserial, primary key)
      - `name` (varchar, strategy name)
      - `description` (text, strategy description)
      - `code_json` (jsonb, strategy logic/configuration)
      - `tags` (text[], array of tags)
      - `is_public` (boolean, public visibility flag)
      - `owner_id` (bigint, foreign key to users)
      - `created_at` (timestamp)
      - `updated_at` (timestamp)
      - `published_at` (timestamp, when made public)

  2. Security
    - Enable RLS on `strategies_v2` table
    - Add policies for role-based access control
    - Researchers can CRUD their own strategies
    - Portfolio Managers can view all strategies
    - Clients can view only public strategies

  3. Sample Data
    - Create 3 dummy strategies for testing
    - Mix of public and private strategies
*/

-- Create strategies_v2 table (enhanced version)
CREATE TABLE IF NOT EXISTS strategies_v2 (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    code_json JSONB NOT NULL DEFAULT '{}',
    tags TEXT[] DEFAULT '{}',
    is_public BOOLEAN DEFAULT FALSE,
    owner_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_strategy_name_length CHECK (LENGTH(name) >= 2 AND LENGTH(name) <= 100),
    CONSTRAINT chk_strategy_description_length CHECK (description IS NULL OR LENGTH(description) <= 2000),
    CONSTRAINT unique_strategy_name_per_owner UNIQUE (owner_id, name)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_strategies_v2_owner_id ON strategies_v2(owner_id);
CREATE INDEX IF NOT EXISTS idx_strategies_v2_is_public ON strategies_v2(is_public);
CREATE INDEX IF NOT EXISTS idx_strategies_v2_tags ON strategies_v2 USING GIN(tags);
CREATE INDEX IF NOT EXISTS idx_strategies_v2_name ON strategies_v2(name);
CREATE INDEX IF NOT EXISTS idx_strategies_v2_published_at ON strategies_v2(published_at);

-- Enable Row Level Security
ALTER TABLE strategies_v2 ENABLE ROW LEVEL SECURITY;

-- Create RLS policies for strategies_v2

-- Researchers can view their own strategies
CREATE POLICY "Researchers can view their own strategies"
    ON strategies_v2
    FOR SELECT
    TO authenticated
    USING (
        owner_id = (SELECT id FROM users WHERE username = current_user)
        AND (SELECT role FROM users WHERE username = current_user) = 'RESEARCHER'
    );

-- Portfolio Managers can view all strategies
CREATE POLICY "Portfolio Managers can view all strategies"
    ON strategies_v2
    FOR SELECT
    TO authenticated
    USING (
        (SELECT role FROM users WHERE username = current_user) = 'PORTFOLIO_MANAGER'
    );

-- Clients can view only public strategies
CREATE POLICY "Clients can view public strategies"
    ON strategies_v2
    FOR SELECT
    TO authenticated
    USING (
        is_public = TRUE
        AND (SELECT role FROM users WHERE username = current_user) = 'CLIENT'
    );

-- Researchers can create strategies
CREATE POLICY "Researchers can create strategies"
    ON strategies_v2
    FOR INSERT
    TO authenticated
    WITH CHECK (
        owner_id = (SELECT id FROM users WHERE username = current_user)
        AND (SELECT role FROM users WHERE username = current_user) = 'RESEARCHER'
    );

-- Researchers can update their own strategies
CREATE POLICY "Researchers can update their own strategies"
    ON strategies_v2
    FOR UPDATE
    TO authenticated
    USING (
        owner_id = (SELECT id FROM users WHERE username = current_user)
        AND (SELECT role FROM users WHERE username = current_user) = 'RESEARCHER'
    )
    WITH CHECK (
        owner_id = (SELECT id FROM users WHERE username = current_user)
        AND (SELECT role FROM users WHERE username = current_user) = 'RESEARCHER'
    );

-- Researchers can delete their own strategies
CREATE POLICY "Researchers can delete their own strategies"
    ON strategies_v2
    FOR DELETE
    TO authenticated
    USING (
        owner_id = (SELECT id FROM users WHERE username = current_user)
        AND (SELECT role FROM users WHERE username = current_user) = 'RESEARCHER'
    );

-- Create trigger to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_strategies_v2_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_strategies_v2_updated_at 
    BEFORE UPDATE ON strategies_v2
    FOR EACH ROW 
    EXECUTE FUNCTION update_strategies_v2_updated_at();

-- Function to publish a strategy
CREATE OR REPLACE FUNCTION publish_strategy(strategy_id_param BIGINT, user_id_param BIGINT)
RETURNS BOOLEAN AS $$
DECLARE
    strategy_exists BOOLEAN := FALSE;
    user_is_researcher BOOLEAN := FALSE;
BEGIN
    -- Check if strategy exists and belongs to user
    SELECT EXISTS(
        SELECT 1 FROM strategies_v2 
        WHERE id = strategy_id_param AND owner_id = user_id_param
    ) INTO strategy_exists;
    
    -- Check if user is a researcher
    SELECT role = 'RESEARCHER' FROM users WHERE id = user_id_param INTO user_is_researcher;
    
    IF strategy_exists AND user_is_researcher THEN
        UPDATE strategies_v2 
        SET is_public = TRUE, published_at = CURRENT_TIMESTAMP
        WHERE id = strategy_id_param AND owner_id = user_id_param;
        RETURN TRUE;
    END IF;
    
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Insert sample strategies for testing
INSERT INTO strategies_v2 (name, description, code_json, tags, is_public, owner_id, created_at) VALUES
(
    'Momentum Breakout Strategy',
    'A quantitative strategy that identifies momentum breakouts using technical indicators and volume analysis.',
    '{
        "indicators": {
            "sma_short": 20,
            "sma_long": 50,
            "rsi_period": 14,
            "volume_threshold": 1.5
        },
        "entry_conditions": {
            "price_above_sma_short": true,
            "sma_short_above_sma_long": true,
            "rsi_above": 60,
            "volume_spike": true
        },
        "exit_conditions": {
            "stop_loss_percent": 5,
            "take_profit_percent": 15,
            "rsi_below": 30
        },
        "risk_management": {
            "max_position_size": 0.1,
            "max_drawdown": 0.15
        }
    }',
    ARRAY['momentum', 'breakout', 'technical-analysis', 'high-frequency'],
    TRUE,
    (SELECT id FROM users WHERE username = 'researcher1'),
    CURRENT_TIMESTAMP - INTERVAL '15 days'
),
(
    'Mean Reversion Pairs Trading',
    'Statistical arbitrage strategy that trades pairs of correlated assets when their price relationship deviates from historical norms.',
    '{
        "pairs": [
            {"asset1": "AAPL", "asset2": "MSFT", "correlation_threshold": 0.8},
            {"asset1": "JPM", "asset2": "BAC", "correlation_threshold": 0.75}
        ],
        "parameters": {
            "lookback_period": 60,
            "z_score_entry": 2.0,
            "z_score_exit": 0.5,
            "correlation_min": 0.7
        },
        "position_sizing": {
            "max_leverage": 2.0,
            "position_limit": 0.05
        },
        "risk_controls": {
            "max_holding_period": 30,
            "stop_loss_z_score": 3.0
        }
    }',
    ARRAY['pairs-trading', 'mean-reversion', 'statistical-arbitrage', 'market-neutral'],
    FALSE,
    (SELECT id FROM users WHERE username = 'researcher1'),
    CURRENT_TIMESTAMP - INTERVAL '10 days'
),
(
    'Multi-Factor Alpha Model',
    'Advanced quantitative model combining fundamental, technical, and sentiment factors to generate alpha signals.',
    '{
        "factors": {
            "fundamental": {
                "pe_ratio_weight": 0.2,
                "roe_weight": 0.15,
                "debt_to_equity_weight": -0.1,
                "revenue_growth_weight": 0.25
            },
            "technical": {
                "momentum_weight": 0.3,
                "volatility_weight": -0.15,
                "relative_strength_weight": 0.2
            },
            "sentiment": {
                "analyst_revisions_weight": 0.1,
                "news_sentiment_weight": 0.05,
                "options_flow_weight": 0.15
            }
        },
        "model_parameters": {
            "rebalance_frequency": "monthly",
            "universe_size": 500,
            "factor_decay": 0.95,
            "min_market_cap": 1000000000
        },
        "portfolio_construction": {
            "max_weight_per_stock": 0.03,
            "sector_neutral": true,
            "turnover_limit": 0.5
        }
    }',
    ARRAY['multi-factor', 'alpha-model', 'quantitative', 'long-only', 'institutional'],
    TRUE,
    (SELECT id FROM users WHERE username = 'researcher2'),
    CURRENT_TIMESTAMP - INTERVAL '5 days'
);

-- Create a view for strategy statistics
CREATE OR REPLACE VIEW strategy_statistics AS
SELECT 
    u.role,
    COUNT(*) as total_strategies,
    COUNT(CASE WHEN s.is_public = true THEN 1 END) as public_strategies,
    COUNT(CASE WHEN s.is_public = false THEN 1 END) as private_strategies,
    AVG(array_length(s.tags, 1)) as avg_tags_per_strategy
FROM users u
LEFT JOIN strategies_v2 s ON u.id = s.owner_id
GROUP BY u.role;

-- Create a view for public strategies with owner info
CREATE OR REPLACE VIEW public_strategies_view AS
SELECT 
    s.id,
    s.name,
    s.description,
    s.tags,
    s.created_at,
    s.published_at,
    u.name as owner_name,
    u.username as owner_username
FROM strategies_v2 s
JOIN users u ON s.owner_id = u.id
WHERE s.is_public = true
ORDER BY s.published_at DESC;

-- Display summary of created data
SELECT 'Strategies created:' as info, COUNT(*) as count FROM strategies_v2;
SELECT 'Public strategies:' as info, COUNT(*) as count FROM strategies_v2 WHERE is_public = true;
SELECT 'Private strategies:' as info, COUNT(*) as count FROM strategies_v2 WHERE is_public = false;

-- Show strategy statistics by role
SELECT * FROM strategy_statistics ORDER BY role;

-- Show public strategies
SELECT 
    name,
    owner_name,
    array_length(tags, 1) as tag_count,
    created_at,
    published_at
FROM public_strategies_view;

COMMIT;
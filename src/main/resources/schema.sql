CREATE TABLE IF NOT EXISTS players (
    player_id                 INTEGER PRIMARY KEY,
    player_name               VARCHAR(100) NOT NULL,
    age                       SMALLINT,
    nationality               VARCHAR(60),
    club                      VARCHAR(80),
    position                  VARCHAR(10),
    overall_rating            SMALLINT,
    potential_rating          SMALLINT,
    matches_played            INTEGER,
    goals                     INTEGER,
    assists                   INTEGER,
    minutes_played            INTEGER,
    market_value_million_eur  NUMERIC(10,2),
    contract_years_left       SMALLINT,
    injury_prone              VARCHAR(5),
    transfer_risk_level       VARCHAR(10)
);

-- Acelera GET /fifa/nat/{exp}
CREATE INDEX IF NOT EXISTS idx_players_nationality ON players (lower(nationality));

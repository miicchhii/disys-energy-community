CREATE TABLE IF NOT EXISTS historical_entry (
    hour TIMESTAMP PRIMARY KEY,
    community_produced DOUBLE PRECISION,
    community_used    DOUBLE PRECISION,
    grid_used         DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS current_entry (
    hour TIMESTAMP PRIMARY KEY,
    community_depleted DOUBLE PRECISION,
    grid_portion       DOUBLE PRECISION
);
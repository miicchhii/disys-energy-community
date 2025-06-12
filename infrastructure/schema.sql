CREATE TABLE historical_entry
(
    hour               TIMESTAMP PRIMARY KEY,
    community_produced DOUBLE PRECISION, -- Amount of energy produced by the community
    community_used     DOUBLE PRECISION, -- Amount of energy used by the community
    grid_used          DOUBLE PRECISION  -- Amount of energy used from the grid
);

CREATE TABLE current_entry
(
    hour               TIMESTAMP PRIMARY KEY,
    community_depleted DOUBLE PRECISION, -- Amount of energy depleted from community storage
    grid_portion       DOUBLE PRECISION  -- Portion of energy coming from the grid
);

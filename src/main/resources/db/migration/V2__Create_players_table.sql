CREATE TABLE players (
                         id SERIAL PRIMARY KEY,
                         club_id INTEGER NOT NULL REFERENCES clubs(id),
                         given_name VARCHAR(255) NOT NULL,
                         family_name VARCHAR(255) NOT NULL,
                         nationality VARCHAR(255) NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         date_of_birth DATE NOT NULL
);

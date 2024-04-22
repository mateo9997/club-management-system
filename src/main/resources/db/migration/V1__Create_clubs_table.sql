CREATE TABLE clubs (
                       id SERIAL PRIMARY KEY,
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       official_name VARCHAR(255) NOT NULL,
                       popular_name VARCHAR(255),
                       federation VARCHAR(8) NOT NULL,
                       is_public BOOLEAN DEFAULT FALSE
);

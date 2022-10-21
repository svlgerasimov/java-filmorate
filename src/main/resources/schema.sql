DROP TABLE IF EXISTS film, users, film_genre, genre, mpa, likes, friends;

CREATE TABLE IF NOT EXISTS mpa
(
    id INT PRIMARY KEY,
    name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS film
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(200),
    release_date DATE,
    duration INT,
    mpa_id BIGINT REFERENCES mpa (id) ON DELETE SET NULL,
    CONSTRAINT duration_check CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS genre
(
    id INT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id BIGINT REFERENCES film (id) ON DELETE CASCADE,
    genre_id INT REFERENCES genre (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(64) NOT NULL,
    login VARCHAR(64) NOT NULL,
    name VARCHAR(64) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    friend_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    is_mutual BOOLEAN DEFAULT false,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT friends_dif_id_check CHECK (user_id <> friend_id)
);

CREATE TABLE IF NOT EXISTS likes
(
    film_id BIGINT REFERENCES film (id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES users (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, user_id)
);
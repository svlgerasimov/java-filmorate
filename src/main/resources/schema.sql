DROP TABLE IF EXISTS film, users, film_genre, genre, mpa, likes, friends, reviews, like_review, dislike_review, events, director, film_directors;

CREATE TABLE IF NOT EXISTS mpa
(
    id   INT PRIMARY KEY,
    name VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS director
(
    DIRECTOR_ID BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME        CHARACTER VARYING(64) not null
);

CREATE TABLE IF NOT EXISTS film
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(64) NOT NULL,
    description  VARCHAR(200),
    release_date DATE,
    duration     INT,
    mpa_id       INT         NOT NULL REFERENCES mpa (id),
    CONSTRAINT duration_check CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS film_directors
(
    film_id BIGINT references film(id) ON DELETE CASCADE,
    director_id BIGINT references director(DIRECTOR_ID) ON DELETE CASCADE,
    PRIMARY KEY(film_id, director_id)
);

CREATE TABLE IF NOT EXISTS genre
(
    id   INT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_id  BIGINT REFERENCES film (id) ON DELETE CASCADE,
    genre_id INT REFERENCES genre (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users
(
    id       BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    VARCHAR(64) NOT NULL,
    login    VARCHAR(64) NOT NULL,
    name     VARCHAR(64) NOT NULL,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS friends
(
    user_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
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




CREATE TABLE IF NOT EXISTS events
(
    event_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    timestamp  TIMESTAMP DEFAULT current_timestamp,
    user_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    event_type VARCHAR NOT NULL,
    operation  VARCHAR NOT NULL,
    entity_id  BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS reviews
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    content    VARCHAR(150) NOT NULL,
    ispositive BOOLEAN      NOT NULL,
    user_id    BIGINT REFERENCES users (id) ON DELETE CASCADE,
    film_id    BIGINT REFERENCES film (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS like_review
(
    user_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    review_id BIGINT REFERENCES reviews (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, review_id)
);

CREATE TABLE IF NOT EXISTS dislike_review
(
    user_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    review_id BIGINT REFERENCES reviews (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, review_id)
);
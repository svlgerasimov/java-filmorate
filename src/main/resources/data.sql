
MERGE INTO mpa (id, name) KEY (id)
VALUES (1 ,'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');

MERGE INTO genre (id, name) KEY (id)
VALUES (1, 'Комедия'),
       (2, 'Драма'),
       (3, 'Мультфильм'),
       (4, 'Триллер'),
       (5, 'Документальный'),
       (6, 'Боевик');
--SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id,
  --     d.name FROM film AS f
    -- JOIN film_directors AS fd ON fd.film_id = f.id
     --JOIN director AS d ON d.director_id = fd.director_id


--SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id,
--      d.name, m.id AS mpa_id, m.name AS mpa_name, COUNT(DISTINCT l.user_id) AS
--           rate FROM film AS f
--               LEFT JOIN mpa AS m ON m.id=f.mpa_id
--               JOIN film_directors AS fd ON fd.film_id = f.id
--               JOIN director AS d ON d.director_id = fd.director_id
--               LEFT JOIN likes AS l ON l.film_id=f.id
--                WHERE d.director_id = 1 GROUP BY f.id, d.director_id;

--delete from director where director_id = 1;

--SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id,
--       d.name, g.name, g.id, fg.film_id,
 --                       fg.genre_id FROM film AS f
  --                          LEFT JOIN film_genre AS fg ON f.id = fg.film_id
  --                          LEFT JOIN genre AS g ON fg.genre_id = g.id
  --                          LEFT JOIN film_directors AS fd ON fd.film_id = f.id
  --                          LEFT JOIN director AS d ON d.director_id = fd.director_id
  --                          WHERE d.director_id = 1 GROUP BY f.id, d.director_id;
--SELECT f.id, f.name, f.description, f.release_date, f.duration, d.director_id,
--       d.name AS dirName, m.id AS mpa_id, m.name AS mpa_name, COUNT(DISTINCT l.user_id) AS rate,
--       g.name AS genreName, g.id AS genreId, fg.film_id, fg.genre_id FROM film AS f
--        LEFT JOIN film_genre AS fg ON f.id = fg.film_id
--        LEFT JOIN genre AS g ON fg.genre_id = g.id
--       LEFT JOIN mpa AS m ON m.id=f.mpa_id
--        JOIN film_directors AS fd ON fd.film_id = f.id
--        JOIN director AS d ON d.director_id = fd.director_id
--        LEFT JOIN likes AS l ON l.film_id=f.id
--                WHERE d.director_id = 1 GROUP BY f.id, d.director_id;


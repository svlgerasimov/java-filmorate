
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

--
--INSERT INTO mpa (name)
--    SELECT init.name FROM mpa
--    RIGHT JOIN
--    	(SELECT 'G' AS name
--    	UNION ALL SELECT 'PG'
--    	UNION ALL SELECT 'PG-13'
--    	UNION ALL SELECT 'R'
--    	UNION ALL SELECT 'NC-17'
--    	) AS init ON mpa.NAME = init.name
--    WHERE mpa.NAME IS NULL;
--
--INSERT INTO genre (name)
--    SELECT init.name FROM mpa
--    RIGHT JOIN
--    	(SELECT 'Комедия' AS name
--    	UNION ALL SELECT 'Драма'
--    	UNION ALL SELECT 'Мультфильм'
--    	UNION ALL SELECT 'Триллер'
--    	UNION ALL SELECT 'Документальный'
--    	UNION ALL SELECT 'Боевик'
--    	) AS init ON mpa.NAME = init.name
--    WHERE mpa.NAME IS NULL;
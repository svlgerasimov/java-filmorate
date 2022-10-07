
INSERT INTO mpa (name)
    SELECT init.name FROM mpa
    RIGHT JOIN
    	(SELECT 'G' AS name
    	UNION ALL SELECT 'PG'
    	UNION ALL SELECT 'PG-13'
    	UNION ALL SELECT 'R'
    	UNION ALL SELECT 'NC-17'
    	) AS init ON mpa.NAME = init.name
    WHERE mpa.NAME IS NULL;

INSERT INTO genre (name)
    SELECT init.name FROM mpa
    RIGHT JOIN
    	(SELECT 'Комедия' AS name
    	UNION ALL SELECT 'Драма'
    	UNION ALL SELECT 'Мультфильм'
    	UNION ALL SELECT 'Триллер'
    	UNION ALL SELECT 'Документальный'
    	UNION ALL SELECT 'Боевик'
    	) AS init ON mpa.NAME = init.name
    WHERE mpa.NAME IS NULL;
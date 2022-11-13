package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LikesDbStorage implements LikesStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean addLike(long filmId, long userId) {
        String sql = "MERGE INTO likes (film_id, user_id) VALUES (?, ?);";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public boolean removeLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id=? AND user_id=?;";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    @Override
    public List<Long> findSimilarUsers(long userId, int limit) {
        // Пользователи, отсортированные по количеству общих лайков с выбранным
        String sql = "WITH user_likes AS\n" +
                "    (SELECT f.id AS film_id\n" +
                "     FROM FILM f\n" +
                "         JOIN LIKES l on f.ID = l.FILM_ID\n" +
                "     WHERE l.USER_ID = ?)\n" +
                "SELECT l.USER_ID AS user_id, COUNT(*) AS count\n" +
                "FROM user_likes AS ul\n" +
                "    JOIN likes AS l ON l.FILM_ID = ul.film_id\n" +
                "WHERE user_id <> ?\n" +
                "GROUP BY l.USER_ID\n" +
                "ORDER BY count DESC\n" +
                "LIMIT ?;";
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                userId, userId, limit);
    }
}

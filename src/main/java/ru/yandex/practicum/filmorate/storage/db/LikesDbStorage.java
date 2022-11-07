package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

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
    public boolean removeLikesByFilm(long filmId) {
        String sql = "DELETE FROM LIKES WHERE film_id = ?";
        return jdbcTemplate.update(sql, filmId) > 0;
    }

    @Override
    public boolean removeLikesByUser(long userId) {
        String sql = "DELETE FROM likes WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId) > 0;
    }
}

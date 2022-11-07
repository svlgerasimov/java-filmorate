package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ReviewsDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public long addReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource()
                .addValue("content", review.getContent())
                .addValue("ispositive", review.getIsPositive())
                .addValue("user_id", review.getUserId())
                .addValue("film_id", review.getFilmId());
        return simpleJdbcInsert.executeAndReturnKey(mapSqlParameterSource).longValue();


        /*String sqlQueryReview = "INSERT INTO reviews (content, ispositive, user_Id, film_id) VALUES( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sqlQueryReview, new String[]{"id"});
                ps.setString(1, review.getContent());
                ps.setBoolean(2, review.getIsPositive());
                ps.setLong(3, review.getUserId());
                ps.setLong(4, review.getFilmId());
                return ps;
            }, keyHolder);

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new DataBaseException("Ошибка добавления Review в БД");
        }
        return Objects.requireNonNull(keyHolder.getKey()).intValue();*/
    }

    @Override
    public boolean updateReview(Review review) {
        String sql = "UPDATE reviews " +
                "SET content=?, ispositive=?  WHERE id = ?;"; //user_id=?, film_id=?
        return jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
               // review.getUserId(),
               // review.getFilmId(),
                review.getReviewId()) > 0;
    }

    @Override
    public boolean removeReview(long id) {
        String sql = "DELETE FROM reviews WHERE id=?;";
        return jdbcTemplate.update(sql, id) > 0;

    }

    @Override
    public Optional<Review> getReviewById(long id) {
        String sql = "SELECT r.id, r.content, r.ispositive, r.user_id, r.film_id, " +
                "COUNT(lr.user_id) - COUNT(dr.user_id) AS useful " +
                "FROM reviews r " +
                "LEFT JOIN like_review lr ON r.id = lr.review_id " +
                "LEFT JOIN dislike_review dr ON r.id = dr.review_id " +
                "WHERE r.id = ? " +
                "GROUP BY r.id ORDER BY useful DESC; ";
        List<Review> reviews = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id);
        return reviews.isEmpty() ? Optional.empty() : Optional.of(reviews.get(0));
    }

    @Override
    public List<Review> getAllReview(long filmId, int count) { //  если фильм не указан то все. Если кол-во не указано то 10.
        String where = "";
        if (filmId != 0) {
            where = "WHERE r.film_id =" + filmId;
        }
        String sql = "SELECT r.id, r.content, r.ispositive, r.user_id, r.film_id, " +
                "COUNT(lr.user_id) - COUNT(dr.user_id) useful " +
                "FROM reviews r " +
                "LEFT JOIN like_review lr ON r.id = lr.review_id " +
                "LEFT JOIN dislike_review dr ON r.id = dr.review_id " +
                where +
                "GROUP BY r.id ORDER BY useful DESC LIMIT ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
    }

    @Override
    public boolean addLikeReview(long reviewId, long userId) {
        String sql = "MERGE INTO like_review (user_id, review_id) VALUES (?, ?);";
        return jdbcTemplate.update(sql, userId, reviewId) > 0;
    }

    @Override
    public boolean addDislikeReview(long reviewId, long userId) {
        String sql = "MERGE INTO dislike_review (user_id, review_id) VALUES (?, ?);";
        return jdbcTemplate.update(sql, userId, reviewId) > 0;
    }

    @Override
    public boolean deleteLikeReview(long reviewId, long userId) {
        String sql = "DELETE FROM like_review WHERE user_id=? AND review_id=?;";
        return jdbcTemplate.update(sql, userId, reviewId) > 0;
    }

    @Override
    public boolean deleteDislikeReview(long reviewId, long userId) {
        String sql = "DELETE FROM dislike_review WHERE user_id=? AND review_id=?;";
        return jdbcTemplate.update(sql, userId, reviewId) > 0;
    }

    private Review makeReview(ResultSet resultSet) throws SQLException {
        return new Review(resultSet.getLong("id"),
                resultSet.getString("content"),
                resultSet.getBoolean("ispositive"),
                resultSet.getLong("user_id"),
                resultSet.getLong("film_id"),
                resultSet.getInt("useful"));
    }
}

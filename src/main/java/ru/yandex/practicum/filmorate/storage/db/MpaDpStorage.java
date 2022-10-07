package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MpaDpStorage implements MpaStorage {
    private static final String fieldId = "id";
    private static final String fieldName = "name";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = "SELECT id, name FROM mpa;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> getMpaById(long id) {
        String sql = "SELECT id, name FROM mpa WHERE id = ?;";
        return jdbcTemplate
                .queryForStream(sql, (rs, rowNum) -> makeMpa(rs), id)
                .findFirst();
    }

    private static Mpa makeMpa(ResultSet resultSet) throws SQLException {
        return new Mpa(resultSet.getLong(fieldId), resultSet.getString(fieldName));
    }
}

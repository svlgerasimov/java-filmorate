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
    private static final String TABLE_MPA = "mpa";
    private static final String FIELD_ID = "id";
    private static final String FIELD_NAME = "name";

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        String sql = String.format("SELECT %s, %s FROM %s;", FIELD_ID, FIELD_NAME, TABLE_MPA);
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> getMpaById(long id) {
        String sql = String.format("SELECT %s, %s FROM %s WHERE %s = ?;", FIELD_ID, FIELD_NAME, TABLE_MPA, FIELD_ID);
        return jdbcTemplate
                .queryForStream(sql, (rs, rowNum) -> makeMpa(rs), id)
                .findFirst();
    }

    private static Mpa makeMpa(ResultSet resultSet) throws SQLException {
        return new Mpa(resultSet.getInt(FIELD_ID), resultSet.getString(FIELD_NAME));
    }
}

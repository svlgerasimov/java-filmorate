package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT id, name FROM mpa;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Optional<Mpa> getById(long id) {
        String sql = "SELECT id, name FROM mpa WHERE id=?;";
        List<Mpa> mpas = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id);
        return mpas.isEmpty() ? Optional.empty() : Optional.of(mpas.get(0));
    }

    private static Mpa makeMpa(ResultSet resultSet) throws SQLException {
        return new Mpa(resultSet.getInt("id"), resultSet.getString("name"));
    }
}

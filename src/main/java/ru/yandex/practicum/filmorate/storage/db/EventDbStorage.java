package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventOperation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.EventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long userId, EventType eventType, EventOperation operation, long entityId) {
        String sql = "INSERT INTO events (user_id, event_type, operation, entity_id) VALUES (?, ?, ?, ?);";
        jdbcTemplate.update(sql, userId, eventType.name(), operation.name(), entityId);
    }

    @Override
    public List<Event> getAll(long userId) {
        String sql = "SELECT event_id, timestamp, user_id, event_type, operation, entity_id " +
                "FROM events " +
                "WHERE user_id = ?;";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), userId);
    }

    public static Event makeEvent(ResultSet resultSet) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getInt("event_id"))
                .timestamp(resultSet.getTimestamp("timestamp").getTime())
                .userId(resultSet.getInt("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(EventOperation.valueOf(resultSet.getString("operation")))
                .entityId(resultSet.getInt("entity_id"))
                .build();
    }
}

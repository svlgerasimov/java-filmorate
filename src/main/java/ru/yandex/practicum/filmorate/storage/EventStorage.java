package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventStorage {

    void addEvent(long userId, EventType eventType, EventOperation operation, long entityId);

    Collection<Event> getAllEvents(long userId);
}

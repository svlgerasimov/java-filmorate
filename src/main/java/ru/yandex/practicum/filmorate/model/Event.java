package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.storage.EventOperation;
import ru.yandex.practicum.filmorate.storage.EventType;

@Value
@Builder
public class Event {

    long eventId;

    long timestamp;

    long userId;

    EventType eventType;

    EventOperation operation;

    long entityId;
}

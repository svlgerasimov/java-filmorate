package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Value;
import ru.yandex.practicum.filmorate.storage.EventOperation;
import ru.yandex.practicum.filmorate.storage.EventType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Value
@Builder
public class Event {

    long eventId;

    long timestamp;

    long userId;

    @NotBlank(message = "EventType is blank.")
    EventType eventType;

    @NotBlank(message = "EventOperation is blank.")
    EventOperation operation;

    long entityId;
}

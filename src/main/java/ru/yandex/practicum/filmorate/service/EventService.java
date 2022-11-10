package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventOperation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.EventType;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

    private final EventStorage eventStorage;

    public Collection<Event> getAllEvents(long userId) {
        return eventStorage.getAllEvents(userId);
    }

    public void addEvent(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        eventStorage.addEvent(userId, eventType, eventOperation, entityId);
        log.debug("EventService: {} {} id={} from user id={}.", eventOperation, eventType, entityId,  userId);
    }
}

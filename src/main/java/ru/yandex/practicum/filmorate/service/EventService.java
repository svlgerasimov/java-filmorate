package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventOperation;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.EventType;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventService {

    private final EventStorage eventStorage;

    public List<Event> getAll(long userId) {
        return eventStorage.getAll(userId);
    }

    public void add(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        eventStorage.add(userId, eventType, eventOperation, entityId);
        log.debug("EventService: {} {} id={} from user id={}.", eventOperation, eventType, entityId,  userId);
    }
}

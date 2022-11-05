package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.LikesStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationsService {
    private final LikesStorage likesStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> formRecommendations(long userId, int similarUsersCount) {
        // Проверка, есть ли пользователь с этим id
        checkUserExists(userId);
        // Фильмы, которым пользователь уже поставил лайк
        Collection<Film> filmsLikesByUser = filmStorage.getFilmsLikedByUser(userId);
        // id пользователей, отсортированные по общим лайкам
        return likesStorage.findSimilarUsers(userId, similarUsersCount)
                .stream()
                // каждый id пользователя отображаем в коллекцию фильмов, которым он поставил лайк
                .map(filmStorage::getFilmsLikedByUser)
                // объединяем все фильмы в один стрим
                .flatMap(Collection::stream)
                // убираем повторы
                .distinct()
                // оставляем фильмы, которым пользователь ещё не поставил лайк
                .filter(film -> !filmsLikesByUser.contains(film))
                .collect(Collectors.toList());
    }

    private void checkUserExists(long id) {
        userStorage.getById(id)
                .orElseThrow(() ->
                        new NotFoundException(String.format("User id=%s not found", id)));
    }
}

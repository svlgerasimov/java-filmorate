package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RecommendationsService {

    private final static int SIMILAR_USERS_COUNT = 3;
    private final FilmService filmService;
    private final LikesStorage likesStorage;

    public List<Film> formRecommendations(long userId) {
        // Фильмы, которым пользователь уже поставил лайк, проверка наличия пользователя - в filmService
        List<Film> filmsLikesByUser = filmService.getFilmsLikedByUser(userId);
        // id пользователей, отсортированные по общим лайкам
        return likesStorage.findSimilarUsers(userId, SIMILAR_USERS_COUNT)
                .stream()
                // каждый id пользователя отображаем в коллекцию фильмов, которым он поставил лайк
                .map(filmService::getFilmsLikedByUser)
                // объединяем все фильмы в один стрим
                .flatMap(List::stream)
                // убираем повторы
                .distinct()
                // оставляем фильмы, которым пользователь ещё не поставил лайк
                .filter(film -> !filmsLikesByUser.contains(film))
                .collect(Collectors.toList());
    }
}

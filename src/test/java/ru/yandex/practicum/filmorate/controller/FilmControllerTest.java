package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FilmControllerTest {
//
//    public static final String NAME_CORRECT = "Film name";
//    public static final String DESCRIPTION_CORRECT = "Film description";
//    public static final LocalDate RELEASE_DATE_CORRECT = LocalDate.of(2000, Month.JANUARY, 1);
//    public static final LocalDate RELEASE_DATE_MIN = LocalDate.of(1895, Month.DECEMBER, 28);
//    public static final int DURATION_CORRECT = 90;
//    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
//
//    @Autowired
//    private MockMvcTestHelper mockMvcTestHelper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void getAllFilmsAndThenStatus200AndFilmsCollectionReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film);
//        film = film.withId(1);
//        mockMvcTestHelper.mockMvcPerformGetAllFilms()
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(List.of(film))));
//    }
//
//    @Test
//    void addFilmAndThenStatus200AndFilmReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addNullFilmAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(null)
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmFailNullNameAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0,null, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmFailEmptyNameAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0,"", DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmFailBlankNameAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0,"      ", DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmNullDescriptionAndThenStatus200AndFilmReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addFilmLimitDescriptionLengthAndThenStatus200AndFilmReturn() throws Exception {
//        String description = Stream.generate(() -> "a")
//                .limit(MAX_FILM_DESCRIPTION_LENGTH)
//                .collect(Collectors.joining());
//        Film film = new Film(0, NAME_CORRECT, description, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        Film expected = new Film(1, NAME_CORRECT, description, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addFilmFailTooLongDescriptionAndThenStatus400() throws Exception {
//        String description = Stream.generate(() -> "a")
//                .limit(MAX_FILM_DESCRIPTION_LENGTH + 1)
//                .collect(Collectors.joining());
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0, NAME_CORRECT, description, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmLimitReleaseDateAndThenStatus200AndFilmReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_MIN, DURATION_CORRECT,
//                null, List.of());
//        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_MIN, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addFilmFailTooOldReleaseDateAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_MIN.minusDays(1), DURATION_CORRECT,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmNullReleaseDateAndThenStatus200AndFilmReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, null, DURATION_CORRECT,
//                null, List.of());
//        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, null, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addFilmPositiveDurationAndThenStatus200AndFilmReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, 1,
//                null, List.of());
//        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, 1,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addFilmZeroDurationAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, 0,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addFilmNegativeDurationAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, -1,
//                        null, List.of()))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void updateFilmAndThenStatus200AndUserReturn() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film);
//        film = new Film(1, NAME_CORRECT + " updated", DESCRIPTION_CORRECT + " updated",
//                RELEASE_DATE_CORRECT, DURATION_CORRECT, null, List.of());
//        mockMvcTestHelper.mockMvcPerformPutFilm(film)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(film)));
//        mockMvcTestHelper.mockMvcPerformGetAllFilms()
//                .andExpect(content().json(objectMapper.writeValueAsString(List.of(film))));
//    }
//
//    @Test
//    void updateUserFailIncorrectIdAndThenStatus404() throws Exception {
//        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                null, List.of());
//        mockMvcTestHelper.mockMvcPerformPostFilm(film);
//        film = film.withId(-1);
//        mockMvcTestHelper.mockMvcPerformPutFilm(film)
//                .andExpect(status().isNotFound())
//                .andExpect(result ->
//                        Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException));
//    }
//
//    @Test
//    void getMostPopularFilmsFailNegativeCountAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostFilm(
//                new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT,
//                        null, List.of()));
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, UserControllerTest.EMAIL_CORRECT, UserControllerTest.LOGIN_CORRECT,
//                        UserControllerTest.NAME_CORRECT, UserControllerTest.BIRTHDAY_CORRECT));
//        mockMvcTestHelper.mockMvcPerformPutLike(1, 1);
//        mockMvcTestHelper.mockMvcPerformGetMostPopularFilms(-1)
//                .andExpect(status().isBadRequest());
//    }
}

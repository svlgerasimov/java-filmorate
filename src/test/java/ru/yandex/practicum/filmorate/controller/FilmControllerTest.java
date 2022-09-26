package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FilmControllerTest {

    public static final String URL_FILMS = "/films";

    private static final String NAME_CORRECT = "Film name";
    private static final String DESCRIPTION_CORRECT = "Film description";
    private static final LocalDate RELEASE_DATE_CORRECT = LocalDate.of(2000, Month.JANUARY, 1);
    private static final LocalDate RELEASE_DATE_MIN = LocalDate.of(1895, Month.DECEMBER, 28);
    private static final int DURATION_CORRECT = 90;
    private static final int MAX_FILM_DESCRIPTION_LENGTH = 200;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions mockMvcPerformGetAllFilms() throws Exception {
        return mockMvc.perform(get(URL_FILMS));
    }

    private ResultActions mockMvcPerformGetFilm(long id) throws Exception {
        return mockMvc.perform(get(URL_FILMS + "/" + id));
    }

    private ResultActions mockMvcPerformPostFilm(Film film) throws Exception {
        return mockMvc.perform(
                post(URL_FILMS)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions mockMvcPerformPutFilm(Film film) throws Exception {
        return mockMvc.perform(
                put(URL_FILMS)
                        .content(objectMapper.writeValueAsString(film))
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllFilmsAndThenStatus200AndFilmsCollectionReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPostFilm(film);
        film = film.withId(1);
        mockMvcPerformGetAllFilms()
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(film))));
    }

//    @Test
//    void getFilmByIdAndThenStatus200And

    @Test
    void addFilmAndThenStatus200AndFilmReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPostFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addNullFilmAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(null)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmFailNullNameAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(new Film(0,null, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmFailEmptyNameAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(new Film(0,"", DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmFailBlankNameAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(new Film(0,"      ", DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmNullDescriptionAndThenStatus200AndFilmReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPostFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addFilmLimitDescriptionLengthAndThenStatus200AndFilmReturn() throws Exception {
        String description = Stream.generate(() -> "a")
                .limit(MAX_FILM_DESCRIPTION_LENGTH)
                .collect(Collectors.joining());
        Film film = new Film(0, NAME_CORRECT, description, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        Film expected = new Film(1, NAME_CORRECT, description, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPostFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addFilmFailTooLongDescriptionAndThenStatus400() throws Exception {
        String description = Stream.generate(() -> "a")
                .limit(MAX_FILM_DESCRIPTION_LENGTH + 1)
                .collect(Collectors.joining());
        mockMvcPerformPostFilm(new Film(0, NAME_CORRECT, description, RELEASE_DATE_CORRECT, DURATION_CORRECT))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmLimitReleaseDateAndThenStatus200AndFilmReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_MIN, DURATION_CORRECT);
        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_MIN, DURATION_CORRECT);
        mockMvcPerformPostFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addFilmFailTooOldReleaseDateAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(
                new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_MIN.minusDays(1), DURATION_CORRECT))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmNullReleaseDateAndThenStatus200AndFilmReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, null, DURATION_CORRECT);
        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, null, DURATION_CORRECT);
        mockMvcPerformPostFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addFilmPositiveDurationAndThenStatus200AndFilmReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, 1);
        Film expected = new Film(1, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, 1);
        mockMvcPerformPostFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addFilmZeroDurationAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, 0))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addFilmNegativeDurationAndThenStatus400() throws Exception {
        mockMvcPerformPostFilm(new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, -1))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFilmAndThenStatus200AndUserReturn() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPostFilm(film);
        film = new Film(1, NAME_CORRECT + " updated", DESCRIPTION_CORRECT + " updated",
                RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPutFilm(film)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
        mockMvcPerformGetAllFilms()
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(film))));
    }

    @Test
    void updateUserFailIncorrectIdAndThenStatus404() throws Exception {
        Film film = new Film(0, NAME_CORRECT, DESCRIPTION_CORRECT, RELEASE_DATE_CORRECT, DURATION_CORRECT);
        mockMvcPerformPostFilm(film);
        film = film.withId(-1);
        mockMvcPerformPutFilm(film)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException));
    }
}

package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class UserControllerTest {

    public static final String URL_USERS = "/users";

    private static final String EMAIL_CORRECT = "mail@mail.ru";
    private static final String EMAIL_INCORRECT_1 = "mail.ru";
    private static final String EMAIL_INCORRECT_2 = ".mail@ru";
    private static final String EMAIL_INCORRECT_3 = "@mail.ru";
    private static final String EMAIL_INCORRECT_4 = "mail@ru.";
    private static final String EMAIL_INCORRECT_5 = "mail.ru@";
    private static final String LOGIN_CORRECT = "login";
    private static final String LOGIN_WITH_WHITESPACE = "login login";
    private static final String NAME_CORRECT = "name";
    private static final LocalDate BIRTHDAY_CORRECT = LocalDate.of(2000, Month.JANUARY, 1);
    private static final LocalDate BIRTHDAY_IN_FUTURE = LocalDate.of(4000, Month.JANUARY, 1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ResultActions mockMvcPerformGet() throws Exception {
        return mockMvc.perform(get(URL_USERS));
    }

    private ResultActions mockMvcPerformPost(User user) throws Exception {
        return mockMvc.perform(
                post(URL_USERS)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions mockMvcPerformPut(User user) throws Exception {
        return mockMvc.perform(
                put(URL_USERS)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllUsersAndThenStatus200AndUsersCollectionReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user);
        user.setId(1);
        mockMvcPerformGet()
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
    }

    @Test
    void addUserAndThenStatus200AndUserReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        User expected = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        expected.setName(NAME_CORRECT);
        expected.setId(1);
        mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addNullUserAndThenStatus400() throws Exception {
        mockMvcPerformPost(null)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailLoginWithWhitespaceAndThenStatus400() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_WITH_WHITESPACE, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailNullLoginAndThenStatus400() throws Exception {
        User user = new User(EMAIL_CORRECT, null, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailEmptyLoginAndThenStatus400() throws Exception {
        User user = new User(EMAIL_CORRECT, "", BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailBlankLoginAndThenStatus400() throws Exception {
        User user = new User(EMAIL_CORRECT, "    ", BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailNullEmailAndThenStatus400() throws Exception {
        User user = new User(null, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailEmptyEmailAndThenStatus400() throws Exception {
        User user = new User("", LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailIncorrect1EmailAndThenStatus400() throws Exception {
        User user = new User(EMAIL_INCORRECT_1, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailIncorrect2EmailAndThenStatus400() throws Exception {
        User user = new User(EMAIL_INCORRECT_2, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailIncorrect3EmailAndThenStatus400() throws Exception {
        User user = new User(EMAIL_INCORRECT_3, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailIncorrect4EmailAndThenStatus400() throws Exception {
        User user = new User(EMAIL_INCORRECT_4, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserFailIncorrect5EmailAndThenStatus400() throws Exception {
        User user = new User(EMAIL_INCORRECT_5, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserNullBirthdayAndThenStatus200AndUserReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, null);
        user.setName(NAME_CORRECT);
        User expected = new User(EMAIL_CORRECT, LOGIN_CORRECT, null);
        expected.setName(NAME_CORRECT);
        expected.setId(1);
        mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addUserFailIncorrectBirthdayAndThenStatus400() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_IN_FUTURE);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isBadRequest());
    }

    @Test
    void addUserNullNameAndThenStatus200AndUserReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        User expected = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        expected.setId(1);
        expected.setName(LOGIN_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addUserEmptyNameAndThenStatus200AndUserReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName("");
        User expected = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        expected.setId(1);
        expected.setName(LOGIN_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void addUserBlankNameAndThenStatus200AndUserReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName("       ");
        User expected = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        expected.setId(1);
        expected.setName(LOGIN_CORRECT);
        mockMvcPerformPost(user)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void updateUserAndThenStatus200AndUserReturn() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user);
        user.setId(1);
        user.setName(NAME_CORRECT + " updated");
        mockMvcPerformPut(user)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
        mockMvcPerformGet()
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
    }

    @Test
    void updateUserFailIncorrectIdAndThenStatus404() throws Exception {
        User user = new User(EMAIL_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
        user.setName(NAME_CORRECT);
        mockMvcPerformPost(user);
        user.setId(-1);
        mockMvcPerformPut(user)
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException));
    }
}
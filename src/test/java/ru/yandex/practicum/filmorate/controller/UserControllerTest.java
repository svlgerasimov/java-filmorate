package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
class UserControllerTest {
//
//    public static final String EMAIL_CORRECT = "mail@mail.ru";
//    private static final String EMAIL_INCORRECT_1 = "mail.ru";
//    private static final String EMAIL_INCORRECT_2 = ".mail@ru";
//    private static final String EMAIL_INCORRECT_3 = "@mail.ru";
//    private static final String EMAIL_INCORRECT_4 = "mail@ru.";
//    private static final String EMAIL_INCORRECT_5 = "mail.ru@";
//    public static final String LOGIN_CORRECT = "login";
//    private static final String LOGIN_WITH_WHITESPACE = "login login";
//    public static final String NAME_CORRECT = "name";
//    public static final LocalDate BIRTHDAY_CORRECT = LocalDate.of(2000, Month.JANUARY, 1);
//    private static final LocalDate BIRTHDAY_IN_FUTURE = LocalDate.of(4000, Month.JANUARY, 1);
//
//    @Autowired
//    private MockMvcTestHelper mockMvcTestHelper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    void getAllUsersAndThenStatus200AndUsersCollectionReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user);
//        user = user.withId(1);
//        mockMvcTestHelper.mockMvcPerformGetAllUsers()
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(List.of(user))));
//    }
//
//    @Test
//    void getUserByIdAndThenStatus200AndUserReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user);
//        user = user.withId(1);
//        mockMvcTestHelper.mockMvcPerformGetUser(1)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(user)));
//    }
//
//    @Test
//    void getUserFailIncorrectIdAndThenStatus404() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT));
//        mockMvcTestHelper.mockMvcPerformGetUser(-1)
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void addUserAndThenStatus200AndUserReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT);
//        User expected = new User(1, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addNullUserAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(null)
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailLoginWithWhitespaceAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, LOGIN_WITH_WHITESPACE, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailNullLoginAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, null, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailEmptyLoginAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, "", NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailBlankLoginAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, "    ", NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailNullEmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, null, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailEmptyEmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, "", LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailIncorrect1EmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_INCORRECT_1, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailIncorrect2EmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_INCORRECT_2, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailIncorrect3EmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_INCORRECT_3, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailIncorrect4EmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_INCORRECT_4, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailIncorrect5EmailAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_INCORRECT_5, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailNullBirthdayAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, null))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserFailIncorrectBirthdayAndThenStatus400() throws Exception {
//        mockMvcTestHelper.mockMvcPerformPostUser(
//                new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_IN_FUTURE))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void addUserNullNameAndThenStatus200AndUserReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, null, BIRTHDAY_CORRECT);
//        User expected = new User(1, EMAIL_CORRECT, LOGIN_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addUserEmptyNameAndThenStatus200AndUserReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, "", BIRTHDAY_CORRECT);
//        User expected = new User(1, EMAIL_CORRECT, LOGIN_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void addUserBlankNameAndThenStatus200AndUserReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, "         ", BIRTHDAY_CORRECT);
//        User expected = new User(1, EMAIL_CORRECT, LOGIN_CORRECT, LOGIN_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(expected)));
//    }
//
//    @Test
//    void updateUserAndThenStatus200AndUserReturn() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user);
//        User updated = new User(1, EMAIL_CORRECT, LOGIN_CORRECT + "_updated",
//                NAME_CORRECT + " updated", BIRTHDAY_CORRECT.minusYears(3));
//        mockMvcTestHelper.mockMvcPerformPutUser(updated)
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(updated)));
//        mockMvcTestHelper.mockMvcPerformGetAllUsers()
//                .andExpect(content().json(objectMapper.writeValueAsString(List.of(updated))));
//    }
//
//    @Test
//    void updateUserFailIncorrectIdAndThenStatus404() throws Exception {
//        User user = new User(0, EMAIL_CORRECT, LOGIN_CORRECT, NAME_CORRECT, BIRTHDAY_CORRECT);
//        mockMvcTestHelper.mockMvcPerformPostUser(user);
//        User updated = new User(-1, EMAIL_CORRECT, LOGIN_CORRECT + "_updated",
//                NAME_CORRECT + " updated", BIRTHDAY_CORRECT.minusYears(3));
//        mockMvcTestHelper.mockMvcPerformPutUser(updated)
//                .andExpect(status().isNotFound())
//                .andExpect(result ->
//                        Assertions.assertTrue(result.getResolvedException() instanceof NotFoundException));
//    }
}
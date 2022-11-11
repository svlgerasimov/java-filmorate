package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Component;

@Component
public class MockMvcTestHelper {
//    public static final String URL_FILMS = "/films";
//    public static final String URL_USERS = "/users";
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    public ResultActions mockMvcPerformGetAllFilms() throws Exception {
//        return mockMvc.perform(get(URL_FILMS));
//    }
//
//    public ResultActions mockMvcPerformGetFilm(long id) throws Exception {
//        return mockMvc.perform(get(URL_FILMS + "/" + id));
//    }
//
//    public ResultActions mockMvcPerformPostFilm(Film film) throws Exception {
//        return mockMvc.perform(
//                post(URL_FILMS)
//                        .content(objectMapper.writeValueAsString(film))
//                        .contentType(MediaType.APPLICATION_JSON));
//    }
//
//    public ResultActions mockMvcPerformPutFilm(Film film) throws Exception {
//        return mockMvc.perform(
//                put(URL_FILMS)
//                        .content(objectMapper.writeValueAsString(film))
//                        .contentType(MediaType.APPLICATION_JSON));
//    }
//
//    public ResultActions mockMvcPerformPutLike(long filmId, long userId) throws Exception {
//        return mockMvc.perform(
//                put(String.format("%s/%d/like/%d", URL_FILMS, filmId, userId)));
//    }
//
////    public ResultActions mockMvcPerformGetMostPopularFilms() throws Exception {
////        return mockMvc.perform(get("%s/%s", URL_FILMS, "popular"));
////    }
//
//    public ResultActions mockMvcPerformGetMostPopularFilms(int count) throws Exception {
//        return mockMvc.perform(get(String.format("%s/%s?count=%d", URL_FILMS, "popular", count)));
//    }
//
//    public ResultActions mockMvcPerformGetAllUsers() throws Exception {
//        return mockMvc.perform(get(URL_USERS));
//    }
//
//    public ResultActions mockMvcPerformGetUser(long id) throws Exception {
//        return mockMvc.perform(get(URL_USERS + "/" + id));
//    }
//
//    public ResultActions mockMvcPerformPostUser(User user) throws Exception {
//        return mockMvc.perform(
//                post(URL_USERS)
//                        .content(objectMapper.writeValueAsString(user))
//                        .contentType(MediaType.APPLICATION_JSON));
//    }
//
//    public ResultActions mockMvcPerformPutUser(User user) throws Exception {
//        return mockMvc.perform(
//                put(URL_USERS)
//                        .content(objectMapper.writeValueAsString(user))
//                        .contentType(MediaType.APPLICATION_JSON));
//    }
}

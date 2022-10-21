package ru.yandex.practicum.filmorate.storage.db;

import lombok.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.Month;

@NoArgsConstructor(staticName = "defaultBuilder")
public class TestUserBuilder {
    private long id;
    private String email = "test email";
    private String login = "test login";
    private String name = "test name";
    private LocalDate birthday = LocalDate.of(2001, Month.JANUARY, 1);

    public TestUserBuilder id(long id) {
        this.id = id;
        return this;
    }

    public TestUserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public TestUserBuilder login(String login) {
        this.login = login;
        return this;
    }

    public TestUserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TestUserBuilder birthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public User build() {
        return new User(id, email, login, name, birthday);
    }
}

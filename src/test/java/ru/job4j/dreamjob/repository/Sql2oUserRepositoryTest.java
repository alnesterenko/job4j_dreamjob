package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        var users = sql2oUserRepository.findAll();
        for (var oneUser : users) {
            sql2oUserRepository.deleteById(oneUser.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        User user = new User(0, "user@mail.ru", "admin", "1234");
        var userOptional = sql2oUserRepository.save(user);
        var savedUser = sql2oUserRepository.findById(userOptional.get().getId()).get();
        var wasFoundByEmailAndPassword = sql2oUserRepository.findByEmailAndPassword("user@mail.ru", "1234");
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(userOptional.get()).isEqualTo(wasFoundByEmailAndPassword.get());
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "user1@mail.ru", "admin1", "1234"));
        var user2 = sql2oUserRepository.save(new User(0, "user2@mail.ru", "admin2", "12345"));
        var user3 = sql2oUserRepository.save(new User(0, "user3@mail.ru", "admin3", "123456"));
        var result = sql2oUserRepository.findAll();
        assertThat(result).isEqualTo(List.of(user1.get(), user2.get(), user3.get()));
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        assertThat(sql2oUserRepository.findAll()).isEqualTo(emptyList());
        assertThat(sql2oUserRepository.findById(0)).isEqualTo(empty());
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        var user = sql2oUserRepository.save(new User(0, "user@mail.ru", "admin", "1234"));
        var isDeleted = sql2oUserRepository.deleteById(user.get().getId());
        var savedUser = sql2oUserRepository.findById(user.get().getId());
        var wasFoundByEmailAndPassword = sql2oUserRepository.findByEmailAndPassword(user.get().getEmail(), user.get().getPassword());
        assertThat(isDeleted).isTrue();
        assertThat(savedUser).isEqualTo(empty());
        assertThat(wasFoundByEmailAndPassword).isEqualTo(empty());
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        assertThat(sql2oUserRepository.deleteById(0)).isFalse();
    }

    @Test
    public void whenSaveTwice() {
        User uzver = new User(0, "user@mail.ru", "admin", "1234");
        var firstUser = sql2oUserRepository.save(uzver);
        var secondUser = sql2oUserRepository.save(uzver);
        var result = sql2oUserRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(firstUser.isPresent()).isTrue();
        assertThat(secondUser.isPresent()).isFalse();
    }

    @Test
    public void whenSavedTwoDifferentUsersWithTheSameEmail() {
        String email = "user@mail.ru";
        var firstUser = sql2oUserRepository.save(new User(0, email, "admin1", "1234"));
        var secondUser = sql2oUserRepository.save(new User(0, email, "admin2", "12345"));
        var wasFoundByEmailAndPassword1 = sql2oUserRepository.findByEmailAndPassword(email, "1234");
        var wasFoundByEmailAndPassword2 = sql2oUserRepository.findByEmailAndPassword(email, "12345");
        var result = sql2oUserRepository.findAll();
        assertThat(result.size()).isEqualTo(1);
        assertThat(firstUser.isPresent()).isTrue();
        assertThat(secondUser.isPresent()).isFalse();
        assertThat(wasFoundByEmailAndPassword1.isPresent()).isTrue();
        assertThat(wasFoundByEmailAndPassword2.isPresent()).isFalse();
    }
}
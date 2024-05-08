package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;
    private static Sql2o sql2o;

    @BeforeAll
    public static void initRepository() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");
        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("TRUNCATE TABLE users");
            query.executeUpdate();
        }
    }

    @Test
    void whenSaveUserThenIsPresent() {
        User user = new User(0, "mail@mail.ru", "Micky", "0000");
        Optional<User> optionalUser = sql2oUserRepository.save(user);
        assertThat(optionalUser.isPresent()).isTrue();
        assertThat(optionalUser.get().getId()).isNotZero();
    }

    @Test
    void whenMailRepeatedThenNull() {
        User user = new User(0, "mail@mail.ru", "Micky", "0000");
        Optional<User> optionalUser1 = sql2oUserRepository.save(user);
        Optional<User> optionalUser2 = sql2oUserRepository.save(user);
        assertThat(optionalUser1.isPresent()).isTrue();
        assertThat(optionalUser2.isPresent()).isFalse();
    }

    @Test
    void whenPasswordMatchesThenReturnUser() {
        User user = new User(0, "mail@mail.ru", "Micky", "0000");
        sql2oUserRepository.save(user);
        Optional<User> emailAndPassword = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(emailAndPassword.isPresent()).isTrue();
        assertThat(emailAndPassword.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(emailAndPassword.get().getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void whenPasswordNotMatchesThenReturnNull() {
        User user = new User(0, "mail@mail.ru", "Micky", "0000");
        sql2oUserRepository.save(user);
        Optional<User> emailAndPassword = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), "1111");
        assertThat(emailAndPassword.isPresent()).isFalse();
    }
}
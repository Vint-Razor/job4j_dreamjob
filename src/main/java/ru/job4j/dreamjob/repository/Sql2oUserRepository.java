package ru.job4j.dreamjob.repository;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;

import java.util.Objects;
import java.util.Optional;

@Repository
public class Sql2oUserRepository implements UserRepository {
    private final Sql2o sql2o;
    private static final Logger LOG = LoggerFactory.getLogger(Sql2oUserRepository.class.getName());

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        Optional<User> rsl = Optional.empty();
        try (Connection connection = sql2o.open()) {
            String sql = """
                    INSERT INTO users(email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedKey = query.executeUpdate().getKey(Integer.class);
            if (generatedKey > 0) {
                user.setId(generatedKey);
                rsl = Optional.of(user);
            }
        } catch (Sql2oException e) {
            LOG.error("Ошибка сохранения пользователя");
        }
        return rsl;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        Optional<User> rsl = Optional.empty();
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("SELECT * FROM users  WHERE email = :email");
            User user = query.addParameter("email", email).executeAndFetchFirst(User.class);
            if (user != null && password.equals(user.getPassword())) {
                rsl = Optional.of(user);
            }
            return rsl;
        }
    }
}

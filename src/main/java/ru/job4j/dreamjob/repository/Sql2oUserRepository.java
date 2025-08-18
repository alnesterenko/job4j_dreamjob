package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

@ThreadSafe
@Repository
public class Sql2oUserRepository implements UserRepository {

    private final Sql2o sql2o;

    /* Решил использовать встроенный в JDK логер чтобы не "заморачиваться" с зависимостями и настройками */
    private Logger logger = Logger.getLogger(Sql2oUserRepository.class.getName());

    public Sql2oUserRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Optional<User> save(User user) {
        Optional<User> optionalUser = Optional.empty();
        try (var connection = sql2o.open()) {
            var sql = """
                    INSERT INTO users(email, name, password)
                    VALUES (:email, :name, :password)
                    """;
            var query = connection.createQuery(sql, true)
                    .addParameter("email", user.getEmail())
                    .addParameter("name", user.getName())
                    .addParameter("password", user.getPassword());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            user.setId(generatedId);
            optionalUser = Optional.of(user);
        } catch (Sql2oException e) {
            /* В случае неудачи просто выводим сообщение об ошибке В КОНСОЛЬ */
            logger.info(e.getMessage());
        }
        return optionalUser;
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users WHERE email = :email AND password = :password");
            query.addParameter("email", email);
            query.addParameter("password", password);
            var user = query.executeAndFetchFirst(User.class);
            return Optional.ofNullable(user);
        }
    }

    /* Пришлось добавить ради удобства тестирования */
    @Override
    public Collection<User> findAll() {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("SELECT * FROM users");
            return query.executeAndFetch(User.class);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (var connection = sql2o.open()) {
            var query = connection.createQuery("DELETE FROM users WHERE id = :id");
            query.addParameter("id", id);
            /* Убеждаемся, что удаление затронуло хоть какие-то строки */
            return (query.executeUpdate().getResult()) > 0;
        }
    }

    @Override
    public Optional<User> findById(int id) {
            try (var connection = sql2o.open()) {
                var query = connection.createQuery("SELECT * FROM users WHERE id = :id");
                query.addParameter("id", id);
                var user = query.executeAndFetchFirst(User.class);
                return Optional.ofNullable(user);
            }
    }
}

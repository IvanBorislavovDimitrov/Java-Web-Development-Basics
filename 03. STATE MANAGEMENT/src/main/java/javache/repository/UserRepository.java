package javache.repository;

import javache.models.User;

import java.util.Collection;

public interface UserRepository {
    void save(User user);

    User getById(String id);

    Collection<User> getAll();
}

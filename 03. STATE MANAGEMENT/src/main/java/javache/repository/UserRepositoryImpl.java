package javache.repository;

import javache.io.UsersLoader;
import javache.io.UsersWriter;
import javache.models.User;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UserRepositoryImpl implements UserRepository {

    private Map<String, User> users;
    private UsersLoader usersLoader;
    private UsersWriter usersWriter;

    public UserRepositoryImpl() {
        this.usersLoader = new UsersLoader();
        this.usersWriter = new UsersWriter();
        this.users = new HashMap<>();
        this.initUsers();
    }

    @Override
    public void save(User user) {
        this.users.put(user.getId(), user);
        this.updateUsers();
    }

    @Override
    public User getById(String id) {
        return this.users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return this.users.values();
    }

    private void updateUsers() {
        User[] users = new User[this.users.size()];
        int index = 0;
        for (User user : this.users.values()) {
            users[index++] = user;
        }
        try {
            this.usersWriter.writeUsers(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initUsers() {
        try {
            User[] users = this.usersLoader.getUsers();
            if (users == null) {
                return;
            }
            for (User user : users) {
                this.users.put(user.getId(), user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

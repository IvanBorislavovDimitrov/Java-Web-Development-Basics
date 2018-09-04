package javache.models;

import com.google.gson.annotations.Expose;

import java.util.UUID;

public class User {

    @Expose
    private String id;

    @Expose
    private String email;

    @Expose
    private String password;

    public User() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

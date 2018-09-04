package javache.io;

import javache.models.User;
import javache.parsers.JSONParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public final class UsersLoader {

    private static final String PATH = "src/main/resources/files/";
    private static final String NAME = "users.json";
    private JSONParser jsonParser;

    public UsersLoader() {
        this.jsonParser = new JSONParser();
    }

    public User[] getUsers() throws IOException {
        File file = new File(PATH + NAME);
        List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
        StringBuilder sb = new StringBuilder();
        lines.forEach(sb::append);
        sb.trimToSize();

        User[] users = this.jsonParser.read(User[].class, sb.toString());

        return users;
    }
}

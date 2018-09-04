package javache.io;

import javache.models.User;
import javache.parsers.JSONParser;

import java.io.*;

public final class UsersWriter {

    private static final String PATH = "src/main/resources/files/";
    private static final String NAME = "users.json";
    private JSONParser jsonParser;

    public UsersWriter() {
        this.jsonParser = new JSONParser();
    }

    public void writeUsers(User[] users) throws IOException {
        String jsonContent = this.jsonParser.write(users);

        File file = new File(PATH + NAME);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(jsonContent);

        writer.close();
    }
}

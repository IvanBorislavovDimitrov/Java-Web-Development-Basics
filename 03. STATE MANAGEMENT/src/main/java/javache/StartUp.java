package javache;

import javache.constants.WebConstants;
import javache.server.Server;

import java.io.IOException;

public class StartUp {

    public static void main(String[] args) throws IOException {
        Server server = new Server(WebConstants.SERVER_PORT);

        server.run();
    }
}

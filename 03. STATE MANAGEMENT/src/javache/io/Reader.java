package javache.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public final class Reader {

    public Reader() {

    }

    public String readAllLines(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder sb = new StringBuilder();

        while (bufferedReader.ready()) {
            sb.append((char) bufferedReader.read());
        }

        return sb.toString();
    }
}

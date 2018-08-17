package javache.handlers;

import javache.http.HttpRequest;
import javache.http.HttpRequestImpl;
import javache.http.HttpResponse;
import javache.http.HttpResponseImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class RequestHandler {

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    public byte[] handleRequest(String requestContent) {

        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        if (!this.httpRequest.getMethod().equals("GET")) {
            return null;
        }

        this.constructHttpResponse();

        return this.httpResponse.getBytes();
    }

    // TODO
    private void constructHttpResponse() {

        try {
            File file = new File("src/resources" + this.httpRequest.getRequestUrl());

            for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
                this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }

            this.httpResponse.setStatusCode(200);

            this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));

        } catch (IOException e) {
            File file = new File("src/resources/not-found.html");
            try {
                this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));
                this.httpResponse.setStatusCode(404);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

}

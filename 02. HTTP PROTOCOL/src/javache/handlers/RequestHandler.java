package javache.handlers;

import javache.constants.WebConstants;
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

            this.httpResponse.setStatusCode(WebConstants.OK);

            this.addMimeType();

            this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));

        } catch (IOException e) {
            this.httpResponse = new HttpResponseImpl();
            File file = new File("src/resources/not-found.html");
            for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
                this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
            try {
                this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));
                this.httpResponse.setStatusCode(WebConstants.NOT_FOUND);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void addMimeType() {
        String type = this.httpRequest.getRequestUrl().substring(this.httpRequest.getRequestUrl().indexOf(".") + 1);
        if (type.equals("html")) {
            this.httpResponse.addHeader("Content-Type", "multipart");
        } else if (type.equals("jpg") || type.equals("jpeg") || type.equals("png")) {
            this.httpResponse.addHeader("Content-Type", "image/png");
        }
    }
}

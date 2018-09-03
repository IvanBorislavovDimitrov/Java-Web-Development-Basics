package javache.handlers;

import javache.constants.WebConstants;
import javache.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class RequestHandler {

    private static final String HTML_EXTENSION = ".html";
    private static final String PAGES_FOLDER = "src/resources/pages/";
    private static final String ASSETS_FOLDER = "src/resources/assets/";

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSessionStorage httpSessionStorage;

    public RequestHandler(HttpSessionStorage httpSessionStorage) {
        this.httpSessionStorage = httpSessionStorage;
    }

    public byte[] handleRequest(String requestContent) throws Exception {

        this.httpRequest = new HttpRequestImpl(requestContent);
        this.httpResponse = new HttpResponseImpl();

        if (this.httpRequest.getMethod().equals("GET")) {
            this.processGetRequest();
        } else if (this.httpRequest.getMethod().equals("POST")) {
            this.processPostRequest();
        }

        this.httpResponse.addHeader("Content-Disposition", "inline");

        this.httpSessionStorage.refreshSessions();

        return this.httpResponse.getBytes();
    }

    private void processPostRequest() {
        String url = this.httpRequest.getRequestUrl();
        url = this.parseUrl(url);

        try {
            File file = new File(url);
            for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
                if (!stringStringEntry.getKey().equalsIgnoreCase("Content-length")) {
                    this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }

            this.httpResponse.setStatusCode(WebConstants.REDIRECT);

            this.addMimeType(url);

            Map<String, String> bodyParameters = this.httpRequest.getBodyParameters();

            bodyParameters.forEach((key, value) -> {
                this.httpResponse.addCookie(new HttpCookieImpl(key, value));
            });

            this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));

        } catch (IOException e) {
            this.notFound();
        }
    }

    private void notFound() {
        this.httpResponse = new HttpResponseImpl();
        String url = "not-found";
        File file = new File(PAGES_FOLDER + url + HTML_EXTENSION);
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

    // TODO
    private void processGetRequest() {
        String url = this.httpRequest.getRequestUrl();
        url = this.parseUrl(url);
        try {
            File file = new File(url);
            for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
                if (!stringStringEntry.getKey().equalsIgnoreCase("Content-length")) {
                    this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }

            this.httpResponse.setStatusCode(WebConstants.OK);

            this.addMimeType(url);

            this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));

        } catch (IOException e) {
            this.notFound();
        }
    }

    private void addMimeType(String url) {
        String type = url.substring(url.indexOf('.') + 1);
        if (type.equals("html")) {
            this.httpResponse.addHeader("Content-Type", "text/html");
        } else if (type.equals("jpg") || type.equals("jpeg") || type.equals("png")) {
            this.httpResponse.addHeader("Content-Type", "image/png");
        }
    }

    private String parseUrl(String url) {
        switch (url) {
            case "/":
                return PAGES_FOLDER + "index" + HTML_EXTENSION;
            case "/login":
                return PAGES_FOLDER + "login" + HTML_EXTENSION;
            case "/register":
                return PAGES_FOLDER + "register" + HTML_EXTENSION;
            default:
                return ASSETS_FOLDER + url;
        }
    }
}

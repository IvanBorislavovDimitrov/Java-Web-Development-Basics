package javache.handlers;

import javache.constants.WebConstants;
import javache.http.*;
import javache.models.User;
import javache.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class RequestHandler {

    private static final String HTML_EXTENSION = ".html";
    private static final String PAGES_FOLDER = "src/main/resources/pages/";
    private static final String ASSETS_FOLDER = "src/main/resources/assets/";
    private static final String REGISTER_URL = "http://localhost:8007/register";
    private static final String LOGIN_URL = "http://localhost:8007/login";

    private HttpRequest httpRequest;
    private HttpResponse httpResponse;
    private HttpSessionStorage httpSessionStorage;
    private UserRepository userRepository;

    private static User loggedUser;

    public RequestHandler(HttpSessionStorage httpSessionStorage, UserRepository userRepository) {
        this.httpSessionStorage = httpSessionStorage;
        this.userRepository = userRepository;
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
        final String to = this.httpRequest.getRequestUrl();
        String url = this.httpRequest.getRequestUrl();
        url = this.parseUrl(url);

        String referer = this.httpRequest.getHeaders().get("Referer");
        if (referer.equalsIgnoreCase(REGISTER_URL)) {
            this.registerUser(to);
        } else if (referer.equalsIgnoreCase(LOGIN_URL)) {
            this.login();
        }

        try {
            File file = new File(url);

            this.addContent(url, WebConstants.REDIRECT);

            this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));

            if (loggedUser != null) {
                HttpCookie httpCookie = new HttpCookieImpl(loggedUser.getEmail(), loggedUser.getPassword());
                this.httpRequest.getCookies().putIfAbsent(httpCookie.getName(), httpCookie);
            }
        } catch (IOException e) {
            this.notFound();
        }
    }

    private void login() {
        Map<String, String> bodyParameters = this.httpRequest.getBodyParameters();
        AtomicReference<String> email = new AtomicReference<>();
        AtomicReference<String> password = new AtomicReference<>();
        bodyParameters.forEach((key, value) -> {
            if (key.equals("email")) {
                email.set(value);
            } else if (key.equals("password")) {
                password.set(value);
            }
        });

        loggedUser = this.userRepository.getAll().stream()
                .filter(x -> x.getEmail().equals(email.get()) && x.getPassword().equals(password.get()))
                .findFirst()
                .orElse(null);
        if (loggedUser != null) {
            HttpCookie httpCookie = new HttpCookieImpl(loggedUser.getEmail(), loggedUser.getPassword());
            this.httpResponse.addCookie(httpCookie);
        }
    }

    private void registerUser(String to) {
        User user = new User();
        boolean isEmailSet = false;
        boolean isPasswordSet = false;
        boolean isConfirmPasswordSet = false;
        String password = null;
        String confirmPassword = null;
        for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getBodyParameters().entrySet()) {
            if (stringStringEntry.getKey().equalsIgnoreCase("email")) {
                user.setEmail(stringStringEntry.getValue());
                isEmailSet = true;
            } else if (stringStringEntry.getKey().equalsIgnoreCase("password")) {
                user.setPassword(stringStringEntry.getValue());
                isPasswordSet = true;
                password = stringStringEntry.getValue();
            } else if (stringStringEntry.getKey().equalsIgnoreCase("confirmPassword")) {
                isConfirmPasswordSet = true;
                confirmPassword = stringStringEntry.getValue();
            }
        }
        boolean registered = false;
        if (isEmailSet && isPasswordSet) {
            if (password.equals(confirmPassword)) {
                this.userRepository.save(user);
                registered = true;
            }
        }

        if (!registered) {

        }

        if (to.equals("/")) {
            HttpSession httpSession = new HttpSessionImpl();
            httpSession.addAttribute("loggedUser", user);
            this.httpSessionStorage.addSession(httpSession);
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

        if (url.equals(PAGES_FOLDER + "logout" + HTML_EXTENSION)) {
            loggedUser = null;
        }

        try {
            File file = new File(url);
            if (url.equals(PAGES_FOLDER + "profile" + HTML_EXTENSION)) {
                if (loggedUser == null) {
                    this.sendToMustBeLogged();
                    return;
                }
                this.login(file);
            } else if (url.equals(PAGES_FOLDER + "home" + HTML_EXTENSION)) {
                this.loadHomePage(file);
            } else {
                this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));
            }

            for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
                if (!stringStringEntry.getKey().equalsIgnoreCase("Content-length")) {
                    this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
            }

            addContent(url, WebConstants.OK);

            if (loggedUser != null) {
                HttpCookie httpCookie = new HttpCookieImpl(loggedUser.getEmail(), loggedUser.getPassword());
                this.httpRequest.getCookies().putIfAbsent(httpCookie.getName(), httpCookie);
            }

        } catch (IOException e) {
            this.notFound();
        }
    }

    private void loadHomePage(File file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line);
            sb.append(System.lineSeparator());
        }
        Collection<User> all = this.userRepository.getAll();
        String users = all.stream()
                .filter(u -> !u.getId().equals(loggedUser.getId()))
                .map(User::getEmail)
                .collect(Collectors.joining("<br>"));

        String fileContent = sb.toString().replace("${people}", users);

        this.httpResponse.setContent(fileContent.getBytes());
    }

    private void addContent(String url, int ok) {
        for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
            if (!stringStringEntry.getKey().equalsIgnoreCase("Content-length")) {
                this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }

        this.httpResponse.setStatusCode(ok);

        this.addMimeType(url);
    }

    private void login(File file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file.getPath()));
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            sb.append(line).append(System.lineSeparator());
        }
        sb.trimToSize();
        sb = new StringBuilder(sb.toString().replace("${username}", loggedUser.getEmail()));
        sb = new StringBuilder(sb.toString().replace("${password}", loggedUser.getPassword()));

        this.httpResponse.setContent(sb.toString().getBytes());
    }

    private void sendToMustBeLogged() throws IOException {
        File file;
        file = new File(PAGES_FOLDER + "must-be-logged" + HTML_EXTENSION);
        for (Map.Entry<String, String> stringStringEntry : this.httpRequest.getHeaders().entrySet()) {
            if (!stringStringEntry.getKey().equalsIgnoreCase("Content-length")) {
                this.httpResponse.addHeader(stringStringEntry.getKey(), stringStringEntry.getValue());
            }
        }

        this.httpResponse.setStatusCode(WebConstants.OK);

        this.httpResponse.setContent(Files.readAllBytes(Paths.get(file.getPath())));
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
            case "/home":
                return PAGES_FOLDER + "home" + HTML_EXTENSION;
            case "/logout":
                return PAGES_FOLDER + "logout" + HTML_EXTENSION;
            case "/profile":
                return PAGES_FOLDER + "profile" + HTML_EXTENSION;
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

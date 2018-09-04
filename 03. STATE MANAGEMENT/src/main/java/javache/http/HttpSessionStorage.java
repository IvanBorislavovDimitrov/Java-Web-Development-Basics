package javache.http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpSessionStorage {

    private Map<String, HttpSession> sessions;

    public HttpSessionStorage() {
        this.sessions = new HashMap<>();
    }

    public HttpSession getById(String id) {
        return this.sessions.get(id);
    }

    public void addSession(HttpSession httpSession) {
        this.sessions.put(httpSession.getId(), httpSession);
    }

    public void refreshSessions() {
        this.sessions = this.sessions.entrySet().stream()
                .filter(s -> s.getValue().isValid())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}

package javache.http;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpSessionImpl implements HttpSession {

    private String id;
    private boolean isValid;
    private Map<String, Object> attributes;

    public HttpSessionImpl() {
        this.id = UUID.randomUUID().toString();
        this.isValid = true;
        this.attributes = new HashMap<>();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public boolean isValid() {
        return this.isValid;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public void addAttribute(String name, Object attribute) {
        this.attributes.putIfAbsent(name, attribute);
    }

    @Override
    public void invalidate() {
        this.isValid = false;
        this.attributes = null;
    }
}

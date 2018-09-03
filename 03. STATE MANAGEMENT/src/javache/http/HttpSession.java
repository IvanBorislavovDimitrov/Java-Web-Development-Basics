package javache.http;

import java.util.Map;

public interface HttpSession {

    String getId();

    boolean isValid();

    Map<String, Object> getAttributes();

    void addAttribute(String name, Object attribute);

    void invalidate();
}

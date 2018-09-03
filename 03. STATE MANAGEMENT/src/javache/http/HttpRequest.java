package javache.http;

import java.util.Map;

public interface HttpRequest {

    Map<String, HttpCookie> getCookies();

    void addCookie(HttpCookie httpCookie);

    Map<String, String> getHeaders();

    Map<String, String> getBodyParameters();

    String getMethod();

    void setMethod(String method);

    String getRequestUrl();

    void setRequestUrl(String requestUrl);

    void addHeader(String header, String value);

    void addBodyParameter(String parameter, String value);

    boolean isResource();
}

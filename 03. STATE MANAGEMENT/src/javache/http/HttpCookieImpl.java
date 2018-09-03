package javache.http;

public class HttpCookieImpl implements HttpCookie {

    private String name;
    private String value;

    public HttpCookieImpl(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.getName() + "=" + this.getValue();
    }
}

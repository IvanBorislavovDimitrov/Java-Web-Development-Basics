package javache.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestImpl implements HttpRequest {

    private String method;
    private String totalContent;
    private Map<String, String> headers;
    private Map<String, String> bodyParameters;
    private String requestUrl;
    private Map<String, HttpCookie> cookies;

    public HttpRequestImpl(String totalContent) {
        this.totalContent = totalContent;
        this.headers = new HashMap<>();
        this.bodyParameters = new HashMap<>();
        this.cookies = new HashMap<>();
        this.setMethod();
        this.initializeHeaders();
        this.initializeBodyParams();
        this.setUrl();
    }

    @Override
    public Map<String, HttpCookie> getCookies() {
        return cookies;
    }

    @Override
    public void addCookie(HttpCookie httpCookie) {
        this.cookies.put(httpCookie.getName(), httpCookie);
    }

    @Override
    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(this.headers);
    }

    @Override
    public Map<String, String> getBodyParameters() {
        return Collections.unmodifiableMap(this.bodyParameters);
    }

    @Override
    public String getMethod() {
        return this.method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getRequestUrl() {
        return this.requestUrl;
    }

    @Override
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public void addHeader(String header, String value) {
        this.headers.put(header, value);
    }

    @Override
    public void addBodyParameter(String parameter, String value) {
        this.bodyParameters.put(parameter, value);
    }

    @Override
    public boolean isResource() {
        String[] requestUrlParams = this.totalContent.substring(0, this.totalContent.indexOf("\r\n")).split("\\s+");

        return requestUrlParams[1].contains(".");
    }

    private void initializeBodyParams() {
        String[] requestUrlParams = this.totalContent.substring(0, this.totalContent.indexOf("\r\n")).split("\\s+");
        if (requestUrlParams[1].contains("" +
                "?")) {
            String[] tokens = requestUrlParams[1].split("\\?")[1].split("&");

            Arrays.stream(tokens).forEach(t -> {
                String[] values = t.split("=");
                String key = values[0];
                String value = values[1];

                this.addBodyParameter(key, value);
            });
        } else {
            String[] tokens = this.totalContent.split("\r\n\r\n");
            if (tokens.length == 2) {
                String[] params = tokens[1].split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    String key = keyValue[0];
                    String value = keyValue[1];
                    this.bodyParameters.put(key, value);
                }
            }
        }
    }

    private void initializeHeaders() {
        String[] tokens = this.totalContent.split("\r\n");
        Arrays.stream(tokens).filter(x -> !x.trim().equals("")).skip(1).forEach(t -> {
            int indexOfFirstDots = t.indexOf(":");
            if (indexOfFirstDots != -1) {
                String header = t.substring(0, indexOfFirstDots);
                String value = t.substring(indexOfFirstDots + 1).trim();

                this.addHeader(header, value);
            }
        });
    }

    private void setMethod() {
        this.method = this.totalContent.split("\\s+")[0];
    }

    private void setUrl() {
        this.requestUrl = this.totalContent.substring(0, this.totalContent.indexOf("\r\n")).split("\\s+")[1];
    }
}

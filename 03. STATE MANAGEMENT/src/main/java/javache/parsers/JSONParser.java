package javache.parsers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONParser {

    private Gson gson;

    public JSONParser() {
        this.gson = new GsonBuilder()
                .excludeFieldsWithModifiers()
                .setPrettyPrinting()
                .create();
    }

    public <T> T read(Class<T> objectClass, String fileContent) {
        return this.gson.fromJson(fileContent, objectClass);
    }

    public <T> String write(T obj) {
        return this.gson.toJson(obj);
    }
}

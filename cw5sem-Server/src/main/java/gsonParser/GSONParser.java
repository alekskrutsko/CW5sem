package gsonParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GSONParser<ClassType> {

    public GSONParser() {
    }

    public ClassType getObject(String json, Type type) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        ClassType object = gson.fromJson(json, type);
        System.out.println(json);
        return object;
    }

    public String getString(ClassType object) {
        Type type = new TypeToken<ClassType>() {}.getType();
        Gson gson = new Gson();
        String json = gson.toJson(object, type);
        return json;
    }
}

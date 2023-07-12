package com.example.androidlottieapp.extensions;

public class JsonFileExtension extends KFileExtension {

    public static final JsonFileExtension JSON = new JsonFileExtension();

    public JsonFileExtension() {
        super(".json");
    }

    @Override
    public boolean canParseContent(String contentType) {
        return contentType.toLowerCase().contains("application/json");
    }
}

package me.kasuki.staffcredits.utilities.json;

import com.google.gson.JsonObject;

public class JsonBuilder {

    private final JsonObject jsonObject;

    public JsonBuilder() {
        this.jsonObject = new JsonObject();
    }

    public JsonBuilder addProperty(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Character value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, Boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonBuilder addProperty(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonObject toJson() {
        return this.jsonObject;
    }

}

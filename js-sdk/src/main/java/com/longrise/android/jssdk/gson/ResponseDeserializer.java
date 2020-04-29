package com.longrise.android.jssdk.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.longrise.android.jssdk.Response;

import java.lang.reflect.Type;

/**
 * Created by godliness on 2020-04-16.
 *
 * @author godliness
 */
final class ResponseDeserializer implements JsonDeserializer<Response<String>> {

    static Type getType() {
        return new TypeToken<Response<String>>() {
        }.getType();
    }

    static ResponseDeserializer create() {
        return new ResponseDeserializer();
    }

    @Override
    public Response<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();
        final JsonElement versionElement = jsonObject.get("version");
        final int version = versionElement != null ? versionElement.getAsInt() : -1;

        final JsonElement idElement = jsonObject.get("id");
        final int id = idElement != null ? idElement.getAsInt() : -1;

        final String result;
        final JsonElement jsonResult = jsonObject.get("result");
        if (jsonResult == null) {
            result = null;
        } else if (jsonResult.isJsonObject()) {
            result = jsonResult.getAsJsonObject().toString();
        } else if (jsonResult.isJsonArray()) {
            result = jsonResult.getAsJsonArray().toString();
        } else {
            result = jsonResult.getAsString();
        }
        final Response<String> response = new Response<>();
        response.setVersion(version);
        response.setCallbackId(id);
        response.deserialize(result);
        return response;
    }

    private ResponseDeserializer(){

    }
}

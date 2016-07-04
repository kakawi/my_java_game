package game;

import com.google.gson.*;

import java.lang.reflect.Type;

public class JSRequestDeserializer implements JsonDeserializer<JSRequest> {
    @Override
    public JSRequest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JSRequest jsRequest = new JSRequest(jsonObject.get("action").getAsString());
        JsonElement dataElement = jsonObject.get("data");
        if (dataElement != null) {
            jsRequest.setData(dataElement.getAsJsonObject());
        }

        return jsRequest;
    }
}

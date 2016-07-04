package game;

import com.google.gson.JsonObject;

public class JSRequest {
    private String action;
    private JsonObject data;

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public JSRequest(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }
}

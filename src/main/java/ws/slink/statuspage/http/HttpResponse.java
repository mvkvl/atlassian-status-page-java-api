package ws.slink.statuspage.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@ToString
public class HttpResponse {

    private HttpStatus status;
    private String    message;
    private Object       body;

    public String getBodyAsString() {
        if (null == body)
            return "";
        else if (body instanceof String)
            return (String)body;
        else if (body instanceof JsonObject)
            return ((JsonObject)body).getAsString();
        else
            throw new IllegalArgumentException("unsupported body type: " + body.getClass().getSimpleName());
    }
    public JsonObject getBodyAsJson() {
        if (null == body)
            return new JsonObject();
        else if (body instanceof String)
            return new JsonParser().parse((String)body).getAsJsonObject();
        else if (body instanceof JsonObject)
            return (JsonObject)body;
        else
            throw new IllegalArgumentException("unsupported body type: " + body.getClass().getSimpleName());
    }

}

package ws.slink.statuspage.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Data
@Accessors(fluent = true)
@ToString
public class Group {

    private String id;
    private String name;

    @SerializedName("page_id")
    private String pageId;

    private String description;

    @SerializedName("components")
    private List<String> components;

    private List<Component> componentObjects;

    public List<Component> componentObjects() {
        if (null == componentObjects) {
            return Collections.emptyList();
        } else {
            return componentObjects;
        }
    }
}

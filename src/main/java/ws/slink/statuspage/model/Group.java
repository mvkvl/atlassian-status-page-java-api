package ws.slink.statuspage.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

@Data
@Accessors(fluent = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "name"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Group {

    private String id;
    private String name;

    @JsonProperty("page_id")
    private String pageId;

    private String description;

    @JsonProperty("component")
    private List<String> componentIds;

    @JsonIgnore
    private List<Component> components;

    public List<Component> components() {
        if (null == components) {
            return Collections.emptyList();
        } else {
            return components;
        }
    }
}

package ws.slink.statuspage.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(fluent = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "name"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Incident {

    private String id;
    private String name;
    private String status;
    private String impact;
    private String created;
    private String updated;
    private String started;
    private String resolved;

    @JsonProperty("page_id")
    private String pageId;


    @JsonIgnore
    private Page page;

    @JsonIgnore
    private List<Group> groups;

    @JsonIgnore
    private List<Component> components;

    @JsonIgnore
    private List<Incident> incidents;

}

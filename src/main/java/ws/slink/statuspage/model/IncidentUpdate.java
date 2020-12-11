package ws.slink.statuspage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import ws.slink.statuspage.type.IncidentStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@Accessors(fluent = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "name"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class IncidentUpdate {

    private String id;

    private IncidentStatus status;

    private String body;

    private String incident_id;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("affected_components")
    private List<ComponentUpdate> affectedComponents = new ArrayList<>();
    public List<ComponentUpdate> affectedComponents() {
        if (null == affectedComponents)
            return Collections.emptyList();
        else
            return affectedComponents;
    }
}

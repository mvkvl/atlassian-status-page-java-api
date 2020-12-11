package ws.slink.statuspage.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import ws.slink.statuspage.type.IncidentSeverity;
import ws.slink.statuspage.type.IncidentStatus;

import java.time.LocalDateTime;
import java.util.*;

@Data
@Accessors(fluent = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "name"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Incident {

    private String id;
    private String name;

    private IncidentStatus status;

    private IncidentSeverity impact;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("started_at")
    private LocalDateTime startedAt;

    @JsonProperty("resolved_at")
    private LocalDateTime resolvedAt;

    @JsonProperty("monitoring_at")
    private LocalDateTime monitoringAt;

    @JsonProperty("scheduled_for")
    private LocalDateTime scheduledFor;

    @JsonProperty("scheduled_until")
    private LocalDateTime scheduledUntil;

    @JsonProperty("page_id")
    private String pageId;

    @JsonIgnore
    private Page page;

    private Map<String, Object> metadata = new HashMap<>();

//    @JsonIgnore
//    private List<Group> groups;

    private List<Component> components = new ArrayList<>();
    public List<Component> components() {
        if (null == components)
            return Collections.emptyList();
        else
            return components;
    }

    @JsonProperty("incident_updates")
    private List<IncidentUpdate> updates = new ArrayList<>();
    public List<IncidentUpdate> updates() {
        if (null == updates) {
            return Collections.emptyList();
        } else {
            return updates;
        }
    }

}

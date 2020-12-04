package ws.slink.statuspage.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
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

    @JsonIgnore
    private List<Group> groups;

    private List<Component> components;

    @JsonProperty("incident_updates")
    private List<IncidentUpdate> updates;

}

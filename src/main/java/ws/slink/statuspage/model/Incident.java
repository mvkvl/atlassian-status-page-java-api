package ws.slink.statuspage.model;

import com.google.gson.annotations.SerializedName;
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
public class Incident {

    private String id;
    private String name;

    private IncidentStatus status;

    private IncidentSeverity impact;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    @SerializedName("started_at")
    private LocalDateTime startedAt;

    @SerializedName("resolved_at")
    private LocalDateTime resolvedAt;

    @SerializedName("monitoring_at")
    private LocalDateTime monitoringAt;

    @SerializedName("scheduled_for")
    private LocalDateTime scheduledFor;

    @SerializedName("scheduled_until")
    private LocalDateTime scheduledUntil;

    @SerializedName("page_id")
    private String pageId;

    private Page page;

    private Map<String, Object> metadata = new HashMap<>();

    private List<Component> components = new ArrayList<>();
    public List<Component> components() {
        if (null == components)
            return Collections.emptyList();
        else
            return components;
    }

    @SerializedName("incident_updates")
    private List<IncidentUpdate> updates = new ArrayList<>();
    public List<IncidentUpdate> updates() {
        if (null == updates) {
            return Collections.emptyList();
        } else {
            return updates;
        }
    }

}

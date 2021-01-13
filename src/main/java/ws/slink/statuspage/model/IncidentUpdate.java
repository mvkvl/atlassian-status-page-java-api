package ws.slink.statuspage.model;

import com.google.gson.annotations.SerializedName;
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
public class IncidentUpdate {

    private String id;

    private IncidentStatus status;

    private String body;

    private String incident_id;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    @SerializedName("affected_components")
    private List<ComponentUpdate> affectedComponents = new ArrayList<>();
    public List<ComponentUpdate> affectedComponents() {
        if (null == affectedComponents)
            return Collections.emptyList();
        else
            return affectedComponents;
    }
}

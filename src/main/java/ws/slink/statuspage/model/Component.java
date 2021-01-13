package ws.slink.statuspage.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import ws.slink.statuspage.type.ComponentStatus;

@Data
@Accessors(fluent = true)
@ToString
public class Component {

    private String id;

    private String name;

    @SerializedName("page_id")
    private String pageId;

    @SerializedName("group_id")
    private String groupId;

    private String description;

    @SerializedName("only_show_if_degraded")
    private boolean onlyShowIfDegraded;

    private boolean showcase;

    private ComponentStatus status;

    private boolean group;

}

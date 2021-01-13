package ws.slink.statuspage.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@ToString
public class ComponentUpdate {

    @SerializedName("code")
    private String id;

    private String name;

    @SerializedName("old_status")
    private String statusOld;

    @SerializedName("new_status")
    private String statusNew;

}

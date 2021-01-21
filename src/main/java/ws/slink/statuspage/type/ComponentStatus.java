package ws.slink.statuspage.type;

import com.google.gson.annotations.SerializedName;

public enum ComponentStatus {

     @SerializedName("operational")
     OPERATIONAL("operational", 1),

     @SerializedName("under_maintenance")
     MAINTENANCE("under_maintenance", 2),

     @SerializedName("degraded_performance")
     DEGRADED("degraded_performance", 3),

     @SerializedName("partial_outage")
     PARTIAL_OUTAGE("partial_outage", 4),

     @SerializedName("major_outage")
     MAJOR_OUTAGE("major_outage", 5),

     @SerializedName("")
     NONE("", 0)
    ;

    private String value;
    private int id;
    ComponentStatus(String value, int id) {
        this.value = value;
        this.id = id;
    }
    public String value() {
        return this.value;
    }
    public int id() {
        return this.id;
    }
    public static ComponentStatus of(String input){
        for(ComponentStatus v : values()){
            if( v.value().equalsIgnoreCase(input) ){
                return v;
            }
        }
        return null;
    }

}

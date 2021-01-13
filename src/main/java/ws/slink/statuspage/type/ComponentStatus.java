package ws.slink.statuspage.type;

import com.google.gson.annotations.SerializedName;

public enum ComponentStatus {

     @SerializedName("operational")
     OPERATIONAL("operational"),

     @SerializedName("under_maintenance")
     MAINTENANCE("under_maintenance"),

     @SerializedName("degraded_performance")
     DEGRADED("degraded_performance"),

     @SerializedName("partial_outage")
     PARTIAL_OUTAGE("partial_outage"),

     @SerializedName("major_outage")
     MAJOR_OUTAGE("major_outage"),

     @SerializedName("")
     NONE("")
    ;

    private String value;
    ComponentStatus(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
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

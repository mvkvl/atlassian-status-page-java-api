package ws.slink.statuspage.type;

import com.google.gson.annotations.SerializedName;

public enum IncidentSeverity {

     @SerializedName("none")
     NONE("none", 0),

     @SerializedName("minor")
     MINOR("minor", 1),

     @SerializedName("major")
     MAJOR("major", 2),

     @SerializedName("critical")
     CRITICAL("critical", 3),

     @SerializedName("maintenance")
     MAINTENANCE("maintenance", 4)
    ;

    private String value;
    private int id;
    IncidentSeverity(String value, int id) {
        this.value = value;
        this.id = id;
    }
    public String value() {
        return this.value;
    }
    public int id() {
        return this.id;
    }
    public static IncidentSeverity of(String input){
        for(IncidentSeverity v : values()){
            if( v.value().equalsIgnoreCase(input) ){
                return v;
            }
        }
        return null;
    }

}

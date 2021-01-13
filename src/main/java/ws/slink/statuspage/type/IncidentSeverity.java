package ws.slink.statuspage.type;

import com.google.gson.annotations.SerializedName;

public enum IncidentSeverity {

     @SerializedName("none")
     NONE("none"),

     @SerializedName("minor")
     MINOR("minor"),

     @SerializedName("major")
     MAJOR("major"),

     @SerializedName("critical")
     CRITICAL("critical"),

     @SerializedName("maintenance")
     MAINTENANCE("maintenance")
    ;

    private String value;
    IncidentSeverity(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
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

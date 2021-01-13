package ws.slink.statuspage.type;

import com.google.gson.annotations.SerializedName;

public enum IncidentStatus {

    @SerializedName("investigating")
    INVESTIGATING("investigating"),

    @SerializedName("identified")
    IDENTIFIED("identified"),

    @SerializedName("monitoring")
    MONITORING("monitoring"),

    @SerializedName("resolved")
    RESOLVED("resolved"),

    @SerializedName("scheduled")
    SCHEDULED("scheduled"),

    @SerializedName("in_progress")
    IN_PROGRESS("in_progress"),

    @SerializedName("verifying")
    VERIFYING("verifying"),

    @SerializedName("completed")
    COMPLETED("completed"),
    ;

    private String value;
    IncidentStatus(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
    public static IncidentStatus of(String input){
        for(IncidentStatus v : values()){
            if( v.value().equalsIgnoreCase(input) ){
                return v;
            }
        }
        return null;
    }

}

package ws.slink.statuspage.type;

import com.google.gson.annotations.SerializedName;

public enum IncidentStatus {

    @SerializedName("investigating")
    INVESTIGATING("investigating", 1),

    @SerializedName("identified")
    IDENTIFIED("identified", 2),

    @SerializedName("monitoring")
    MONITORING("monitoring", 3),

    @SerializedName("resolved")
    RESOLVED("resolved", 4),

    @SerializedName("scheduled")
    SCHEDULED("scheduled", 5),

    @SerializedName("in_progress")
    IN_PROGRESS("in_progress", 6),

    @SerializedName("verifying")
    VERIFYING("verifying", 7),

    @SerializedName("completed")
    COMPLETED("completed", 8),
    ;

    private String value;
    private int id;
    IncidentStatus(String value, int id) {
        this.value = value;
        this.id = id;
    }
    public String value() {
        return this.value;
    }
    public int id() {
        return this.id;
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

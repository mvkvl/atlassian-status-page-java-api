package ws.slink.statuspage.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ws.slink.statuspage.type.converter.IncidentStatusDeserializer;

@JsonDeserialize(using = IncidentStatusDeserializer.class)
public enum IncidentStatus {
    INVESTIGATING("investigating"),
    IDENTIFIED("identified"),
    MONITORING("monitoring"),
    RESOLVED("resolved")
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

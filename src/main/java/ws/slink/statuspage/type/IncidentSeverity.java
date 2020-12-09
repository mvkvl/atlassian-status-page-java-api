package ws.slink.statuspage.type;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ws.slink.statuspage.type.converter.IncidentSeverityDeserializer;

@JsonDeserialize(using = IncidentSeverityDeserializer.class)
public enum IncidentSeverity {
     NONE("none")
    ,MINOR("minor")
    ,MAJOR("major")
    ,CRITICAL("critical")
    ,MAINTENANCE("maintenance")
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

package ws.slink.statuspage.type.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ws.slink.statuspage.type.IncidentSeverity;
import ws.slink.statuspage.type.IncidentStatus;

import java.io.IOException;

public class IncidentSeverityDeserializer extends StdDeserializer {

    public IncidentSeverityDeserializer() {
        super(IncidentStatus.class);
    }
    public IncidentSeverityDeserializer(Class t) {
        super(t);
    }

    @Override
    public Object deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return IncidentSeverity.of(node.asText());
    }

}

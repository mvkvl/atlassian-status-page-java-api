package ws.slink.statuspage.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(fluent = true)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "name"})
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class Page {

    private String id;
    private String name;

    @JsonIgnore
    private List<Group> groups = Collections.emptyList();
    @JsonIgnore
    private List<Component> components = Collections.emptyList();;
    @JsonIgnore
    private List<Incident> incidents;

    public List<Component> allComponents() {
        return Stream.concat(components.stream(), groups.stream().flatMap(v -> v.components().stream())).collect(Collectors.toList());
    }
}

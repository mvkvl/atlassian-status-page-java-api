package ws.slink.statuspage.model;

import com.google.gson.internal.LinkedTreeMap;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Accessors(fluent = true)
@ToString
public class Page {

    private String id;
    private String name;

    private List<Group> groups = Collections.emptyList();
    private List<Component> components = Collections.emptyList();;
    private List<Incident> incidents;
    public List<Component> allComponents() {
        return Stream.concat(this.components.stream(), groups.stream().flatMap(v -> v.componentObjects().stream()))
            .filter(c -> !c.group())
            .sorted(Comparator.comparing(Component::name))
            .collect(Collectors.toList())
        ;
    }

    public static Page of(LinkedTreeMap<String, Object> map) {
        return new Page()
            .id(map.get("id").toString())
            .name(map.get("name").toString())
        ;
    }
}

package tmp;

import org.junit.ClassRule;
import org.junit.Test;
import ws.slink.statuspage.model.Page;
import ws.slink.test.statuspage.config.StatusPageTestResource;

import java.util.stream.Collectors;

public class TestSync {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void testSyncA() {
        resource.run(() -> {
            resource.statusPage().sync().stream().forEach(page -> {
                System.out.println("page " + page.name() + " [" + page.id() + "]");
                page.groups().stream().forEach(group -> {
                    System.out.println(" > group: " + group.name() + " [" + group.id() + "]");
                    group.components().stream().forEach(component -> System.out.println("  ~ group component: " + component.name() + " [" + component.id() + "]"));
                });
                page.components().stream().forEach(component -> System.out.println(" - component: " + component.name() + " [" + component.id() + "]"));
                page.incidents().stream().forEach(incident -> {
                    System.out.println(" = incident : " + incident.name() + " [" + incident.status().value() + ", " + incident.impact().value() + ", " + incident.id() + "]");
                    incident.components().stream().forEach(ic -> {
                        System.out.println("   -> affected component: " + ic.name() + "[" + ic.status() + ", " + ic.id() + "]");
                    });
                    incident.updates().stream().forEach(iu -> {
                        System.out.println(
                                "   => incident update: " +
                                        iu.status().value() +
                                        ", " +
                                        iu.createdAt() +
                                        ": " +
                                        iu.affectedComponents()
                                                .stream()
                                                .map(iuac -> "[" + iuac.name() + " # " + iuac.id() + ": " + iuac.statusOld() + " -> " + iuac.statusNew() + "]")
                                                .collect(Collectors.joining(", "))
                        );
                    });
                });
            });
        });
    }

    @Test public void testSyncB() {
        resource.run(() -> {
            Page page = resource.statusPage().pages().get(0);
            System.out.println(page);
            System.out.println(resource.statusPage().sync(page));
            resource.statusPage().sync(page.id()).ifPresent(System.out::println);
            resource.statusPage().sync("stub").ifPresent(System.out::println);
        });
    }

}

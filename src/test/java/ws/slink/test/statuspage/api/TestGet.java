package ws.slink.test.statuspage.api;

import org.junit.ClassRule;
import org.junit.Test;
import ws.slink.statuspage.model.Component;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Incident;
import ws.slink.statuspage.model.Page;
import ws.slink.test.statuspage.StatusPageTestResource;

public class TestGet {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void testGetPage() {
        resource.run(() -> {
            Page page = resource.getStatusPage().pages().get(0);
            resource.getStatusPage().getPage(page.id()).ifPresent(System.out::println);
            resource.getStatusPage().getPage(page.id(), true).ifPresent(System.out::println);
            resource.getStatusPage().getPage("stub", true).ifPresent(System.out::println);
            System.out.println(page.allComponents());
            resource.getStatusPage().getPage(page.id(), true).ifPresent(p -> System.out.println(p.allComponents()));
        });
    }

    @Test public void testGetGroup() {
        resource.run(() -> {
            Page page = resource.getStatusPage().getPage(resource.getStatusPage().pages().get(0).id(), true).get();
            System.out.println("test page : " + page);

            Group group = page.groups().get(0);
            System.out.println("test group: " + group);

            resource.getStatusPage().getGroup(page.id(), group.id()).ifPresent(System.out::println);
            resource.getStatusPage().getGroup(page.id(), group.id(), true).ifPresent(System.out::println);
            resource.getStatusPage().getGroup(page.id(), "stub", true).ifPresent(System.out::println);
        });
    }

    @Test public void testGetComponent() {
        resource.run(() -> {
            Page page = resource.getStatusPage().getPage(resource.getStatusPage().pages().get(0).id(), true).get();
            System.out.println("test page     : " + page);

            Component component = resource.getStatusPage().components(page.id()).get(0);
            System.out.println("test component: " + component);

            resource.getStatusPage().getComponent(page.id(), component.id()).ifPresent(System.out::println);
            resource.getStatusPage().getComponent(page.id(), component.id(), true).ifPresent(System.out::println);
            resource.getStatusPage().getComponent(page.id(), "stub", true).ifPresent(System.out::println);
        });
    }

    @Test public void testGetIncident() {
        resource.run(() -> {
            Page page = resource.getStatusPage().getPage(resource.getStatusPage().pages().get(0).id(), true).get();
            System.out.println("test page    : " + page);

            Incident incident = resource.getStatusPage().incidents(page.id()).get(0);
            System.out.println("test incident: " + incident);

            resource.getStatusPage().getIncident(page.id(), incident.id()).ifPresent(System.out::println);
            resource.getStatusPage().getIncident(page.id(), incident.id(), true).ifPresent(System.out::println);
            resource.getStatusPage().getIncident(page.id(), "stub", true).ifPresent(System.out::println);
        });
    }

}

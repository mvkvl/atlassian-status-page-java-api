package ws.slink.test.statuspage.api;

import org.junit.ClassRule;
import org.junit.Test;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Page;
import ws.slink.test.statuspage.StatusPageTestResource;

public class TestLists {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void testPagesList() {
        resource.run(() ->
            resource.getStatusPage().pages().stream().forEach(System.out::println)
        );
    }

    @Test public void testGroupsList() {
        resource.run(() -> {
            Page page = resource.getStatusPage().pages().get(0);
            System.out.println("--- GROUPS -------------------------------------------");
            System.out.println(resource.getStatusPage().groups(page));
            System.out.println(resource.getStatusPage().groups(page.id()));
        });
    }

    @Test public void testGroupComponentsList() {
        resource.run(() -> {
            Page page = resource.getStatusPage().pages().get(0);
            Group group = resource.getStatusPage().groups(page).get(1);
            System.out.println("--- GROUP COMPONENTS ---------------------------------");
            System.out.println(resource.getStatusPage().groupComponents(group));
        });
    }

    @Test public void testComponentsList() {
        resource.run(() -> {
            Page page = resource.getStatusPage().pages().get(0);
            System.out.println("--- COMPONENTS ---------------------------------------");
            System.out.println(resource.getStatusPage().components(page));
            System.out.println(resource.getStatusPage().components(page.id()));
            System.out.println(resource.getStatusPage().components(page, 1, 1));
            System.out.println(resource.getStatusPage().components(page, 1, 2));
            System.out.println(resource.getStatusPage().components(page, 1, 3));
        });
    }

    @Test public void testIncidentsList() {
        resource.run(() -> {
            Page page = resource.getStatusPage().pages().get(0);
            System.out.println("--- INCIDENTS ---------------------------------------");
            System.out.println(resource.getStatusPage().incidents(page));
            System.out.println(resource.getStatusPage().incidents(page.id()));
            System.out.println(resource.getStatusPage().incidents(page, 1, 1));
            System.out.println(resource.getStatusPage().incidents(page, 1, 2));
            System.out.println(resource.getStatusPage().incidents(page, 1, 3));
        });
    }

}

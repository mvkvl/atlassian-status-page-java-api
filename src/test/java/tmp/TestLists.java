package tmp;

import org.junit.ClassRule;
import org.junit.Test;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Page;
import ws.slink.test.statuspage.config.StatusPageTestResource;

public class TestLists {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();


    @Test public void testGroupComponentsList() {
        resource.run(() -> {
            Page page = resource.statusPage().pages().get(0);
            Group group = resource.statusPage().groups(page).get(1);
            System.out.println("--- GROUP COMPONENTS ---------------------------------");
            System.out.println(resource.statusPage().groupComponents(group));
        });
    }

    @Test public void testComponentsList() {
        resource.run(() -> {
            Page page = resource.statusPage().pages().get(0);
            System.out.println("--- COMPONENTS ---------------------------------------");
            System.out.println(resource.statusPage().components(page));
            System.out.println(resource.statusPage().components(page.id()));
            System.out.println(resource.statusPage().components(page, 1, 1));
            System.out.println(resource.statusPage().components(page, 1, 2));
            System.out.println(resource.statusPage().components(page, 1, 3));
        });
    }


}

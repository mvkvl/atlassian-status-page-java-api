package ws.slink.test.statuspage.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ws.slink.statuspage.model.*;
import ws.slink.statuspage.type.IncidentSeverity;
import ws.slink.statuspage.type.IncidentStatus;
import ws.slink.test.statuspage.config.StatusPageTestResource;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
@FixMethodOrder(MethodSorters.JVM)
public class IncidentTest {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void testCreateIncident() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        resource.statusPage().createIncident(
            page.id(),
            resource.getIncidentTitle(),
            "something's happened... dealing with it",
            IncidentSeverity.MAJOR
        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));
        page = resource.statusPage().sync(page);
        assertTrue(page.incidents().stream().filter(i -> i.name().endsWith(resource.getIncidentTitle())).findAny().isPresent());
    }

    @Test public void testListIncidents() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        List<Incident> incidents = resource.statusPage().incidents(page);
        assertEquals(1, incidents.size());
        assertTrue(incidents.stream().filter(i -> i.name().endsWith(resource.getIncidentTitle())).findAny().isPresent());
    }

    @Test public void testGetIncident() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        assertEquals(1, page.incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(incident.isPresent());
        assertEquals(resource.getIncidentTitle(), incident.get().name());
        assertEquals(IncidentSeverity.MAJOR, incident.get().impact());
        assertEquals(IncidentStatus.INVESTIGATING, incident.get().status());
    }

    @Test public void testUpdateIncident() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        assertEquals(1, page.incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(incident.isPresent());

        incident.get().impact(IncidentSeverity.MINOR);
        incident.get().status(IncidentStatus.MONITORING);
        Optional<Incident> updated = resource.statusPage().updateIncident(incident.get());
        assertTrue(updated.isPresent());
        assertEquals(IncidentSeverity.MINOR, incident.get().impact());
        assertEquals(IncidentStatus.MONITORING, incident.get().status());

        page = resource.statusPage().sync(page);
        Optional<Incident> reloaded = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(reloaded.isPresent());
        assertEquals(resource.getIncidentTitle(), incident.get().name());
        assertEquals(IncidentSeverity.MINOR, reloaded.get().impact());
        assertEquals(IncidentStatus.MONITORING, reloaded.get().status());
    }

    @Test public void testDeleteIncident() {
//        resource.getStatusPage().pages().get(0).id();
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        assertEquals(1, page.incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(incident.isPresent());

        Optional<Incident> removed = resource.statusPage().deleteIncident(page.id(), incident.get().id());
        assertTrue(removed.isPresent());

        page = resource.statusPage().sync(page);
        assertEquals(0, page.incidents().size());
    }

}

/*
//    @Test
    public void testCreateIncidentA() {
        resource.run(() -> {
            Page page = resource.getStatusPage().getPage(resource.getStatusPage().pages().get(0).id(), true).get();
            page = resource.getStatusPage().sync(page);

            System.out.println(page);
//            Incident incident = resource.getStatusPage().incidents(page.id()).get(0);

            Incident incident = new Incident()
                    .name("test incident")
                    .impact(IncidentSeverity.MAJOR)
                    .status(IncidentStatus.INVESTIGATING);

            page.groups()
                    .stream()
                    .flatMap(v -> v.components().stream())
                    .filter(v -> !v.group())
                    .forEach(v -> incident.components().add(v));
            incident.components().stream().forEach(c -> c.status(resource.randomComponentStatus()));
            System.out.println(incident);

            resource.getStatusPage().createIncident(page.id(), incident, "something's happened... dealing with it").ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));

//            resource.getStatusPage().getIncident(page.id(), incident.id()).ifPresent(System.out::println);
//            resource.getStatusPage().getIncident(page.id(), incident.id(), true).ifPresent(System.out::println);
//            resource.getStatusPage().getIncident(page.id(), "stub", true).ifPresent(System.out::println);
        });
    }
*/

//    @Test public void testCreateIncidentSimpleA() {
//        Page page = resource.getStatusPage().getPage(resource.getStatusPage().pages().get(0).id(), true).get();
//        resource.getStatusPage().createIncident(
//                page.id(),
//                resource.getIncidentTitle(),
//                "something's happened... dealing with it",
//                IncidentSeverity.MAJOR
//        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));
//        page = resource.getStatusPage().sync(page);
//
//        assertTrue(page.incidents().stream().filter(i -> i.name().endsWith(resource.getIncidentTitle())).findAny().isPresent());
//    }


    /*
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
    */


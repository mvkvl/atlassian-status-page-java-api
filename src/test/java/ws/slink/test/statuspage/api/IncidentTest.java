package ws.slink.test.statuspage.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ws.slink.statuspage.model.*;
import ws.slink.statuspage.type.ComponentStatus;
import ws.slink.statuspage.type.IncidentSeverity;
import ws.slink.statuspage.type.IncidentStatus;
import ws.slink.test.statuspage.config.StatusPageTestResource;
import ws.slink.test.statuspage.config.TestConstants;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ws.slink.test.statuspage.tools.AssertTools.assertEmpty;
import static ws.slink.test.statuspage.tools.AssertTools.assertNonEmpty;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IncidentTest {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void A_testCreateIncident() {
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

    @Test public void B_testListIncidents() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        List<Incident> incidents = resource.statusPage().incidents(page);
        assertEquals(1, incidents.size());
        assertTrue(incidents.stream().filter(i -> i.name().endsWith(resource.getIncidentTitle())).findAny().isPresent());
    }

    @Test public void C_testGetIncident() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
        assertEquals(1, page.incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(incident.isPresent());
        assertEquals(resource.getIncidentTitle(), incident.get().name());
        assertEquals(IncidentSeverity.MAJOR, incident.get().impact());
        assertEquals(IncidentStatus.INVESTIGATING, incident.get().status());
    }

    @Test public void D_testUpdateIncident() {
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

    @Test public void E_testDeleteIncident() {
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

//    @Ignore
    @Test public void F_testUpdateIncidentWithComponents() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();

        Optional<Component> componentA = resource.statusPage().createComponent(
                page.id(),
                TestConstants.TEST_COMPONENT_A_TITLE,
                TestConstants.TEST_COMPONENT_A_DESCRIPTION,
                true
        );
        assertNonEmpty(componentA);
        componentA.get().status(ComponentStatus.DEGRADED);

        Optional<Component> componentB = resource.statusPage().createComponent(
                page.id(),
                TestConstants.TEST_COMPONENT_B_TITLE,
                TestConstants.TEST_COMPONENT_B_DESCRIPTION,
                true
        );
        assertNonEmpty(componentB);
        componentB.get().status(ComponentStatus.MAJOR_OUTAGE);

        Map<String, Object> jira = new HashMap<>();
        jira.put("issue_id", "<TEST ID>");
        Map<String, Object> meta = new HashMap<>();
        meta.put("jira", jira);

        resource.statusPage().createIncident(
            page.id(),
            resource.getIncidentTitle(),
            "something's happened... dealing with it",
            IncidentSeverity.MAJOR,
            meta,
            Arrays.asList(componentA.get(), componentB.get())
        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));

        page = resource.statusPage().sync(page);
        Optional<Incident> incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(incident.isPresent());

//        System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WAITING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        incident.get().impact(IncidentSeverity.MINOR);
        incident.get().status(IncidentStatus.MONITORING);
        incident.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status(ComponentStatus.OPERATIONAL);
        incident.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status(ComponentStatus.MAINTENANCE);
        Optional<Incident> updated = resource.statusPage().updateIncident(incident.get());
        assertTrue(updated.isPresent());
        assertEquals(IncidentSeverity.MINOR, incident.get().impact());
        assertEquals(IncidentStatus.MONITORING, incident.get().status());

        page = resource.statusPage().sync(page);
        Optional<Incident> reloaded = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());

        assertTrue(reloaded.isPresent());
        assertEquals(resource.getIncidentTitle(), reloaded.get().name());
        assertEquals(IncidentSeverity.MINOR, reloaded.get().impact());
        assertEquals(IncidentStatus.MONITORING, reloaded.get().status());
        assertNonEmpty(reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertNonEmpty(reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());
        assertEquals(ComponentStatus.OPERATIONAL, reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status());
        assertEquals(ComponentStatus.MAINTENANCE, reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status());

//        System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WAITING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        componentA = page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertNonEmpty(componentA);
        resource.statusPage().deleteComponent(componentA.get());

        componentB = page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny();
        assertNonEmpty(componentB);
        resource.statusPage().deleteComponent(componentB.get());

        page = resource.statusPage().sync(page);
        assertEmpty(page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertEmpty(page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());

        Optional<Incident> removed = resource.statusPage().deleteIncident(page.id(), incident.get().id());
        assertTrue(removed.isPresent());

        page = resource.statusPage().sync(page);
        assertEmpty(page.incidents());
    }

//    @Ignore
    @Test public void G_testIncidentLifecycleWithComponents() {
        Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();

        Optional<Component> componentA = resource.statusPage().createComponent(
                page.id(),
                TestConstants.TEST_COMPONENT_A_TITLE,
                TestConstants.TEST_COMPONENT_A_DESCRIPTION,
                true
        );
        assertNonEmpty(componentA);
        componentA.get().status(ComponentStatus.DEGRADED);

        Optional<Component> componentB = resource.statusPage().createComponent(
                page.id(),
                TestConstants.TEST_COMPONENT_B_TITLE,
                TestConstants.TEST_COMPONENT_B_DESCRIPTION,
                true
        );
        assertNonEmpty(componentB);
        componentB.get().status(ComponentStatus.MAJOR_OUTAGE);

        Map<String, Object> jira = new HashMap<>();
        jira.put("issue_id", "<TEST ID>");
        Map<String, Object> meta = new HashMap<>();
        meta.put("jira", jira);

        resource.statusPage().createIncident(
                page.id(),
                resource.getIncidentTitle(),
                "something's happened... dealing with it",
                IncidentSeverity.MAJOR
        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));

        page = resource.statusPage().sync(page);
        Optional<Incident> incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertTrue(incident.isPresent());

//        System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WAITING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        incident.get().impact(IncidentSeverity.MINOR);
        incident.get().status(IncidentStatus.IDENTIFIED);
        incident.get().components().add(componentA.get());
        incident.get().components().add(componentB.get());
        Optional<Incident> updated = resource.statusPage().updateIncident(incident.get());
        assertNonEmpty(updated);
        assertEquals(IncidentSeverity.MINOR, updated.get().impact());
        assertEquals(IncidentStatus.IDENTIFIED, updated.get().status());

        page = resource.statusPage().sync(page);
        Optional<Incident> reloaded = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());

        assertTrue(reloaded.isPresent());
        assertEquals(resource.getIncidentTitle(), reloaded.get().name());
        assertEquals(IncidentSeverity.MINOR, reloaded.get().impact());
        assertEquals(IncidentStatus.IDENTIFIED, reloaded.get().status());
        assertNonEmpty(reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertNonEmpty(reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());
        assertEquals(ComponentStatus.DEGRADED, reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status());
        assertEquals(ComponentStatus.MAJOR_OUTAGE, reloaded.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status());

//        System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WAITING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        page = resource.statusPage().sync(page);
        incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertNonEmpty(incident);
        incident.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status(ComponentStatus.OPERATIONAL);
        incident.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status(ComponentStatus.MAINTENANCE);
        incident.get().status(IncidentStatus.MONITORING);
        updated = resource.statusPage().updateIncident(incident.get());
        assertNonEmpty(updated);
        assertEquals(IncidentSeverity.MINOR, updated.get().impact());
        assertEquals(IncidentStatus.MONITORING, updated.get().status());
        assertNonEmpty(updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertNonEmpty(updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());
        assertEquals(ComponentStatus.OPERATIONAL, updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status());
        assertEquals(ComponentStatus.MAINTENANCE, updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status());

//        System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WAITING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        page = resource.statusPage().sync(page);
        incident = resource.statusPage().getIncident(page.id(), page.incidents().get(0).id());
        assertNonEmpty(incident);
        incident.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status(ComponentStatus.OPERATIONAL);
        incident.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status(ComponentStatus.OPERATIONAL);
        incident.get().status(IncidentStatus.RESOLVED);
        updated = resource.statusPage().updateIncident(incident.get());
        assertNonEmpty(updated);
        assertEquals(IncidentStatus.RESOLVED, updated.get().status());
        assertNonEmpty(updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertNonEmpty(updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());
        assertEquals(ComponentStatus.OPERATIONAL, updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findFirst().get().status());
        assertEquals(ComponentStatus.OPERATIONAL, updated.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findFirst().get().status());

//        System.err.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ WAITING ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        try {
//            Thread.sleep(60000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        componentA = page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertNonEmpty(componentA);
        resource.statusPage().deleteComponent(componentA.get());

        componentB = page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny();
        assertNonEmpty(componentB);
        resource.statusPage().deleteComponent(componentB.get());

        page = resource.statusPage().sync(page);
        assertEmpty(page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertEmpty(page.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());

        Optional<Incident> removed = resource.statusPage().deleteIncident(page.id(), incident.get().id());
        assertTrue(removed.isPresent());

        page = resource.statusPage().sync(page);
        assertEmpty(page.incidents());
    }

}

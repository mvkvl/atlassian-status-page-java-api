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
import static ws.slink.test.statuspage.config.TestConstants.TEST_PAGE_NAME;
import static ws.slink.test.statuspage.tools.AssertTools.assertEmpty;
import static ws.slink.test.statuspage.tools.AssertTools.assertNonEmpty;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IncidentTest {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void A_testCreateIncident() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        resource.statusPage().createIncident(
            page.get().id(),
            resource.getIncidentTitle(),
            "something's happened... dealing with it",
            IncidentSeverity.MAJOR
        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));
        Page syncPage = resource.statusPage().sync(page.get());
        assertNonEmpty(syncPage.incidents().stream().filter(i -> i.name().endsWith(resource.getIncidentTitle())).findAny());
    }

    @Test public void B_testListIncidents() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        List<Incident> incidents = resource.statusPage().incidents(page.get());
        assertEquals(1, incidents.size());
        assertNonEmpty(incidents.stream().filter(i -> i.name().endsWith(resource.getIncidentTitle())).findAny());
    }

    @Test public void C_testGetIncident() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        assertEquals(1, page.get().incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.get().id(), page.get().incidents().get(0).id());
        assertTrue(incident.isPresent());
        assertEquals(resource.getIncidentTitle(), incident.get().name());
        assertEquals(IncidentSeverity.MAJOR, incident.get().impact());
        assertEquals(IncidentStatus.INVESTIGATING, incident.get().status());
    }

    @Test public void D_testUpdateIncident() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        assertEquals(1, page.get().incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.get().id(), page.get().incidents().get(0).id());
        assertTrue(incident.isPresent());

        incident.get().impact(IncidentSeverity.MINOR);
        incident.get().status(IncidentStatus.MONITORING);
        Optional<Incident> updated = resource.statusPage().updateIncident(incident.get());
        assertTrue(updated.isPresent());
        assertEquals(IncidentSeverity.MINOR, incident.get().impact());
        assertEquals(IncidentStatus.MONITORING, incident.get().status());

        Page syncPage = resource.statusPage().sync(page.get());
        Optional<Incident> reloaded = resource.statusPage().getIncident(page.get().id(), page.get().incidents().get(0).id());
        assertTrue(reloaded.isPresent());
        assertEquals(resource.getIncidentTitle(), incident.get().name());
        assertEquals(IncidentSeverity.MINOR, reloaded.get().impact());
        assertEquals(IncidentStatus.MONITORING, reloaded.get().status());
    }

    @Test public void E_testDeleteIncident() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        assertEquals(1, page.get().incidents().size());

        Optional<Incident> incident = resource.statusPage().getIncident(page.get().id(), page.get().incidents().get(0).id());
        assertTrue(incident.isPresent());

        Optional<Incident> removed = resource.statusPage().deleteIncident(page.get().id(), incident.get().id());
        assertTrue(removed.isPresent());

        Page syncPage = resource.statusPage().sync(page.get());
        assertEquals(0, syncPage.incidents().size());
    }

    @Ignore
    @Test public void F_testUpdateIncidentWithComponents() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        Optional<Component> componentA = resource.statusPage().createComponent(
                page.get().id(),
                TestConstants.TEST_COMPONENT_A_TITLE,
                TestConstants.TEST_COMPONENT_A_DESCRIPTION,
                true
        );
        assertNonEmpty(componentA);
        componentA.get().status(ComponentStatus.DEGRADED);

        Optional<Component> componentB = resource.statusPage().createComponent(
                page.get().id(),
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
            page.get().id(),
            resource.getIncidentTitle(),
            "something's happened... dealing with it",
            IncidentSeverity.MAJOR,
            meta,
            Arrays.asList(componentA.get(), componentB.get())
        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));

        Page syncPage = resource.statusPage().sync(page.get());
        Optional<Incident> incident = resource.statusPage().getIncident(syncPage.id(), syncPage.incidents().get(0).id());
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

        syncPage = resource.statusPage().sync(syncPage);
        Optional<Incident> reloaded = resource.statusPage().getIncident(syncPage.id(), syncPage.incidents().get(0).id());

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

        componentA = syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertNonEmpty(componentA);
        resource.statusPage().deleteComponent(componentA.get());

        componentB = syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny();
        assertNonEmpty(componentB);
        resource.statusPage().deleteComponent(componentB.get());

        syncPage = resource.statusPage().sync(syncPage);
        assertEmpty(syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertEmpty(syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());

        Optional<Incident> removed = resource.statusPage().deleteIncident(syncPage.id(), incident.get().id());
        assertTrue(removed.isPresent());

        syncPage = resource.statusPage().sync(syncPage);
        assertEmpty(syncPage.incidents());
    }

    @Ignore
    @Test public void G_testIncidentLifecycleWithComponents() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        Optional<Component> componentA = resource.statusPage().createComponent(
                page.get().id(),
                TestConstants.TEST_COMPONENT_A_TITLE,
                TestConstants.TEST_COMPONENT_A_DESCRIPTION,
                true
        );
        assertNonEmpty(componentA);
        componentA.get().status(ComponentStatus.DEGRADED);

        Optional<Component> componentB = resource.statusPage().createComponent(
                page.get().id(),
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
                page.get().id(),
                resource.getIncidentTitle(),
                "something's happened... dealing with it",
                IncidentSeverity.MAJOR
        );//.ifPresentOrElse(System.out::println, () -> System.out.println("could not create incident"));

        Page syncPage = resource.statusPage().sync(page.get());
        Optional<Incident> incident = resource.statusPage().getIncident(syncPage.id(), syncPage.incidents().get(0).id());
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

        syncPage = resource.statusPage().sync(syncPage);
        Optional<Incident> reloaded = resource.statusPage().getIncident(syncPage.id(), syncPage.incidents().get(0).id());

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

        syncPage = resource.statusPage().sync(syncPage);
        incident = resource.statusPage().getIncident(syncPage.id(), syncPage.incidents().get(0).id());
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

        syncPage = resource.statusPage().sync(syncPage);
        incident = resource.statusPage().getIncident(syncPage.id(), syncPage.incidents().get(0).id());
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

        componentA = syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertNonEmpty(componentA);
        resource.statusPage().deleteComponent(componentA.get());

        componentB = syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny();
        assertNonEmpty(componentB);
        resource.statusPage().deleteComponent(componentB.get());

        syncPage = resource.statusPage().sync(syncPage);
        assertEmpty(syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny());
        assertEmpty(syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_B_TITLE)).findAny());

        Optional<Incident> removed = resource.statusPage().deleteIncident(syncPage.id(), incident.get().id());
        assertTrue(removed.isPresent());

        syncPage = resource.statusPage().sync(syncPage);
        assertEmpty(syncPage.incidents());
    }

}

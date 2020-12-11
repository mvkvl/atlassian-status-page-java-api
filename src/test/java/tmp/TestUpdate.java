package tmp;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.Test;
import ws.slink.statuspage.model.Incident;
import ws.slink.statuspage.model.Page;
import ws.slink.statuspage.type.IncidentSeverity;
import ws.slink.statuspage.type.IncidentStatus;
import ws.slink.test.statuspage.config.StatusPageTestResource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class TestUpdate {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void testUpdateIncident() {
        resource.run(() -> {
            Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
            page = resource.statusPage().sync(page);

            Incident incident = new Incident()
                .name("test incident")
                .impact(IncidentSeverity.MAJOR)
                .status(IncidentStatus.INVESTIGATING)
            ;

            Map<String, String> jiraMeta = new HashMap<>();
            jiraMeta.put("issueId", "1234567");
            incident.metadata().put("jira", jiraMeta);

            page.groups()
                .stream()
                .flatMap(v -> v.components().stream())
                .filter(v -> !v.group())
                .limit(1)
                .forEach(v -> incident.components().add(v));
            incident.components().stream().forEach(c -> c.status(resource.randomComponentStatus()));

            Optional<Incident> incidentOpt = resource.statusPage().createIncident(page.id(), incident, "something's happened... dealing with it");
            if (incidentOpt.isPresent()) {
                log.info("incident created: {}", incidentOpt.get());
            }

            Incident createdIncident = incidentOpt.get();
            createdIncident.status(IncidentStatus.IDENTIFIED);

            page.groups()
                .stream()
                .flatMap(v -> v.components().stream())
                .filter(v -> !v.group())
                .filter(v -> !createdIncident.components().stream().map(w -> w.id()).collect(Collectors.toList()).contains(v.id()))
                .forEach(v -> createdIncident.components().add(v));
            createdIncident.components().stream().forEach(c -> c.status(resource.randomComponentStatus()));

//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            resource.statusPage().updateIncident(createdIncident, "first investigation report").ifPresentOrElse(System.out::println, () -> System.err.println("could not update incident"));

            createdIncident.components().clear();

//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            resource.statusPage().updateIncident(createdIncident.impact(IncidentSeverity.MINOR), "second investigation report").ifPresentOrElse(System.out::println, () -> System.err.println("could not update incident"));

//            resource.getStatusPage().getIncident(page.id(), incident.id()).ifPresent(System.out::println);
//            resource.getStatusPage().getIncident(page.id(), incident.id(), true).ifPresent(System.out::println);
//            resource.getStatusPage().getIncident(page.id(), "stub", true).ifPresent(System.out::println);
        });
    }

    @Test public void testUpdateIncidentComponent() {
        resource.run(() -> {
            Page page = resource.statusPage().getPage(resource.statusPage().pages().get(0).id(), true).get();
            page = resource.statusPage().sync(page);

            Incident incident = new Incident()
                    .name("test incident")
                    .impact(IncidentSeverity.MAJOR)
                    .status(IncidentStatus.INVESTIGATING)
                    ;

            Map<String, String> jiraMeta = new HashMap<>();
            jiraMeta.put("issueId", "1234567");
            incident.metadata().put("jira", jiraMeta);

            page.groups()
                .stream()
                .flatMap(v -> v.components().stream())
                .filter(v -> !v.group())
                .limit(1)
                .forEach(v -> incident.components().add(v));
            incident.components().stream().forEach(c -> c.status(resource.randomComponentStatus()));

            Optional<Incident> incidentOpt = resource.statusPage().createIncident(page.id(), incident, "something's happened... dealing with it");
            if (incidentOpt.isPresent()) {
                log.info("incident created: {}", incidentOpt.get());
            }

            Incident createdIncident = incidentOpt.get();
            createdIncident.status(IncidentStatus.IDENTIFIED);

            page.groups()
                    .stream()
                    .flatMap(v -> v.components().stream())
                    .filter(v -> !v.group())
                    .filter(v -> !createdIncident.components().stream().map(w -> w.id()).collect(Collectors.toList()).contains(v.id()))
                    .forEach(v -> createdIncident.components().add(v));
            createdIncident.components().stream().forEach(c -> c.status(resource.randomComponentStatus()));

//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            resource.statusPage().updateIncident(createdIncident, "first investigation report").ifPresentOrElse(System.out::println, () -> System.err.println("could not update incident"));

            createdIncident.components().clear();

//            try {
//                Thread.sleep(10000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            resource.statusPage().updateIncident(createdIncident.impact(IncidentSeverity.MINOR), "second investigation report").ifPresentOrElse(System.out::println, () -> System.err.println("could not update incident"));

//            resource.getStatusPage().getIncident(page.id(), incident.id()).ifPresent(System.out::println);
//            resource.getStatusPage().getIncident(page.id(), incident.id(), true).ifPresent(System.out::println);
//            resource.getStatusPage().getIncident(page.id(), "stub", true).ifPresent(System.out::println);
        });
    }

}

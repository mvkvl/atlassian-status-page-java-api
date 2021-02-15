package ws.slink.statuspage;

import com.google.gson.Gson;
import lombok.NonNull;
import ws.slink.statuspage.model.Component;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Incident;
import ws.slink.statuspage.model.Page;
import ws.slink.statuspage.type.ComponentStatus;
import ws.slink.statuspage.type.IncidentSeverity;
import ws.slink.statuspage.type.IncidentStatus;

import java.util.*;
import java.util.stream.Collectors;

public class StatusPage {


    // BUILDER
    public static class Builder {
        private String apiKey;
        private boolean bridgeErrors = false;
        private boolean rateLimit = false;
        private long rateLimitDelay = 0;
        private String baseUrl = null;

        public Builder apiKey(String value) {
            this.apiKey = value;
            return this;
        }

        public Builder bridgeErrors(boolean value) {
            this.bridgeErrors = value;
            return this;
        }

        public Builder rateLimit(boolean value) {
            this.rateLimit = value;
            return this;
        }

        public Builder rateLimitDelay(long value) {
            this.rateLimitDelay = value;
            return this;
        }

        public Builder baseUrl(String value) {
            this.baseUrl = value;
            return this;
        }

        public StatusPage build() {
            StatusPage statusPage;
            if (null == baseUrl) {
                statusPage = new StatusPage(apiKey);
            } else {
                statusPage = new StatusPage(apiKey, baseUrl);
            }
            statusPage.statusPageApi.rateLimitDelay(rateLimitDelay).rateLimit(rateLimit).bridgeErrors(bridgeErrors);
            return statusPage;
        }
    }


    // STATUS PAGE API IMPLEMENTATION
    private StatusPageApi statusPageApi;


    // CONSTRUCTORS (ALL PRIVATE, CONSTRUCT WITH BUILDER ONLY)
    private StatusPage() {
    }
    private StatusPage(String apiToken) {
        this.statusPageApi = new StatusPageApi(apiToken);
    }
    private StatusPage(String apiToken, String baseUrl) {
        this.statusPageApi = new StatusPageApi(apiToken, baseUrl);
    }


    // SYNC STATUS PAGE
    public List<Page> sync() {
        List<Page> pages = pages();
        pages.stream().forEach(page -> {
            syncPage(page);
        });
        return pages;
    }
    public Page sync(@NonNull Page page) {
        syncPage(page);
        return page;
    }
    public Optional<Page> sync(@NonNull String pageId) {
        Optional<Page> page = getPage(pageId);
        if (page.isPresent()) {
            return Optional.of(sync(page.get()));
        } else {
            return Optional.empty();
        }
    }
    public Group sync(@NonNull Group group) {
        syncGroup(group);
        return group;
    }
//    public Optional<Incident> syncIncident(@NonNull String pageId, @NonNull String incidentId) {
//        Optional<Page> page = getPage(pageId);
//        if (page.isPresent()) {
//            Page syncedPage = sync(page.get());
//            return syncedPage.incidents().stream().filter(i -> i.id().equals(incidentId)).findAny();
//        } else {
//            return Optional.empty();
//        }
//    }



    // LIST OBJECTS
    public List<Page> pages() {
        return pages(0, 0);
    }
    public List<Page> pages(int pageSize, int pageNum) {
        return new StatusPageQuery(statusPageApi, Page.class)
            .list("pages", "no pages found", pageSize, pageNum);
    }

    public List<Group> groups(Page page) {
        return groups(page.id(), 0, 0);
    }
    public List<Group> groups(String pageId) {
        return groups(pageId, 0, 0);
    }
    private List<Group> groups(String pageId, int pageSize, int pageNum) {
        return new StatusPageQuery(statusPageApi, Group.class)
            .list(
                "pages/" + pageId + "/component-groups",
                "no component groups found for '" + pageId + "'",
                pageSize,
                pageNum
            );
    }


    public List<Component> components(Page page) {
        return components(page.id(), 0, 0, false);
    }
    public List<Component> components(Page page, int pageSize, int pageNum) {
        return components(page.id(), pageSize, pageNum, false);
    }
    public List<Component> components(String pageId) {
        return components(pageId, 0, 0, false);
    }
    public List<Component> components(String pageId, int pageSize, int pageNum) {
        return components(pageId, pageSize, pageNum, false);
    }
    public List<Component> components(Page page, boolean noGroups) {
        return components(page.id(), 0, 0, noGroups);
    }
    public List<Component> components(Page page, int pageSize, int pageNum, boolean noGroups) {
        return components(page.id(), pageSize, pageNum, noGroups);
    }
    public List<Component> components(String pageId, boolean noGroups) {
        return components(pageId, 0, 0, noGroups);
    }
    public List<Component> components(String pageId, int pageSize, int pageNum, boolean noGroups) {
        List<Component> components = new StatusPageQuery(statusPageApi, Component.class)
            .list(
                "pages/" + pageId + "/components",
                "no components found for page '" + pageId + "'",
                pageSize,
                pageNum
            );
        if (noGroups) {
            return components.stream().filter(c -> !c.group()).sorted(Comparator.comparing(Component::name)).collect(Collectors.toList());
        } else {
            return components.stream().sorted(Comparator.comparing(Component::name)).collect(Collectors.toList());
        }
    }

    public List<Component> groupComponents(Group group) {
        return groupComponents(group.pageId(), group.id(), 0, 0);
    }
    public List<Component> groupComponents(String pageId, String groupId) {
        return groupComponents(pageId, groupId, 0, 0);
    }
    private List<Component> groupComponents(String pageId, String groupId, int pageSize, int pageNum) {
        return components(pageId, pageSize, pageNum)
            .stream()
            .filter(v -> null != v.groupId() && v.groupId().equals(groupId))
            .collect(Collectors.toList())
        ;
    }

    public List<Component> nonGroupComponents(Page page) {
        return nonGroupComponents(page.id());
    }
    public List<Component> nonGroupComponents(String pageId) {
        return nonGroupComponents(pageId, 0, 0);
    }
    private List<Component> nonGroupComponents(String pageId, int pageSize, int pageNum) {
        return components(pageId, pageSize, pageNum)
            .stream()
            .filter(v -> null == v.groupId() || v.groupId().trim().isEmpty())
            .collect(Collectors.toList())
        ;
    }

    public List<Incident> incidents(Page page) {
        return incidents(page.id(), null, 0, 0);
    }
    public List<Incident> incidents(Page page, String query) {
        return incidents(page.id(), query, 0, 0);
    }
    public List<Incident> incidents(Page page, int pageSize, int pageNum) {
        return incidents(page.id(), null, pageSize, pageNum);
    }
    public List<Incident> incidents(Page page, String query, int pageSize, int pageNum) {
        return incidents(page.id(), query, pageSize, pageNum);
    }
    public List<Incident> incidents(String pageId) {
        return incidents(pageId, 0, 0);
    }
    public List<Incident> incidents(String pageId, String query) {
        return incidents(pageId, query, 0, 0);
    }
    public List<Incident> incidents(String pageId, int pageSize, int pageNum) {
        return incidents(pageId, null, pageSize, pageNum);
    }
    public List<Incident> incidents(String pageId, String query, int pageSize, int pageNum) {
        return new StatusPageQuery(statusPageApi, Incident.class)
                .list(
                        "pages/" + pageId + "/incidents",
                        "no incidents found for page '" + pageId + "'",
                        pageSize,
                        pageNum
                );
    }


    // GET OBJECTS
    public Optional<Page> getPage(String pageId) {
        return getPage(pageId, false);
    }
    public Optional<Page> getPage(String pageId, boolean full) {
        Optional<Page> page = new StatusPageQuery(statusPageApi, Page.class)
                .get("pages/" + pageId, "no page found with id " + pageId);
        if (full)
            page.ifPresent(this::syncPage);
        return page;
    }
    public Optional<Page> getPageByTitle(String pageTitle) {
        return getPageByTitle(pageTitle, false);
    }
    public Optional<Page> getPageByTitle(String pageTitle, boolean full) {
        Optional<Page> page = pages().stream().filter(p -> null != p.name()).filter(p -> p.name().equals(pageTitle)).findAny();
        if (full)
            page.ifPresent(this::syncPage);
        return page;
    }

    public Optional<Group> getGroup(String pageId, String groupId) {
        return getGroup(pageId, groupId, false);
    }
    public Optional<Group> getGroup(String pageId, String groupId, boolean full) {
        Optional<Group> group = new StatusPageQuery(statusPageApi, Group.class)
                .get("pages/" + pageId + "/component-groups/" + groupId, "no group found for page #" + pageId + " with id " + groupId);
        if (full)
            group.ifPresent(this::syncGroup);
        return group;
    }

    public Optional<Component> getComponent(String pageId, String componentId) {
        return getComponent(pageId, componentId, false);
    }
    public Optional<Component> getComponent(String pageId, String componentId, boolean full) {
        Optional<Component> component = new StatusPageQuery(statusPageApi, Component.class)
                .get("pages/" + pageId + "/components/" + componentId, "no component found for page #" + pageId + " with id " + componentId);
//        if (full)
//            component.ifPresent(this::syncGroup);
        return component;
    }

    public Optional<Incident> getIncident(String pageId, String incidentId) {
        return getIncident(pageId, incidentId, false);
    }
    public Optional<Incident> getIncident(String pageId, String incidentId, boolean full) {
        Optional<Incident> incident = new StatusPageQuery(statusPageApi, Incident.class)
                .get("pages/" + pageId + "/incidents/" + incidentId, "no incident found for page #" + pageId + " with id " + incidentId);
        if (full) {
            incident.ifPresent(this::syncIncident);
        }
        return incident;
    }


    // CREATE OBJECTS
    public Optional<Incident> createIncident(String pageId, String title, String body) {
        return createIncident(pageId, title, body, IncidentStatus.INVESTIGATING, IncidentSeverity.NONE, null, null);
    }
    public Optional<Incident> createIncident(String pageId, String title, String body, IncidentSeverity severity) {
        return createIncident(pageId, title, body, IncidentStatus.INVESTIGATING, severity, null, null);
    }
    public Optional<Incident> createIncident(String pageId, String title, String body, IncidentSeverity severity, Map<String, Object> meta) {
        return createIncident(pageId, title, body, IncidentStatus.INVESTIGATING, severity, meta, null);
    }
    public Optional<Incident> createIncident(String pageId, String title, String body, IncidentSeverity severity, Map<String, Object> meta, List<Component> components) {
        return createIncident(pageId, title, body, IncidentStatus.INVESTIGATING, severity, meta, components);
    }
    public Optional<Incident> createIncident(String pageId, String title, String body, IncidentStatus status, IncidentSeverity severity, Map<String, Object> meta, List<Component> components) {
        Optional<Incident> createdIncident = new StatusPageQuery(statusPageApi, Incident.class)
            .post(
                "pages/" + pageId + "/incidents",
                "could not create incident for page #" + pageId,
                incidentRequestJson(null, title, body, pageId, status, severity, meta, components)
            );
//        if (full)
//            component.ifPresent(this::syncIncident);
        return createdIncident;
    }
    public Optional<Incident> createIncident(Page page, Incident incident, String body) {
        return createIncident(page.id(), incident, body);
    }
    public Optional<Incident> createIncident(Incident incident, String body) {
        return createIncident(incident.pageId(), incident, body);

    }
    public Optional<Incident> createIncident(String pageId, Incident incident, String body) {
        return new StatusPageQuery(statusPageApi, Incident.class)
            .post(
                "pages/" + pageId + "/incidents",
                "could not create incident for page #" + pageId,
                incidentRequestJson(incident, body)
            );
    }

    public Optional<Component> createComponent(Component component) {
        return createComponent(
            component.pageId(),
            component.groupId(),
            component.name(),
            component.description(),
            component.status(),
            component.onlyShowIfDegraded(),
            component.showcase()
        );
    }
    public Optional<Component> createComponent(String pageId, String title, String description) {
        return createComponent(pageId, null, title, description, null, null, null);
    }
    public Optional<Component> createComponent(String pageId, String title, String description, boolean showcase) {
        return createComponent(pageId, null, title, description, null, null, showcase);
    }
    public Optional<Component> createComponent(String pageId, String title, String description, ComponentStatus status) {
        return createComponent(pageId, null, title, description, status, null, null);
    }
    public Optional<Component> createComponent(String pageId, String groupId, String title, String description) {
        return createComponent(pageId, groupId, title, description, null, null, null);
    }
    public Optional<Component> createComponent(String pageId, String groupId, String title, String description, ComponentStatus status) {
        return createComponent(pageId, groupId, title, description, status, null, null);
    }
    public Optional<Component> createComponent(String pageId, String groupId, String title, String description, ComponentStatus status, Boolean onlyShowIfDegraded, Boolean showcase) {
        return new StatusPageQuery(statusPageApi, Component.class)
            .post(
                "pages/" + pageId + "/components",
                "could not create component for page #" + pageId,
                componentRequestJson(null, title, description, pageId, groupId, status, onlyShowIfDegraded, showcase, null)
            );
    }

    public Optional<Group> createGroup(Group group) {
        return createGroup(
            group.pageId(),
            group.name(),
            group.description(),
            group.components()
        );
    }
    public Optional<Group> createGroup(String pageId, String title, String description) {
        return createGroup(pageId, title, description, null);
    }
    public Optional<Group> createGroup(String pageId, String title, String description, List<String> componentIds) {
        return new StatusPageQuery(statusPageApi, Component.class)
            .post(
                "pages/" + pageId + "/component-groups",
                "could not create component group for page #" + pageId,
                groupRequestJson(title, description, componentIds)
            );
    }


    // UPDATE OBJECTS
    public Optional<Incident> updateIncident(Incident incident) {
        return updateIncident(incident.pageId(), incident, null);
    }
    public Optional<Incident> updateIncident(Incident incident, String body) {
        return updateIncident(incident.pageId(), incident, body);
    }
    public Optional<Incident> updateIncident(String pageId, Incident incident) {
        return updateIncident(pageId, incident, null);
    }
    public Optional<Incident> updateIncident(String pageId, Incident incident, String body) {
        return new StatusPageQuery(statusPageApi, Incident.class)
            .put(
                "pages/" + pageId + "/incidents/" + incident.id(),
                "could not update incident #" + incident.id(),
                incidentRequestJson(incident, body)
            );
//        if (full)
//            component.ifPresent(this::syncIncident);
    }

    public Optional<Component> updateComponent(Component component) {
        return updateComponent(component.pageId(), component, null);
    }
    public Optional<Component> updateComponent(Component component, ComponentStatus status) {
        return updateComponent(component.pageId(), component, status);
    }
    public Optional<Component> updateComponent(String pageId, Component component) {
        return updateComponent(pageId, component, null);
    }
    public Optional<Component> updateComponent(String pageId, Component component, ComponentStatus status) {
        if (null != status)
            component.status(status);
        return new StatusPageQuery(statusPageApi, Component.class)
            .put(
                "pages/" + pageId + "/components/" + component.id(),
                "could not update component #" + component.id(),
                componentRequestJson(component)
            );
    }

    public Optional<Group> updateGroup(Group group) {
        return updateGroup(group.pageId(), group);
    }
    public Optional<Group> updateGroup(String pageId, Group group) {
        return updateGroup(pageId, group, null);
    }
    public Optional<Group> updateGroup(String pageId, Group group, List<String> componentIds) {
        if (null != componentIds)
            group.components(componentIds);
        System.out.println("----- updateGroup: " + group);
        return new StatusPageQuery(statusPageApi, Group.class)
            .put(
                "pages/" + pageId + "/component-groups/" + group.id(),
                "could not update component group #" + group.id(),
                groupRequestJson(group)
            );
    }


    // DELETE OBJECTS
    public Optional<Incident> deleteIncident(Incident incident) {
        return deleteIncident(incident.pageId(), incident);
    }
    public Optional<Incident> deleteIncident(String pageId, Incident incident) {
        return deleteIncident(pageId, incident.id());
    }
    public Optional<Incident> deleteIncident(String pageId, String incidentId) {
        return new StatusPageQuery(statusPageApi, Incident.class)
            .delete(
                "pages/" + pageId + "/incidents/" + incidentId,
                "could not delete incident #" + incidentId
            );
    }

    public Optional<Component> deleteComponent(Component component) {
        return deleteComponent(component.pageId(), component);
    }
    public Optional<Component> deleteComponent(String pageId, Component component) {
        return deleteComponent(pageId, component.id());
    }
    public Optional<Component> deleteComponent(String pageId, String componentId) {
        return new StatusPageQuery(statusPageApi, Component.class)
            .delete(
                "pages/" + pageId + "/components/" + componentId,
                "could not delete component #" + componentId
            );
    }

    public Optional<Group> deleteGroup(Group group) {
        return deleteGroup(group.pageId(), group);
    }
    public Optional<Group> deleteGroup(String pageId, Group group) {
        return deleteGroup(pageId, group.id());
    }
    public Optional<Group> deleteGroup(String pageId, String groupId) {
        return new StatusPageQuery(statusPageApi, Component.class)
            .delete(
                "pages/" + pageId + "/component-groups/" + groupId,
                "could not delete component group #" + groupId
            );
    }

    // TOOLS
    private void syncPage(@NonNull Page page) {
        List<Group> groups = groups(page);
        groups.forEach(this::syncGroup);
        page
            .groups(groups)
            .components(components(page).stream().filter(c -> !c.group() && (null == c.groupId() || c.groupId().isEmpty())).collect(Collectors.toList()))
            .incidents(incidents(page, Integer.MAX_VALUE, 1))
        ;
    }
    private void syncGroup(@NonNull Group group) {
        group.componentObjects(groupComponents(group));
        group.components(group.componentObjects().stream().map(Component::id).collect(Collectors.toList()));
    }
    private void syncIncident(@NonNull Incident incident) {
        getPage(incident.pageId()).ifPresent(page -> incident.page(page));
    }

    private String incidentRequestJson(Incident incident, String body) {
        return incidentRequestJson(
            incident.id(),
            incident.name(),
            body,
            incident.pageId(),
            incident.status(),
            incident.impact(),
            incident.metadata(),
            incident.components()
        );
    }
    private String incidentRequestJson(
        String id,
        String title,
        String body,
        String pageId,
        IncidentStatus status,
        IncidentSeverity severity,
        Map<String, Object> meta,
        List<Component> components
    ) {
        Map<String, Object> map = new HashMap<>();
        if (null != pageId && pageId.isEmpty())
            map.put("page_id", pageId);
        if (null != id && !id.isEmpty())
            map.put("id", id);
        if (null != title && !title.isEmpty())
            map.put("name", title);
        if (null != body && !body.isEmpty())
            map.put("body", body);
        if (null != status)
            map.put("status", status.value());
        if (null != severity)
            map.put("impact_override", severity.value());
        else
            map.put("impact_override", IncidentSeverity.NONE.value());
        if (null != meta && !meta.isEmpty())
            map.put("metadata", meta);
        if (null != components /*&& !components.isEmpty()*/) {
            map.put("component_ids", components.stream().map(Component::id).collect(Collectors.toList()));
            map.put("components", components.stream().collect(Collectors.toMap(Component::id, c -> c.status().value())));
        }

        map.put("deliver_notifications", true);

/*
        if (null != incident.components())
            json.put("component_ids", incident.components().stream().map(v -> v.id()).collect(Collectors.toList()));

        if (null != incident.components())
            json.put("components", incident.components().stream().collect(Collectors.toMap(Component::id, c -> c.status().value())));
*/
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("incident", map);

        return new Gson().toJson(wrapper);
    }


    private String componentRequestJson(Component component) {
        return componentRequestJson(
                component.id(),
                component.name(),
                component.description(),
                component.pageId(),
                component.groupId(),
                component.status(),
                component.onlyShowIfDegraded(),
                component.showcase(),
                null
        );
    }
    private String componentRequestJson(
            String id,
            String title,
            String description,
            String pageId,
            String groupId,
            ComponentStatus status,
            Boolean onlyShowIfDegraded,
            Boolean showcase,
            String startDate
    ) {
        Map<String, Object> map = new HashMap<>();

        if (null != groupId && groupId.isEmpty())
            map.put("group_id", groupId);
//        if (null != pageId && pageId.isEmpty())
//            json.put("page_id", pageId);
//        if (null != id && !id.isEmpty())
//            json.put("id", id);
        if (null != title && !title.isEmpty())
            map.put("name", title);
        if (null != description && !description.isEmpty())
            map.put("description", description);
        if (null != status)
            map.put("status", status.value());
        else
            map.put("status", ComponentStatus.OPERATIONAL.value());
        if (null != onlyShowIfDegraded)
            map.put("only_show_if_degraded", onlyShowIfDegraded);
        if (null != showcase)
            map.put("showcase", showcase);
        if (null != startDate)
            map.put("start_date", startDate);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("component", map);
        return new Gson().toJson(wrapper);
    }

    private String groupRequestJson(Group group) {
        return groupRequestJson(
            group.name(),
            group.description(),
            group.components()
        );
    }
    private String groupRequestJson(
            String title,
            String description,
            List<String> componentIds
    ) {
        Map<String, Object> map = new HashMap<>();
        if (null != title && !title.isEmpty())
            map.put("name", title);
        if (null != componentIds && !componentIds.isEmpty())
            map.put("components", componentIds);
        if (null != description && !description.isEmpty())
            map.put("description", description);
        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("component_group", map);

        return new Gson().toJson(wrapper);
    }

}
/*
{
        "component": {
        "description": "string",
        "status": "operational",
        "name": "string",
        "only_show_if_degraded": true,
        "group_id": "string",
        "showcase": true,
        "start_date": "2020-12-08"
        }
}
*/
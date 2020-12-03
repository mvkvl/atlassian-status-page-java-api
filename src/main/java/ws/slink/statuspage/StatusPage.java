package ws.slink.statuspage;

import lombok.NonNull;
import ws.slink.statuspage.model.Component;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Incident;
import ws.slink.statuspage.model.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StatusPage {

    // BUILDER
    public static class Builder {
        private String apiKey;
        private boolean bridgeErrors = false;
        private boolean rateLimit    = false;
        private long rateLimitDelay  = 0;

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

        public StatusPage build() {
            StatusPage statusPage = new StatusPage(apiKey);
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

    // LIST OBJECTS
    public List<Page> pages() {
        return pages(0, 0);
    }
    public List<Page> pages(int pageSize, int pageNum) {
        return new StatusPageQuery(statusPageApi, Page.class)
            .list("pages", "no pages found", pageSize, pageNum);
    }

    public  List<Group> groups(Page page) {
        return groups(page.id(), 0, 0);
    }
    public  List<Group> groups(String pageId) {
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
        return components(page.id(), 0, 0);
    }
    public List<Component> components(Page page, int pageSize, int pageNum) {
        return components(page.id(), pageSize, pageNum);
    }
    public List<Component> components(String pageId) {
        return components(pageId, 0, 0);
    }
    public List<Component> components(String pageId, int pageSize, int pageNum) {
        return new StatusPageQuery(statusPageApi, Component.class)
            .list(
                "pages/" + pageId + "/components",
                "no components found for page '" + pageId + "'",
                pageSize,
                pageNum
            );
    }

    public List<Component> groupComponents(Group group) {
        return groupComponents(group.pageId(), group.id(), 0, 0);
    }
    public List<Component> groupComponents(String pageId, String groupId) {
        return groupComponents(pageId, groupId, 0, 0);
    }
    private List<Component> groupComponents(String pageId, String groupId, int pageSize, int pageNum) {
        return new StatusPageQuery(statusPageApi, Component.class)
            .list(
                "pages/" + pageId + "/components",
                "no component found for page '" + pageId +"'",
                pageSize,
                pageNum
            )
            .stream()
            .map(v -> (Component)v)
            .filter(v -> null != v.groupId() && v.groupId().equals(groupId))
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
        Optional<Incident> component = new StatusPageQuery(statusPageApi, Incident.class)
            .get("pages/" + pageId + "/incidents/" + incidentId, "no incident found for page #" + pageId + " with id " + incidentId);
//        if (full)
//            component.ifPresent(this::syncIncident);
        return component;
    }

    // TOOLS
    private void syncPage(@NonNull Page page) {
        List<Group> groups = groups(page);
        List<Component> components = components(page);
        List<Incident> incidents = incidents(page, Integer.MAX_VALUE, 1);
        groups.forEach(this::syncGroup);
        page
            .groups(groups)
            .components(components.stream().filter(c -> !c.group() && (null == c.groupId() || c.groupId().isEmpty())).collect(Collectors.toList()))
            .incidents(incidents)
        ;
    }
    private void syncGroup(@NonNull Group group) {
        group.components(groupComponents(group));
    }

}

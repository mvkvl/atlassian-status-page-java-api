package ws.slink.statuspage;

import ws.slink.statuspage.model.Component;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Page;

import java.util.List;
import java.util.stream.Collectors;

public class StatusPage {

    private StatusPageApi statusPageApi;

    public StatusPage(String apiToken) {
        this.statusPageApi = new StatusPageApi(apiToken);
    }
    public StatusPage(String apiToken, String baseUrl) {
        this.statusPageApi = new StatusPageApi(apiToken, baseUrl);
    }

    // API CONFIGURATION
    public StatusPage rateLimit(boolean value) {
        this.statusPageApi.rateLimit(value);
        return this;
    }
    public StatusPage rateLimitDelay(long value) {
        this.statusPageApi.rateLimitDelay(value);
        return this;
    }
    public StatusPage bridgeErrors(boolean value) {
        this.statusPageApi.bridgeErrors(value);
        return this;
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
                "no component found for page '" + pageId + "'",
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

}

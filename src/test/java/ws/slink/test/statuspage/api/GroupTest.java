package ws.slink.test.statuspage.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.*;
import org.junit.runners.MethodSorters;
import ws.slink.statuspage.model.Component;
import ws.slink.statuspage.model.Group;
import ws.slink.statuspage.model.Page;
import ws.slink.test.statuspage.config.StatusPageTestResource;
import ws.slink.test.statuspage.config.TestConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static ws.slink.test.statuspage.config.TestConstants.*;
import static ws.slink.test.statuspage.tools.AssertTools.assertEmpty;
import static ws.slink.test.statuspage.tools.AssertTools.assertNonEmpty;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupTest {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    private static Optional<Component> componentA;
    private static Optional<Component> componentB;

    @BeforeClass
    public static void prepare() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        componentA = resource.statusPage().createComponent(
                page.get().id(),
                TEST_COMPONENT_A_TITLE,
                TestConstants.TEST_COMPONENT_A_DESCRIPTION
        );
        assertNotNull(componentA);
        assertTrue(componentA.isPresent());
        componentB = resource.statusPage().createComponent(
                page.get().id(),
                TEST_COMPONENT_B_TITLE,
                TestConstants.TEST_COMPONENT_B_DESCRIPTION
        );
        assertNotNull(componentA);
        assertTrue(componentB.isPresent());
    }

    @AfterClass
    public static void cleanup() {
        resource.statusPage().deleteComponent(componentA.get());
        resource.statusPage().deleteComponent(componentB.get());
    }

    @Test
    public void A_testCreateGroup() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        resource.statusPage().createGroup(
                page.get().id(),
                TestConstants.TEST_GROUP_TITLE,
                TestConstants.TEST_GROUP_DESCRIPTION,
                Arrays.asList(componentA.get().id(), componentB.get().id())
        );
        Page syncPage = resource.statusPage().sync(page.get());
        assertNonEmpty(syncPage.groups().stream().filter(i -> TestConstants.TEST_GROUP_TITLE.equals(i.name())).findAny());
        assertNonEmpty(syncPage.groups().stream().filter(i -> TestConstants.TEST_GROUP_DESCRIPTION.equals(i.description())).findAny());
    }

    @Test
    public void B_testListGroups() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        List<Group> groups = resource.statusPage().groups(page.get());
        assertTrue(groups.size() > 0);

        Optional<Group> found = groups.stream().filter(g -> g.name().equals(TestConstants.TEST_GROUP_TITLE)).findAny();
        assertTrue(found.isPresent());
    }

    @Test
    public void C_testGetGroup() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        List<Group> groups = resource.statusPage().groups(page.get());
        assertTrue(groups.size() > 0);
        Optional<Group> found = groups.stream().filter(g -> g.name().equals(TestConstants.TEST_GROUP_TITLE)).findAny();
        assertTrue(found.isPresent());

        Optional<Group> group = resource.statusPage().getGroup(page.get().id(), found.get().id(), true);
        assertTrue(group.isPresent());
        assertNonEmpty(group.get().componentObjects().stream().map(Component::name).filter(c -> c.equals(TEST_COMPONENT_A_TITLE)).findAny());
        assertNonEmpty(group.get().componentObjects().stream().map(Component::name).filter(c -> c.equals(TEST_COMPONENT_B_TITLE)).findAny());
    }

    @Test
    public void D_testUpdateGroup() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        List<Group> groups = resource.statusPage().groups(page.get());
        assertTrue(groups.size() > 0);

        Optional<Group> found = groups.stream().filter(g -> g.name().equals(TestConstants.TEST_GROUP_TITLE)).findAny();
        found = resource.statusPage().getGroup(page.get().id(), found.get().id(), true);
        assertTrue(found.isPresent());

        found.get().components().remove(0);
        Optional<Group> updated = resource.statusPage().updateGroup(found.get());
        assertTrue(updated.isPresent());

        Group group = updated.get();
        resource.statusPage().sync(group);

        assertEquals(1, group.components().size());
        assertTrue(Arrays.asList(componentA.get().id(), componentB.get().id()).contains(group.components().get(0)));
    }

    @Test
    public void E_testDeleteGroup() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        List<Group> groups = resource.statusPage().groups(page.get());
        assertTrue(groups.size() > 0);

        Optional<Group> found = groups.stream().filter(g -> g.name().equals(TestConstants.TEST_GROUP_TITLE)).findAny();
        found = resource.statusPage().getGroup(page.get().id(), found.get().id(), true);
        assertTrue(found.isPresent());

        Optional<Group> removed = resource.statusPage().deleteGroup(page.get().id(), found.get().id());
        assertTrue(removed.isPresent());

        assertEmpty(resource.statusPage().groups(page.get()).stream().filter(g -> g.name().equals(TestConstants.TEST_GROUP_TITLE)).findAny());
    }

}
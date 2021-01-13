package ws.slink.test.statuspage.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ws.slink.statuspage.model.Component;
import ws.slink.statuspage.model.Page;
import ws.slink.statuspage.type.ComponentStatus;
import ws.slink.test.statuspage.config.StatusPageTestResource;
import ws.slink.test.statuspage.config.TestConstants;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static ws.slink.test.statuspage.config.TestConstants.TEST_PAGE_NAME;
import static ws.slink.test.statuspage.tools.AssertTools.assertNonEmpty;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ComponentTest {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void A_testCreateComponent() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        resource.statusPage().createComponent(
            page.get().id(),
            TestConstants.TEST_COMPONENT_A_TITLE,
            TestConstants.TEST_COMPONENT_A_DESCRIPTION
        );
        Page syncPage = resource.statusPage().sync(page.get());
        assertTrue(syncPage.components().stream().filter(i -> i.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny().isPresent());
    }

    @Test public void B_testListComponents() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        List<Component> components = resource.statusPage().components(page.get());
        assertTrue(components.size() > 0);
        assertTrue(components.stream().filter(i -> i.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny().isPresent());
    }

    @Test public void C_testGetComponent() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);
        assertTrue(page.get().components().size() > 0);

        Optional<Component> component = page.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertTrue(component.isPresent());

        Optional<Component> loaded = resource.statusPage().getComponent(page.get().id(), component.get().id(), true);
        assertTrue(loaded.isPresent());
        assertEquals(TestConstants.TEST_COMPONENT_A_TITLE, loaded.get().name());
        assertEquals(ComponentStatus.OPERATIONAL, loaded.get().status());
    }

    @Test public void D_testUpdateComponent() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        Optional<Component> component = page.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertTrue(component.isPresent());

        Optional<Component> loaded = resource.statusPage().getComponent(page.get().id(), component.get().id(), true);
        assertTrue(loaded.isPresent());

        loaded.get().status(ComponentStatus.DEGRADED);
        loaded.get().description("updated description");
        Optional<Component> updated = resource.statusPage().updateComponent(loaded.get());

        assertTrue(updated.isPresent());
        assertEquals(ComponentStatus.DEGRADED, updated.get().status());
        assertEquals("updated description", updated.get().description());
    }

    @Test public void E_testDeleteComponent() {
        Optional<Page> page = resource.statusPage().getPageByTitle(TEST_PAGE_NAME, true);
        assertNonEmpty(page);

        Optional<Component> component = page.get().components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny();
        assertTrue(component.isPresent());

        Optional<Component> removed = resource.statusPage().deleteComponent(page.get().id(), component.get().id());
        assertTrue(removed.isPresent());

        Page syncPage = resource.statusPage().sync(page.get());
        assertFalse(syncPage.components().stream().filter(c -> c.name().equals(TestConstants.TEST_COMPONENT_A_TITLE)).findAny().isPresent());
    }

}

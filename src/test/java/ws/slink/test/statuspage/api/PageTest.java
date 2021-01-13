package ws.slink.test.statuspage.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import ws.slink.statuspage.model.Page;
import ws.slink.test.statuspage.config.StatusPageTestResource;
import ws.slink.test.statuspage.config.TestConstants;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static ws.slink.test.statuspage.tools.AssertTools.assertNonEmpty;

@Slf4j
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PageTest {

    @ClassRule
    public static StatusPageTestResource resource = StatusPageTestResource.get();

    @Test public void A_testPagesList() {
        List<Page> pages = resource.statusPage().pages();
        assertTrue(pages.size() > 0);
        assertNonEmpty(pages.stream().filter(p -> null != p.name()).filter(p -> p.name().equals(TestConstants.TEST_PAGE_NAME)).findAny());
    }

    @Test public void B_testGetPage() {
        Optional<Page> pg = resource.statusPage().pages().stream().filter(p -> null != p.name()).filter(p -> p.name().equals(TestConstants.TEST_PAGE_NAME)).findAny();
        assertNonEmpty(pg);
        Optional<Page> page = resource.statusPage().getPage(pg.get().id(), true);
        assertNonEmpty(page);
        assertEquals(TestConstants.TEST_PAGE_NAME, page.get().name());
    }

}

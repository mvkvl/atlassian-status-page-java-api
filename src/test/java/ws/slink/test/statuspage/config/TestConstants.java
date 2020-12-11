package ws.slink.test.statuspage.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.rules.ExternalResource;

public class TestConstants extends ExternalResource {

    public static final String TEST_PAGE_NAME = "dxFeed";
    public static final String TEST_COMPONENT_A_TITLE = "component A";
    public static final String TEST_COMPONENT_A_DESCRIPTION = "component A description";
    public static final String TEST_COMPONENT_B_TITLE = "component B";
    public static final String TEST_COMPONENT_B_DESCRIPTION = "component B description";
    public static final String TEST_COMPONENT_C_TITLE = "component C";
    public static final String TEST_COMPONENT_C_DESCRIPTION = "component C description";
    public static final String TEST_GROUP_TITLE = "test group";
    public static final String TEST_GROUP_DESCRIPTION = "test group description";

    private String incidentTitle;

    public TestConstants() {
        this.incidentTitle = "test incident: " + RandomStringUtils.randomAlphanumeric(10);
    }

    public String getIncidentTitle() {
        return this.incidentTitle;
    }

}

package ws.slink.test.statuspage.config;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.rules.ExternalResource;
import ws.slink.statuspage.StatusPage;
import ws.slink.statuspage.model.Page;
import ws.slink.statuspage.type.ComponentStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class StatusPageTestResource extends ExternalResource {

    private static int refCount = 0;
    private static StatusPageTestResource currentInstance;

    private StatusPage statusPage;
    private String incidentTitle;

    public StatusPageTestResource() {
        this.incidentTitle = TestConstants.TEST_INCIDENT_TITLE_PREFIX + RandomStringUtils.randomAlphanumeric(10);
    }

    public static StatusPageTestResource get() {
        if (refCount == 0) {
            currentInstance = new StatusPageTestResource();
        }
        return currentInstance;
    }

    public StatusPage statusPage() {
        return this.statusPage;
    }
    public String getIncidentTitle() {
        return this.incidentTitle;
    }

    @Override
    protected void before() {
        if (refCount++ == 0) {
            // actual test resources init
            this.statusPage = new StatusPage.Builder()
                .apiKey(System.getenv("STATUSPAGE_API_KEY"))
                .bridgeErrors(true)
                .rateLimit(true)
                .rateLimitDelay(1000)
                .build()
            ;
            deleteTestComponentsAndIncidents();
        }
    }

    private void deleteTestComponentsAndIncidents() {
        statusPage.getPageByTitle(TestConstants.TEST_PAGE_NAME, true).ifPresent(page -> {
            page.incidents()
                .stream()
                .filter(i -> i.name().startsWith(TestConstants.TEST_INCIDENT_TITLE_PREFIX))
                .forEach(statusPage::deleteIncident)
            ;
            List<String> testComponentNames = Arrays.asList(TestConstants.TEST_COMPONENT_A_TITLE, TestConstants.TEST_COMPONENT_B_TITLE, TestConstants.TEST_COMPONENT_C_TITLE);
            page.components().stream().filter(c -> testComponentNames.contains(c.name())).forEach(statusPage::deleteComponent);
        });
    }

    @Override
    protected void after() {
        if (--refCount == 0) {
            // actual test resources destroy
        }
    }

    public void run(Runnable task) {
        try {
            task.run();
        } catch (Exception e) {
            if ("true".equalsIgnoreCase(System.getenv("STATUSPAGE_PRINT_STACK_TRACE"))) {
                e.printStackTrace();
                if (null != e.getCause())
                    e.getCause().printStackTrace();
            } else {
                System.err.println("error: " + e.getClass().getSimpleName() + " : " + e.getMessage() +
                    ((null == e.getCause())
                    ? ""
                    : " <- " + e.getCause().getClass().getSimpleName() + " : " + e.getCause().getMessage())
                );
            }
        }
    }

    Random random = new Random();
    public ComponentStatus randomComponentStatus() {
        int rnd = random.nextInt(100);
        if (rnd < 25) {
            return ComponentStatus.OPERATIONAL;
        } else if (rnd < 50) {
            return ComponentStatus.DEGRADED;
        } else if (rnd < 75) {
            return ComponentStatus.PARTIAL_OUTAGE;
        } else {
            return ComponentStatus.MAJOR_OUTAGE;
        }
    }

}

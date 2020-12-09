package ws.slink.test.statuspage;

import kong.unirest.Config;
import kong.unirest.HttpRequest;
import kong.unirest.Interceptor;
import kong.unirest.Unirest;
import org.junit.rules.ExternalResource;
import ws.slink.statuspage.StatusPage;

public class StatusPageTestResource extends ExternalResource {

    private static int refCount = 0;
    private static StatusPageTestResource currentInstance;

    private StatusPage statusPage;

    public static StatusPageTestResource get() {
        if (refCount == 0) {
            currentInstance = new StatusPageTestResource();
        }
        return currentInstance;
    }

    public StatusPage getStatusPage() {
        return this.statusPage;
    }

    @Override
    protected void before() {
        if (refCount++ == 0) {
            // actual test resources init
            if ("true".equalsIgnoreCase(System.getenv("LOG_QUERIES"))) {
                Unirest.config()
                    .interceptor(new Interceptor() {
                        @Override
                        public void onRequest(HttpRequest<?> request, Config config) {
                            System.out.println("--- " + request.getHttpMethod() + " " + request.getUrl());
                        }
                    })
                ;
            }
            this.statusPage = new StatusPage.Builder()
                .apiKey(System.getenv("STATUSPAGE_API_KEY"))
                .bridgeErrors(true)
                .rateLimit(false)
                .rateLimitDelay(1000)
                .build()
            ;
        }
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
            if ("true".equalsIgnoreCase(System.getenv("PRINT_STACK_TRACE"))) {
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

}

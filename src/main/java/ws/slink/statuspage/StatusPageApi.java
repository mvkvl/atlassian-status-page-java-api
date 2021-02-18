package ws.slink.statuspage;

import ws.slink.statuspage.error.ServiceCallException;
import ws.slink.statuspage.http.HttpClient;
import ws.slink.statuspage.http.HttpResponse;
import ws.slink.statuspage.http.HttpMethod;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

class StatusPageApi {

    private final Random random = new Random();
    private final String apiToken;

    // if API calls should be rate limited
    private final AtomicBoolean rateLimitEnabled = new AtomicBoolean(false);

    // for how long (in ms) to delay before next API call
    private final AtomicLong rateLimitDelay = new AtomicLong (1000L);

    // rethrow service errors
    // true  - throw errors out of API calls
    // false - don't throw errors and return empty responses
    private final AtomicBoolean bridgeErrors = new AtomicBoolean (false);

    private String baseUrl = "https://api.statuspage.io/v1/";
    private final AtomicLong callTimestamp = new AtomicLong(0);

    public StatusPageApi(String apiToken) {
        this.apiToken = apiToken;
        init();
    }
    public StatusPageApi(String apiToken, String baseUrl) {
        this.apiToken = apiToken;
        this.baseUrl  = baseUrl;
        init();
    }
    public StatusPageApi(String apiToken, String baseUrl, long rateLimit) {
        this.apiToken = apiToken;
        this.baseUrl  = baseUrl;
        this.rateLimit(true);
        this.rateLimitDelay(rateLimit);
        init();
    }
    public StatusPageApi(String apiToken, String baseUrl, long rateLimit, boolean bridgeErrors) {
        this.apiToken = apiToken;
        this.baseUrl  = baseUrl;
        this.rateLimit(true);
        this.rateLimitDelay(rateLimit);
        this.bridgeErrors(bridgeErrors);
        init();
    }

    private void init() {
    }

    public StatusPageApi rateLimit(boolean value) {
        this.rateLimitEnabled.set(value);
        return this;
    }
    public boolean rateLimit() {
        return this.rateLimitEnabled.get();
    }
    public StatusPageApi rateLimitDelay(long value) {
        this.rateLimitDelay.set(value);
        return this;
    }
    public long rateLimitDelay() {
        return this.rateLimitDelay.get();
    }
    public StatusPageApi bridgeErrors(boolean value) {
        this.bridgeErrors.set(value);
        return this;
    }
    public boolean bridgeErrors() {
        return this.bridgeErrors.get();
    }

    HttpResponse apiCall(String path, HttpMethod method, Map<String, String> headers, Map<String, Object> queryParams, Object body) {
        if (rateLimitEnabled.get())
            delay(); // rate limit API usage
        if (method == HttpMethod.DELETE)
            delay(); // rate limit API usage (as we're performing two calls for delete)
        try {
            switch(method) {
                case GET: {
                    return new HttpClient(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .header(contentHeaderKey(), contentHeaderValue())
                        .header(acceptHeaderKey(), acceptHeaderValue())
                        .queryParams(queryParams)
                        .get();
                }
                case POST: {
                    return new HttpClient(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .header(contentHeaderKey(), contentHeaderValue())
                        .header(acceptHeaderKey(), acceptHeaderValue())
                        .post(body);
                }
                case PUT: {
                    return new HttpClient(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .header(contentHeaderKey(), contentHeaderValue())
                        .header(acceptHeaderKey(), acceptHeaderValue())
                        .put(body);
                }
                case PATCH: {
                    return new HttpClient(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .header(contentHeaderKey(), contentHeaderValue())
                        .header(acceptHeaderKey(), acceptHeaderValue())
                        .patch(body);
                }
                case DELETE: {
                    return new HttpClient(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .header(contentHeaderKey(), contentHeaderValue())
                        .header(acceptHeaderKey(), acceptHeaderValue())
                        .delete();
                }
                default: {
                    throw new IllegalArgumentException("unsupported method requested: " + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceCallException("error requesting status page service: " + e.getMessage()).setCause(e);
        }
    }

    private String authHeaderKey() {
        return "Authorization";
    }
    private String authHeaderValue() {
        return "OAuth " + apiToken;
    }
    private String contentHeaderKey() {
        return "Content-Type";
    }
    private String contentHeaderValue() {
        return "application/json; utf-8";
    }
    private String acceptHeaderKey() {
        return "Accept";
    }
    private String acceptHeaderValue() {
        return "application/json";
    }

    private String generateApiUrl(String path) {
        if (baseUrl.endsWith("/") && !path.startsWith("/"))
            return baseUrl + path;
        else if (!baseUrl.endsWith("/") && path.startsWith("/"))
            return baseUrl + path;
        else if (baseUrl.endsWith("/") && path.startsWith("/"))
            return baseUrl + path.substring(1);
        else
            return baseUrl + "/" + path.substring(1);
    }
    synchronized private void delay() {
        while (callTimestamp.get() > Instant.now().toEpochMilli() - rateLimitDelay.get()) {
            try { Thread.sleep(50 + random.nextInt(50)); } catch (InterruptedException e) { }
        }
        callTimestamp.set(Instant.now().toEpochMilli());
    }
}

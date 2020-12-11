package ws.slink.statuspage;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import ws.slink.statuspage.error.ServiceCallException;
import ws.slink.statuspage.type.HttpMethod;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

class StatusPageApi {

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
    }
    public StatusPageApi(String apiToken, String baseUrl) {
        this.apiToken = apiToken;
        this.baseUrl  = baseUrl;
    }
    public StatusPageApi(String apiToken, String baseUrl, long rateLimit) {
        this.apiToken = apiToken;
        this.baseUrl  = baseUrl;
        this.rateLimit(true);
        this.rateLimitDelay(rateLimit);
    }
    public StatusPageApi(String apiToken, String baseUrl, long rateLimit, boolean bridgeErrors) {
        this.apiToken = apiToken;
        this.baseUrl  = baseUrl;
        this.rateLimit(true);
        this.rateLimitDelay(rateLimit);
        this.bridgeErrors(bridgeErrors);
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

    // http://kong.github.io/unirest-java/
    HttpResponse<? extends Object> apiCall(String path, HttpMethod method, Map<String, String> headers, Map<String, Object> queryParams, Object body) {
        if (rateLimitEnabled.get())
            delay(); // rate limit API usage
        try {
            switch(method) {
                case GET: {
                    return Unirest
                        .get(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .queryString(queryParams)
                        .asJson();
                }
                case POST: {
                    return Unirest
                        .post(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .body(body)
                        .asJson();
                }
                case PUT: {
                    return Unirest
                        .put(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .body(body)
                        .asJson();
                }
                case PATCH: {
                    return Unirest
                        .patch(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .body(body)
                        .asJson();
                }
                case DELETE: {
                    return Unirest
                        .delete(generateApiUrl(path))
                        .headers(headers)
                        .header(authHeaderKey(), authHeaderValue())
                        .body(body)
                        .asJson();
                }
                default: {
                    throw new IllegalArgumentException("unsupported method requested: " + method);
                }
            }
        } catch (UnirestException e) {
            throw new ServiceCallException("error requesting status page service: "
                    + e.getMessage()
            ).setCause(e);
        }
    }

    private String authHeaderKey() {
        return "Authorization";
    }
    private String authHeaderValue() {
        return "OAuth " + apiToken;
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
            try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
        callTimestamp.set(Instant.now().toEpochMilli());
    }
}

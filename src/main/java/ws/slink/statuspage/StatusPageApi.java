package ws.slink.statuspage;

import ws.slink.statuspage.error.ServiceCallException;
import ws.slink.statuspage.type.HttpMethod;
import ws.slink.statuspage.type.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    private HttpClient httpClient;

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
        httpClient = HttpClient
            .newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build()
        ;
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

    HttpResponse<String> apiCall(String path, HttpMethod method, Map<String, String> headers, Map<String, Object> queryParams, Object body) {
        if (rateLimitEnabled.get())
            delay(); // rate limit API usage

        try {
            final HttpRequest.Builder requestBuilder;
            switch(method) {
                case GET: {
                    requestBuilder = HttpRequest.newBuilder()
                        .GET()
                        .header(authHeaderKey(), authHeaderValue())
                        .uri(URI.create(generateApiUrl(path) + formatQueryParams(queryParams)))
                    ;
                    break;
                }
                case POST: {
                    requestBuilder = HttpRequest.newBuilder()
                        .method("POST", HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header(authHeaderKey(), authHeaderValue())
                        .uri(URI.create(generateApiUrl(path)))
                    ;
                    break;
                }
                case PUT: {
                    requestBuilder = HttpRequest.newBuilder()
                        .method("PUT", HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header(authHeaderKey(), authHeaderValue())
                        .uri(URI.create(generateApiUrl(path)))
                    ;
                    break;
                }
                case PATCH: {
                    requestBuilder = HttpRequest.newBuilder()
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(body.toString()))
                        .header(authHeaderKey(), authHeaderValue())
                        .uri(URI.create(generateApiUrl(path)))
                    ;
                    break;
                }
                case DELETE: {
                    requestBuilder = HttpRequest.newBuilder()
                        .DELETE()
                        .header(authHeaderKey(), authHeaderValue())
                        .uri(URI.create(generateApiUrl(path)))
                    ;
                    break;
                }
                default: {
                    throw new IllegalArgumentException("unsupported method requested: " + method);
                }
            }

            if (null != headers)
                headers.forEach((k, v) -> requestBuilder.header(k, v));

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = null;
            // for DELETE first get the object being removed
            if (method == HttpMethod.DELETE) {
                response = apiCall(path, HttpMethod.GET, headers, queryParams, body);
                if (response.statusCode() == HttpStatus.OK.value()) {
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    return response;
                }
            }

            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//            if (getResult.statusCode() == HttpStatus.OK.value()) {
//                httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//                if (getResult.statusCode() == HttpStatus.OK.value()) {
//                    return getResult;
//                }
//            }
        } catch (Exception e) {
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
    private String formatQueryParams(Map<String, Object> queryParams) {
        if (null == queryParams || queryParams.isEmpty())
            return "";
        return "?" +
            queryParams.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"))
            ;
    }
    synchronized private void delay() {
        while (callTimestamp.get() > Instant.now().toEpochMilli() - rateLimitDelay.get()) {
            try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
        callTimestamp.set(Instant.now().toEpochMilli());
    }


}

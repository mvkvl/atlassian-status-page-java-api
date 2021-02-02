package ws.slink.statuspage.http;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(fluent = true)
public class HttpClient {

    private String url;
    private Map<String, Object> queryParams = new HashMap<>();

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, String> headers_ = new HashMap<>();
    private Object body;

    public HttpClient() {
    }
    public HttpClient(String url) {
        this.url = url;
    }

    public HttpClient header(String key, String value) {
        headers_.put(key, value);
        return this;
    }
    public HttpClient headers(Map<String, String> input) {
        if (null != input)
            headers_.putAll(input);
        return this;
    }
    public String header(String key) {
        return headers_.get(key);
    }
    public Map<String, String> headers() {
        return headers_;
    }

    public HttpResponse get(Map<String, Object> queryParams) throws IOException {
        this.queryParams = queryParams;
        return get();
    }
    public HttpResponse get() throws IOException {
        return request(HttpMethod.GET);
    }
    public HttpResponse post(Object body) throws IOException {
        this.body = body;
        return post();
    }
    public HttpResponse post() throws IOException {
        return request(HttpMethod.POST);
    }
    public HttpResponse put(Object body) throws IOException {
        this.body = body;
        return put();
    }
    public HttpResponse put() throws IOException {
        return request(HttpMethod.PUT);
    }
    public HttpResponse patch(Object body) throws IOException {
        this.body = body;
        return patch();
    }
    public HttpResponse patch() throws IOException {
        return request(HttpMethod.PATCH);
    }
    public HttpResponse delete() throws IOException {
        HttpResponse getResponse   = get();
        HttpResponse deleteReponse = request(HttpMethod.DELETE);
        if (deleteReponse.status() == HttpStatus.OK || deleteReponse.status() == HttpStatus.NO_CONTENT) {
            return getResponse;
        } else {
            return deleteReponse;
        }
    }

    private HttpResponse request(HttpMethod method) throws IOException {
        if (url.toLowerCase().startsWith("https")) {
            return securedRequest(method, new URL(createFullUrl(url, queryParams)));
        } else {
            return insecuredRequest(method, new URL(createFullUrl(url, queryParams)));
        }
    }
    private HttpResponse insecuredRequest(HttpMethod method, URL urlObject) throws IOException {
        HttpURLConnection con = (HttpURLConnection)urlObject.openConnection();
        return performRequest(con, method);
    }
    private HttpResponse securedRequest(HttpMethod method, URL urlObject) throws IOException {
        HttpsURLConnection con = (HttpsURLConnection)urlObject.openConnection();
        return performRequest(con, method);
    }
    private HttpResponse performRequest(HttpURLConnection connection, HttpMethod method) {
        HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String    responseMessage = "";
        String    responseBody    = "";
        try {
            connection.setRequestMethod(method.name());
            connection.setInstanceFollowRedirects(false);
            headers_.entrySet().stream().forEach(e -> connection.setRequestProperty(e.getKey(), e.getValue()));
            if (method != HttpMethod.GET) {
                connection.setDoOutput(true);
                try(OutputStream os = connection.getOutputStream()) {
                    if (null != body) {
                        byte[] input = getBodyAsBytes();
                        os.write(input, 0, input.length);
                    }
                }
            }
            responseStatus  = HttpStatus.of(connection.getResponseCode());
            responseMessage = connection.getResponseMessage();
            // connection.getErrorStream()
            try(BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                responseBody = readResponse(br);
            }
        } catch (IOException e) {

//            System.out.println("-----> io exception: " + e.getMessage() + " " + ((null != e.getCause()) ? e.getCause().getMessage() : ""));
//            System.out.println("  ---> base        : " + e.getClass().getSimpleName() + " " + e.getMessage());
            if (null != e.getCause()) {
//                System.out.println("   ---> cause 1    : " + e.getCause().getClass().getSimpleName() + " " + e.getCause().getMessage());
                if (null != e.getCause().getCause()) {
//                    System.out.println("    ---> cause 2   : " + e.getCause().getCause().getClass().getSimpleName() + " " + e.getCause().getCause().getMessage());
                    if (null != e.getCause().getCause().getCause()) {
//                        System.out.println("     ---> cause 3  : " + e.getCause().getCause().getCause().getClass().getSimpleName() + " " + e.getCause().getCause().getCause().getMessage());
                    }
                }
            }
            responseMessage = e.getMessage();
            try {
                responseStatus  = HttpStatus.of(connection.getResponseCode());
//                System.out.println("-----> status code : " + responseStatus);
            } catch (IOException ioException) {
                ioException.printStackTrace();
//                System.out.println("-----> status code : " + responseStatus + "(none)");
            }
        }

//        System.out.println("---> performRequest: " + responseStatus + " " + responseMessage + " " + responseBody);

        return new HttpResponse().status(responseStatus).message(responseMessage).body(responseBody);
    }

    private String readResponse(BufferedReader br) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            responseBody.append(responseLine.trim());
        }
        return responseBody.toString();
    }
    private String createFullUrl(String url_, Map<String, Object> queryParams_) throws UnsupportedEncodingException {
        if (null == queryParams_ || queryParams_.isEmpty()) {
            return url_;
        } else {
            if (url_.contains("?")) {
                return new StringBuilder().append(url_).append("&").append(ParameterStringBuilder.getParamsString(queryParams_)).toString();
            } else {
                return new StringBuilder().append(url_).append("?").append(ParameterStringBuilder.getParamsString(queryParams_)).toString();
            }
        }
    }
    private byte[] getBodyAsBytes() {
        if (body instanceof String)
            return ((String)body).getBytes(StandardCharsets.UTF_8);
        else if (body instanceof JsonObject)
            return ((JsonObject)body).getAsString().getBytes(StandardCharsets.UTF_8);
        else
            throw new IllegalArgumentException("unsupported body type: " + body.getClass().getSimpleName());
    }

}

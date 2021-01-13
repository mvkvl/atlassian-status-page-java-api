package ws.slink.statuspage;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import ws.slink.statuspage.error.JsonParseException;
import ws.slink.statuspage.error.NoDataFoundException;
import ws.slink.statuspage.error.ServiceCallException;
import ws.slink.statuspage.http.HttpMethod;
import ws.slink.statuspage.http.HttpResponse;
import ws.slink.statuspage.http.HttpStatus;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class StatusPageQuery<T> {

    private StatusPageApi statusPageApi;
    private final Class<T> clazz;

    private static final Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return LocalDateTime.parse(json.getAsJsonPrimitive().toString().replaceAll("\"", ""), DateTimeFormatter.ISO_DATE_TIME);
            }
        })
        .create()
    ;

    public StatusPageQuery(StatusPageApi statusPageApi, Class clazz) {
        this.statusPageApi = statusPageApi;
        this.clazz = clazz;
    }

    <T> List<T> list(String url) {
        return list(url, "", 0, 0);
    }
    <T> List<T> list(String url, String notFoundMessage) {
        return list(url, notFoundMessage, 0, 0);
    }
    <T> List<T> list(String url, int pageSize) {
        return list(url, "", pageSize, 0);
    }
    <T> List<T> list(String url, int pageSize, int pageNum) {
        return list(url, "", pageSize, pageNum);
    }
    <T> List<T> list(String url, String notFoundMessage, int pageSize, int pageNum) {
        try {
            Map<String, Object> queryParams = null;
            if (pageSize > 0 && pageNum > 0) {
                queryParams = new HashMap<>();
                queryParams.put("page", pageNum);
                queryParams.put("per_page", pageSize);
            }

            HttpResponse response = statusPageApi.apiCall(url, HttpMethod.GET, null, queryParams, null);

            if (response.status() == HttpStatus.OK) {
                Type collectionType = TypeToken.getParameterized(List.class, clazz).getType();
                List<T> result = gson.fromJson(response.getBodyAsString(), collectionType);
                return result;
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new NoDataFoundException(notFoundMessage);
                }
            }
        } catch (NoDataFoundException e) {
            throw e;
        } catch (JsonSyntaxException e) {
            if (statusPageApi.bridgeErrors()) {
                throw new JsonParseException(e.getMessage()).setCause(e);
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (statusPageApi.bridgeErrors()) {
                throw new ServiceCallException(e.getMessage()).setCause(e);
            } else {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    <T> Optional<T> get(String url) {
        return get(url, "");
    }
    <T> Optional<T> get(String url, String notFoundMessage) {
        return performRequest(url, HttpMethod.GET, Arrays.asList(HttpStatus.OK.code()), notFoundMessage, null);
    }

    <T> Optional<T> post(String url, String jsonBody) {
        return post(url, "", jsonBody);
    }
    <T> Optional<T> post(String url, String errorMessage, String jsonBody) {
        return performRequest(url, HttpMethod.POST, Arrays.asList(HttpStatus.CREATED.code()), errorMessage, jsonBody);
    }

    <T> Optional<T> put(String url, String jsonBody) {
        return put(url, "", jsonBody);
    }
    <T> Optional<T> put(String url, String errorMessage, String jsonBody) {
        return performRequest(url, HttpMethod.PUT, Arrays.asList(HttpStatus.OK.code()), errorMessage, jsonBody);
    }

    <T> Optional<T> delete(String url) {
        return delete(url, "");
    }
    <T> Optional<T> delete(String url, String errorMessage) {
        return performRequest(url, HttpMethod.DELETE, Arrays.asList(HttpStatus.NO_CONTENT.code(), HttpStatus.OK.code()), errorMessage, null);
    }

    private <T> Optional<T> performRequest(String url, HttpMethod method, List<Integer> okResultCodes, String errorMessage, String jsonBody) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            HttpResponse response = statusPageApi.apiCall(url, method, headers, null, jsonBody);
            if (okResultCodes.contains(response.status().code())) {
                Object result = gson.fromJson(response.getBodyAsString(), clazz);
                return (Optional<T>) Optional.ofNullable(result);
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new RuntimeException(errorMessage + ": service answered " + response.status().code() + " (" + response.status().message() + ")");
                }
            }
        } catch (JsonSyntaxException e) {
            if (statusPageApi.bridgeErrors()) {
                throw new JsonParseException(e.getMessage()).setCause(e);
            } else {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (statusPageApi.bridgeErrors()) {
                throw new ServiceCallException(e.getMessage()).setCause(e);
            } else {
                e.printStackTrace();
            }
        }
        return Optional.empty();
    }

}

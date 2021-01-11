package ws.slink.statuspage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import ws.slink.statuspage.error.JsonParseException;
import ws.slink.statuspage.error.NoDataFoundException;
import ws.slink.statuspage.error.ServiceCallException;
import ws.slink.statuspage.type.HttpMethod;
import ws.slink.statuspage.type.HttpStatus;

import java.net.http.HttpResponse;
import java.util.*;

class StatusPageQuery {

    private StatusPageApi statusPageApi;
    private Class clazz;

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
            HttpResponse<String> response = statusPageApi.apiCall(url, HttpMethod.GET, null, queryParams, null);
            if (response.statusCode() == HttpStatus.OK.value()) {
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz); // constructParametricType
                return objectMapper.readValue(response.body(), type);
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new NoDataFoundException(notFoundMessage);
                }
            }
        } catch (NoDataFoundException e) {
            throw e;
        } catch (JsonProcessingException e) {
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
        return performRequest(url, HttpMethod.GET, Arrays.asList(HttpStatus.OK), notFoundMessage, null);
    }

    <T> Optional<T> post(String url, String jsonBody) {
        return post(url, "", jsonBody);
    }
    <T> Optional<T> post(String url, String errorMessage, String jsonBody) {
        return performRequest(url, HttpMethod.POST, Arrays.asList(HttpStatus.CREATED), errorMessage, jsonBody);
    }

    <T> Optional<T> put(String url, String jsonBody) {
        return put(url, "", jsonBody);
    }
    <T> Optional<T> put(String url, String errorMessage, String jsonBody) {
        return performRequest(url, HttpMethod.PUT, Arrays.asList(HttpStatus.OK), errorMessage, jsonBody);
    }

    <T> Optional<T> delete(String url) {
        return delete(url, "");
    }
    <T> Optional<T> delete(String url, String errorMessage) {
        return performRequest(url, HttpMethod.DELETE, Arrays.asList(HttpStatus.NO_CONTENT, HttpStatus.OK), errorMessage, null);
    }

    private <T> Optional<T> performRequest(String url, HttpMethod method, List<HttpStatus> okResultCodes, String errorMessage, String jsonBody) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            HttpResponse<String> response = statusPageApi.apiCall(url, method, headers, null, jsonBody);
            if (okResultCodes.contains(HttpStatus.of(response.statusCode()))) {
//                System.out.println("---> " + response.body());
                if (null == response.body() || response.body().isBlank())
                    return Optional.empty();
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructType(clazz); // constructParametricType
                return Optional.ofNullable(objectMapper.readValue(response.body(), type));
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new RuntimeException(errorMessage + ": service answered " + response.statusCode() /*+ " (" + response.getStatusText() + ")"*/);
                }
            }
        } catch (JsonProcessingException e) {
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
        return Optional.empty();
    }

}

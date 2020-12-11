package ws.slink.statuspage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.apache.http.HttpStatus;
import ws.slink.statuspage.error.JsonParseException;
import ws.slink.statuspage.error.NoDataFoundException;
import ws.slink.statuspage.error.ServiceCallException;
import ws.slink.statuspage.type.HttpMethod;

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
            HttpResponse<? extends Object> response = statusPageApi.apiCall(url, HttpMethod.GET, null, queryParams, null);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonNode node = (JsonNode) response.getBody();
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz); // constructParametricType
                return objectMapper.readValue(node.toString(), type);
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
        return performRequest(url, HttpMethod.GET, Arrays.asList(HttpStatus.SC_OK), notFoundMessage, null);
    }

    <T> Optional<T> post(String url, String jsonBody) {
        return post(url, "", jsonBody);
    }
    <T> Optional<T> post(String url, String errorMessage, String jsonBody) {
        return performRequest(url, HttpMethod.POST, Arrays.asList(HttpStatus.SC_CREATED), errorMessage, jsonBody);
    }

    <T> Optional<T> put(String url, String jsonBody) {
        return put(url, "", jsonBody);
    }
    <T> Optional<T> put(String url, String errorMessage, String jsonBody) {
        return performRequest(url, HttpMethod.PUT, Arrays.asList(HttpStatus.SC_OK), errorMessage, jsonBody);
    }

    <T> Optional<T> delete(String url) {
        return delete(url, "");
    }
    <T> Optional<T> delete(String url, String errorMessage) {
        return performRequest(url, HttpMethod.DELETE, Arrays.asList(HttpStatus.SC_NO_CONTENT, HttpStatus.SC_OK), errorMessage, null);
    }

    private <T> Optional<T> performRequest(String url, HttpMethod method, List<Integer> okResultCodes, String errorMessage, String jsonBody) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            HttpResponse<? extends Object> response = statusPageApi.apiCall(url, method, headers, null, jsonBody);
            if (okResultCodes.contains(response.getStatus())) {
                JsonNode node = (JsonNode) response.getBody();
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructType(clazz); // constructParametricType
                return Optional.ofNullable(objectMapper.readValue(node.toString(), type));
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new RuntimeException(errorMessage + ": service answered " + response.getStatus() + " (" + response.getStatusText() + ")");
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




//                ObjectMapper objectMapper = new ObjectMapper();
//                JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, T);
//                List<T> result = new ArrayList<>();
//                StreamSupport.stream(node.getArray().spliterator(), false)
//                    .forEach(result.add(new ObjectMapper().readValue(node.toString(), new TypeReference<T>(){})));
//Type listType = new TypeToken<ArrayList<T>>() {}.getType();
//                return Arrays.asList(new ObjectMapper().readValue(node.toString(), Object[].class))
//                        .stream()
//                        .map(v -> (T)v)
//                        .collect(Collectors.toList())
//                        ;=


        /*
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            HttpResponse<? extends Object> response = statusPageApi.apiCall(url, HttpMethod.PUT, headers, null, jsonBody);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonNode node = (JsonNode) response.getBody();
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructType(clazz); // constructParametricType
                return Optional.ofNullable(objectMapper.readValue(node.toString(), type));
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new RuntimeException("could not update object - service answered " + response.getStatus());
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
        */

        /*
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            HttpResponse<? extends Object> response = statusPageApi.apiCall(url, HttpMethod.POST, headers, null, jsonBody);
            if (response.getStatus() == HttpStatus.SC_CREATED) {
                JsonNode node = (JsonNode) response.getBody();
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructType(clazz); // constructParametricType
                return Optional.ofNullable(objectMapper.readValue(node.toString(), type));
            } else {
                if (statusPageApi.bridgeErrors()) {
                    throw new RuntimeException(errorOccurredMessage + ": service answered " + response.getStatus());
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
        */

        /*
        try {
            HttpResponse<? extends Object> response = statusPageApi.apiCall(url, HttpMethod.GET, null, null, null);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonNode node = (JsonNode) response.getBody();
                ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
                JavaType type = objectMapper.getTypeFactory().constructType(clazz); // constructParametricType
                return Optional.ofNullable(objectMapper.readValue(node.toString(), type));
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
        return Optional.empty();
        */

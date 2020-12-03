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
                ObjectMapper objectMapper = new ObjectMapper();
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
            e.printStackTrace();
            if (statusPageApi.bridgeErrors()) {
                throw new JsonParseException(e.getClass().getSimpleName() + " : " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (statusPageApi.bridgeErrors()) {
                throw new ServiceCallException(e.getClass().getSimpleName() + " : " + e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    <T> Optional<T> get(String url) {
        return get(url, "");
    }
    <T> Optional<T> get(String url, String notFoundMessage) {
        try {
            HttpResponse<? extends Object> response = statusPageApi.apiCall(url, HttpMethod.GET, null, null, null);
            if (response.getStatus() == HttpStatus.SC_OK) {
                JsonNode node = (JsonNode) response.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
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
            e.printStackTrace();
            if (statusPageApi.bridgeErrors()) {
                throw new JsonParseException(e.getClass().getSimpleName() + " : " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (statusPageApi.bridgeErrors()) {
                throw new ServiceCallException(e.getClass().getSimpleName() + " : " + e.getMessage());
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

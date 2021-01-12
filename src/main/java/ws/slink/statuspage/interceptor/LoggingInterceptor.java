package ws.slink.statuspage.interceptor;

import kong.unirest.*;

public class LoggingInterceptor implements Interceptor {

    private static class LoggingInterceptorSingleton {
        private static final LoggingInterceptor INSTANCE = new LoggingInterceptor();
    }
    public static LoggingInterceptor instance () {
        return LoggingInterceptorSingleton.INSTANCE;
    }

    @Override
    public void onRequest(HttpRequest<?> request, Config config) {
//        System.out.println("statuspage-api request : " + request.getHttpMethod() + " " + request.getUrl());
    }
    @Override
    public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
        System.out.println("statuspage-api response: " + request.getHttpMethod() + " " + request.getUrl() + " " + response.getStatus() + " " + response.getStatusText());
    }

}

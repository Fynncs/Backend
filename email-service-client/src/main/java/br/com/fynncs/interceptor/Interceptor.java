package br.com.fynncs.interceptor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class Interceptor implements ClientHttpRequestInterceptor {

    private final HttpHeaders headers;
    public Interceptor(HttpHeaders headers) {
        this.headers = headers;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if(headers != null){
            request.getHeaders().putAll(headers);
        }
        return execution.execute(request, body);
    }
}

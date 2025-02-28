package br.com.fynncs.service;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.interceptor.Interceptor;
import br.com.fynncs.resource.ResourcePath;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class AuthService {
    private final RestTemplate restTemplate;
    private final String PATH;

    public AuthService(HttpHeaders headers, ConnectionProvider provider) throws Exception {
        this.restTemplate = new RestTemplate();
        this.restTemplate.setInterceptors(Collections.singletonList(new Interceptor(headers)));
        this.PATH = ResourcePath.path(provider);
    }

    public confirm(HttpServletReq)
}

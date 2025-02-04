package br.com.fynncs.service;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.interceptor.Interceptor;
import br.com.fynncs.record.User;
import br.com.fynncs.resource.ResourcePath;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class EmailService {

    private final RestTemplate restTemplate;
    private final String PATH;

    public EmailService(HttpHeaders headers, ConnectionProvider provider) throws Exception {
        restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(new Interceptor(headers)));
        PATH = ResourcePath.path(provider);
    }

    public String sendResetPassword(User user) {
        return restTemplate.postForObject(PATH + "/sendResetPassword", user, String.class);
    }
    public String sendConfirmEmail(User user) {
        return restTemplate.postForObject(PATH + "/sendConfirmEmail", user, String.class);
    }
    public String confirm(String id) {
        return restTemplate.getForObject(PATH + "/confirm/" + id, String.class);
    }
}

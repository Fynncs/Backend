package br.com.fynncs.service.fynncs;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.interceptor.Interceptor;
import br.com.fynncs.record.Client;
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

    public String sendResetPassword(Client client){
        return restTemplate.postForObject(PATH + "/fynncs/sendResetPassword", client, String.class);
    }
}

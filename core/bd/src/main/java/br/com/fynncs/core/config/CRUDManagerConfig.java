package br.com.fynncs.core.config;

import br.com.fynncs.core.connection.CRUDManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CRUDManagerConfig {

    @Bean
    public CRUDManager crudManager() {
        return new CRUDManager();
    }

}

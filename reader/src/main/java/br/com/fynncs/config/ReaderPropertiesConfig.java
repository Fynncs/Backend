package br.com.fynncs.config;

import br.com.fynncs.services.ReaderProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReaderPropertiesConfig {

    @Bean
    public ReaderProperties readerProperties() {
        return new ReaderProperties();
    }
}

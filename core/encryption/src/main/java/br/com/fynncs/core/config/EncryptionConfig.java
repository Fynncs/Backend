package br.com.fynncs.core.config;

import br.com.fynncs.core.Encryption;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EncryptionConfig {

    @Bean
    public Encryption encryption() {
        return new Encryption();
    }
}

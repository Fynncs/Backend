package br.com.fynncs.security.config;

import br.com.fynncs.config.ReaderEncryptedPropertiesConfig;
import br.com.fynncs.security.service.Security;
import br.com.fynncs.security.token.TokenGoogle;
import br.com.fynncs.security.token.TokenSecurity;
import br.com.fynncs.services.ReaderEncryptedProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:security.properties")
@Import(ReaderEncryptedPropertiesConfig.class)
public class SecurityConfig {
    @Autowired
    private ReaderEncryptedProperties readerEncryptedProperties;

    @Bean
    public Security security() {
        return new Security();
    }

    @Bean
    public TokenSecurity tokenSecurity() throws Exception {
        return new TokenSecurity(readerEncryptedProperties);
    }

    @Bean
    public TokenGoogle tokenGoogle() {
        return new TokenGoogle();
    }
}

package br.com.fynncs.config;

import br.com.fynncs.core.config.EncryptionConfig;
import br.com.fynncs.services.ReaderEncryptedProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(EncryptionConfig.class)
public class ReaderEncryptedPropertiesConfig {
    @Bean
    public ReaderEncryptedProperties readerEncryptedProperties() {
        return new ReaderEncryptedProperties();
    }
}

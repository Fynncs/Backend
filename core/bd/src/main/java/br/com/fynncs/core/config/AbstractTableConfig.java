package br.com.fynncs.core.config;

import br.com.fynncs.core.comum.AbstractTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AbstractTableConfig {

    @Bean
    public AbstractTable abstractTable() {
        return new AbstractTable();
    }

}

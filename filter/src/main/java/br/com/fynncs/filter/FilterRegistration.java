package br.com.fynncs.filter;

import br.com.fynncs.security.config.SecurityConfig;
import br.com.fynncs.security.service.Security;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SecurityConfig.class)
public class FilterRegistration {

    @Autowired
    private Security security;

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> filterRegister() {
        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AuthenticationFilter(security));
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setIgnoreRegistrationFailure(false);
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        return registrationBean;
    }

}

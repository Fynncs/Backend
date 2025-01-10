package br.com.fynncs.security;


import br.com.fynncs.security.config.SecurityConfig;
import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.service.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SecurityTest {

    @Autowired
    private Security security;

    private void initializer() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SecurityConfig.class);
        security = context.getBean(Security.class);
    }


    @Test
    public void createTokenTest() throws Exception {
        initializer();
        Assertions.assertNotNull(security.createToken(new Authentication() {
            {
                setIdentifier("Mike");
                setConnectionProvider("POSTGRES");
                setDataBaseName("usuario");
            }
        }, 100));
    }

    @Test
    public void validateTokenTest() throws Exception {
        initializer();
        String token = security.createToken(new Authentication() {
            {
                setIdentifier("Mike");
                setConnectionProvider("POSTGRES");
                setDataBaseName("usuario");
            }
        }, 100);
        Assertions.assertEquals("Mike", security.validateToken(token).getIdentifier());
    }
}

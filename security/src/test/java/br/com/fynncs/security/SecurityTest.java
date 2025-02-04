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
        String token = security.createToken(new Authentication() {
            {
                setId("Mike");
                setConnectionProvider("POSTGRES");
                setDataBaseName("fynncs");
                setSystem("fynncs");
            }
        }, 10000000);
        System.out.println(token);
        Assertions.assertNotNull(token);
    }

    @Test
    public void validateTokenTest() throws Exception {
        initializer();
        String token = security.createToken(new Authentication() {
            {
                setId("Mike");
                setConnectionProvider("POSTGRES");
                setDataBaseName("usuario");
            }
        }, 100);
        Assertions.assertEquals("Mike", security.validateToken(token).getId());
    }
}

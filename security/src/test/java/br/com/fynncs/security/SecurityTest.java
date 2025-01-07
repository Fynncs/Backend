package br.com.fynncs.security;


import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.service.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecurityTest {

    private Security security = new Security();


    @Test
    public void createTokenTest() throws Exception {
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

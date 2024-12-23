package br.com.fynncs.security;


import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.service.Security;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

public class SecurityTest {

    @Autowired
    private Security security = new Security();

    @Test
    public void createTokenTest() throws Exception {
        Assertions.assertNotNull(security.createToken("Mike", 100));
    }

    @Test
    public void validateTokenTest() throws Exception {
        String token = security.createToken("Mike", 100);
        Assertions.assertEquals("Mike", security.validateToken(token).getIdentifier());
    }
}

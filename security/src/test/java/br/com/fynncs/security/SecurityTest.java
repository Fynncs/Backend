package br.com.fynncs.security;


import br.com.fynncs.core.Encryption;
import br.com.fynncs.security.reader.properties.ReaderProperties;
import br.com.fynncs.security.service.Security;
import br.com.fynncs.security.token.TokenSecurity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SecurityTest {


    private final Security security;

    public SecurityTest() throws Exception {
        this.security = new Security(new TokenSecurity(new ReaderProperties(new Encryption())));
    }


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

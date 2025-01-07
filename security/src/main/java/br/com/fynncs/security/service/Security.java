package br.com.fynncs.security.service;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.token.TokenSecurity;
import org.springframework.stereotype.Service;

@Service
public class Security {

    private final TokenSecurity tokenSecurity;

    public Security() {
        try {
            this.tokenSecurity = new TokenSecurity();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String createToken(Authentication authentication, Integer timeToken) {
        return tokenSecurity.createToken(authentication, timeToken);
    }

    public Authentication validateToken(String token) throws Exception {
        return tokenSecurity.validateToken(token);
    }
}

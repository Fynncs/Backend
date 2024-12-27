package br.com.fynncs.security.service;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.token.TokenSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Security {

    private final TokenSecurity tokenSecurity;

    @Autowired
    public Security(TokenSecurity tokenSecurity) {
        this.tokenSecurity = tokenSecurity;
    }

    public String createToken(String id, Integer timeToken) {
        Authentication authentication = new Authentication(id);
        return tokenSecurity.createToken(authentication, timeToken);
    }

    public Authentication validateToken(String token) throws Exception {
        return tokenSecurity.validateToken(token);
    }
}

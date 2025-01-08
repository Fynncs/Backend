package br.com.fynncs.security.service;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.token.TokenSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Security {


    @Autowired
    private TokenSecurity tokenSecurity;

    public Security() {
    }


    public String createToken(Authentication authentication, Integer timeToken) {
        return tokenSecurity.createToken(authentication, timeToken);
    }

    public Authentication validateToken(String token) throws Exception {
        return tokenSecurity.validateToken(token);
    }
}

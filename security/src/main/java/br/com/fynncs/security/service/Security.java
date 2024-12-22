package br.com.fynncs.security.service;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.token.TokenSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Security {

    @Autowired
    private TokenSecurity tokenSecurity;

    public String createToken(String id, Integer timeToken) throws Exception {
        Authentication authentication = new Authentication(id);
        return tokenSecurity.createToken(authentication, timeToken);
    }

    public Authentication validateToken(String token) throws Exception {
        return tokenSecurity.validateToken(token);
    }
}

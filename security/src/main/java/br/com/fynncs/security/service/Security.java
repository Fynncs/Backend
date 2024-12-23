package br.com.fynncs.security.service;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.token.TokenSecurity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Security {

    private TokenSecurity tokenSecurity;

    public Security(){
        try{
            tokenSecurity = new TokenSecurity();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String createToken(String id, Integer timeToken) throws Exception {
        Authentication authentication = new Authentication(id);
        return tokenSecurity.createToken(authentication, timeToken);
    }

    public Authentication validateToken(String token) throws Exception {
        return tokenSecurity.validateToken(token);
    }
}

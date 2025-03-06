package br.com.fynncs.security.token;

import br.com.fynncs.security.model.Authentication;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import jakarta.ws.rs.NotAuthorizedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class TokenGoogle {

    private final String CLIENT_ID = "";

    public Authentication validateToken(String tokenID) throws GeneralSecurityException, IOException {
//        GoogleIdTokenVerifier verifier =
//                new GoogleIdTokenVerifier.Builder(
//                        new NetHttpTransport(),
//                        new JacksonFactory())
//                        .setAudience(Collections.singletonList(CLIENT_ID))
//                .build();
//        GoogleIdToken verified = verifier.verify(tokenID);
//        if(verified == null){
//            throw new NotAuthorizedException("Invalid Token!", this);
//        }
//        GoogleIdToken.Payload payload = verified.getPayload();
        Authentication authentication = new Authentication();
//        authentication.setEmail(payload.getEmail());
        return authentication;
    }
}

package br.com.fynncs.security.token;

import br.com.fynncs.security.model.Authentication;
import br.com.fynncs.security.reader.properties.ReaderProperties;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.ByteUtil;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Properties;

@Service
public class TokenSecurity {

    private final AesKey privateKey;
    private final AesKey publicKey;
    private final ReaderProperties properties;
    private final String FILE_NAME = "application.properties";

    @Autowired
    public TokenSecurity(ReaderProperties properties) throws Exception {
        this.properties = properties;
        properties.read(FILE_NAME);
        if(!createProperties()){
            throw new Exception("Create Properties error!");
        }
        privateKey = new AesKey(properties.getSpecificPropertieDecrypt("privateKey").getBytes());
        publicKey = new AesKey(properties.getSpecificPropertieDecrypt("publicKey").getBytes());
    }


    public String createToken(Authentication authentication) {
        return createToken(authentication, 525600);
    }

    public String createToken(Authentication authentication, Integer timeTokenExpiration) {
        try {
            JwtClaims claims = new JwtClaims();
            claims.setIssuer("fynncs");
            if (timeTokenExpiration == null) {
                timeTokenExpiration = (1000 * 60 * 60 * 3);
            }
            claims.setExpirationTimeMinutesInTheFuture(timeTokenExpiration);
            claims.setGeneratedJwtId();
            claims.setIssuedAtToNow();
            //claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
            claims.setSubject("authentication");
            claims.setClaim("identifier", authentication.getIdentifier());
            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(privateKey);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
            String jwt = jws.getCompactSerialization();
            JsonWebEncryption jwe = new JsonWebEncryption();
            jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256GCMKW);
            String encAlg = ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256;
            jwe.setEncryptionMethodHeaderParameter(encAlg);
            jwe.setKey(publicKey);
            jwe.setContentTypeHeaderValue("JWT");
            jwe.setPayload(jwt);
            jwt = "Bearer ".concat(jwe.getCompactSerialization());
            return jwt;
        } catch (JoseException e) {
            throw new IllegalStateException(e);
        }
    }

    public Authentication validateToken(String token) throws Exception {
        AlgorithmConstraints jwsAlgConstraints
                = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT,
                AlgorithmIdentifiers.HMAC_SHA256);
        AlgorithmConstraints jweAlgConstraints
                = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.PERMIT,
                KeyManagementAlgorithmIdentifiers.A256GCMKW);
        AlgorithmConstraints jweEncConstraints = new AlgorithmConstraints(
                AlgorithmConstraints.ConstraintType.PERMIT,
                ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);

        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                //.setRequireExpirationTime() // the JWT must have an expiration time
                .setMaxFutureValidityInMinutes((1000 * 60 * 60 * 3)) // but the  expiration time can't be too crazy
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer("fynncs") // whom the JWT needs to have been issued by
                //.setExpectedAudience("receiver") // to whom the JWT is intended for
                .setVerificationKey(privateKey) // verify the signature with the sender's public key
                .setJwsAlgorithmConstraints(jwsAlgConstraints) // limits the acceptable signature algorithm(s)
                .setJweAlgorithmConstraints(jweAlgConstraints) // limits acceptable encryption key establishment algorithm(s)
                .setJweContentEncryptionAlgorithmConstraints(jweEncConstraints)
                .setDecryptionKey(publicKey)// limits acceptable content encryption algorithm(s)
                .build(); // create the JwtConsumer instance
        try {
            token = token.substring(token.indexOf(" ") + 1);
            JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
            Authentication authentication = new Authentication();
            authentication.setIdentifier(jwtClaims.getClaimValue("identifier", String.class));
            authentication.setToken(token);
            return authentication;
        } catch (InvalidJwtException e) {
            throw new Exception("Invalid Token", e);
        }
    }

    private String generateKey() {
        return generateKeyRecursive(256, 240, "", null);
    }

    private String ascii(int value) {
        return String.valueOf((char) value);
    }

    private Boolean createProperties() throws Exception {
        if(properties.getProperties() == null
                || properties.getSpecificPropertie("privateKey") == null
                || properties.getSpecificPropertie("publicKey") == null
                || new Date().getTime() > new Date(Long.parseLong(
                        properties.getSpecificPropertieDecrypt("lastModified"))).getTime() + (1000L * 60 * 60 * 24 * 30)){
            properties.setProperties(new Properties());
            properties.addPropertie("publicKey", generateKey());
            properties.addPropertie("privateKey", generateKey());
            properties.addPropertie("lastModified", String.valueOf(new Date().getTime()));
            return properties.save(FILE_NAME);
        }
        return Boolean.TRUE;
    }

    private String generateKeyRecursive(Integer maxBit, Integer minBit, String key, String keyFinal) {
        if (ByteUtil.bitLength(key.getBytes().length) == maxBit) {
            keyFinal = key;
            return keyFinal;
        }
        if (ByteUtil.bitLength(key.getBytes().length) > maxBit) {
            return key;
        }
        SecureRandom random = new SecureRandom();
        while (keyFinal == null) {
            key += ascii(33 + random.nextInt(93));
            key = generateKeyRecursive(maxBit, minBit, key, keyFinal);
            if (ByteUtil.bitLength(key.getBytes().length) == maxBit) {
                keyFinal = key;
                break;
            }
            key = key.substring(key.length() - 1);
        }
        return keyFinal;
    }
}

package br.com.fynncs.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class Authentication implements Serializable {

    private String identifier;
    private String token;

    public Authentication() {
    }

    public Authentication(String identifier) {
        this.identifier = identifier;
    }

    public Authentication(String identifier, String token) {
        this.identifier = identifier;
        this.token = token;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String serializer() {
        try {
            ObjectMapper objectMapper =
                    new ObjectMapper().
                            setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public static Authentication deserialize(String text) {
        try {
            ObjectMapper objectMapper = new ObjectMapper().
                    setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper.readValue(text, Authentication.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }
}

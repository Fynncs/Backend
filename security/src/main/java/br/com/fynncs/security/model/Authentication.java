package br.com.fynncs.security.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class Authentication implements Serializable {

    private String id;
    private String dataBaseName;
    private String connectionProvider;
    private String token;
    private String email;
    private String system;

    public Authentication() {
    }

    public Authentication(String id) {
        this.id = id;
    }

    public Authentication(String id, String token) {
        this.id = id;
        this.token = token;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public void setDataBaseName(String dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public String getConnectionProvider() {
        return connectionProvider;
    }

    public void setConnectionProvider(String connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
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
}

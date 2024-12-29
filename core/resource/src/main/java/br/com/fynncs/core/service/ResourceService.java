package br.com.fynncs.core.service;

import br.com.fynncs.core.dao.CreateConnection;
import br.com.fynncs.core.dao.IResourceConnection;
import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.model.Resource;
import br.com.fynncs.services.ReaderEncryptedProperties;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    private IResourceConnection resourceConnection;

    public ResourceService(ConnectionProvider provider) throws Exception {
        inicializer(getUrlConnection(provider), provider);
    }

    public ResourceService(String urlConnection, ConnectionProvider provider) throws Exception {
        if(urlConnection == null || urlConnection.isBlank()){
            urlConnection = getUrlConnection(provider);
        }
        inicializer(urlConnection, provider);
    }

    private void inicializer(String urlConnection, ConnectionProvider provider) throws Exception {
        this.resourceConnection = CreateConnection.createResourceConnection(urlConnection, provider);
    }

    private String getUrlConnection(ConnectionProvider provider) throws Exception {
        switch (provider){
            case POSTGRES -> {
                ReaderEncryptedProperties properties = new ReaderEncryptedProperties();
                properties.read("src/main/resources/application.properties");
                return properties.getSpecificPropertiesDecrypt("data-base.postgres.url.connection");
            }
            default -> {
                return null;
            }
        }
    }

    public Resource findResourceById(String id, ResourceType type) throws SQLException {
        return resourceConnection.findResourceById(id, type);
    }

    public List<Resource> findListByType(ResourceType type) throws SQLException {
        return resourceConnection.findListByType(Optional.of(type));
    }
}

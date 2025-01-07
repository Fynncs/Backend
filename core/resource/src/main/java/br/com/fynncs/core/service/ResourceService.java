package br.com.fynncs.core.service;

import br.com.fynncs.core.dao.CreateConnection;
import br.com.fynncs.core.dao.IResourceConnection;
import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.model.Resource;
import br.com.fynncs.services.ReaderEncryptedProperties;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class ResourceService {

    private static IResourceConnection resourceConnection;
    private static ReaderEncryptedProperties properties = null;

    private static void initializer(String urlConnection, ConnectionProvider provider) throws Exception {
        initializerProperties();
        resourceConnection = CreateConnection.createResourceConnection(urlConnection, provider);
    }

    private static void initializerProperties() {
        if (properties == null) {
            properties = new ReaderEncryptedProperties();
        }
    }

    private static String getUrlConnection(ConnectionProvider provider) throws Exception {
        switch (provider) {
            case POSTGRES -> {
                properties.read(properties.path("application.properties", ResourceService.class));
                return properties.getSpecificPropertiesDecrypt("data-base.postgres.url.connection");
            }
            default -> {
                return null;
            }
        }
    }

    private static String getStandardProvider() throws Exception {
        properties.read(properties.path("application.properties", ResourceService.class));
        return properties.getSpecificPropertiesDecrypt("provider.standard");
    }

    public static String getEnvironment() throws Exception {
        initializerProperties();
        properties.read(properties.path("application.properties", ResourceService.class));
        return properties.getSpecificPropertiesDecrypt("environment");
    }

    public static Resource findResourceById(ConnectionProvider provider, String id, ResourceType type) throws Exception {
        initializer(getUrlConnection(provider), provider);
        return resourceConnection.findResourceById(id, type);
    }

    public static Resource findResourceById(String id, ResourceType type) throws Exception {
        initializerProperties();
        ConnectionProvider provider = ConnectionProvider.valueOf(getStandardProvider());
        initializer(getUrlConnection(provider), provider);
        return resourceConnection.findResourceById(id, type);
    }

    public static Resource findStandardDataBaseConnectionResource(ConnectionProvider provider) throws Exception {
        initializer(getUrlConnection(provider), provider);
        return resourceConnection.findResourceById("STANDARD", ResourceType.DATABASECONNECTION);
    }

    public static Resource findStandardDataBaseConnectionResource() throws Exception {
        initializerProperties();
        ConnectionProvider provider = ConnectionProvider.valueOf(getStandardProvider());
        initializer(getUrlConnection(provider), provider);
        return resourceConnection.findResourceById("STANDARD", ResourceType.DATABASECONNECTION);
    }

    public static List<Resource> findListByType(ConnectionProvider provider, ResourceType type) throws Exception {
        initializer(getUrlConnection(provider), provider);
        return resourceConnection.findListByType(Optional.of(type));
    }

    public static List<Resource> findListByType(ResourceType type) throws Exception {
        initializerProperties();
        ConnectionProvider provider = ConnectionProvider.valueOf(getStandardProvider());
        initializer(getUrlConnection(provider), provider);
        return resourceConnection.findListByType(Optional.of(type));
    }

    public static Resource findResourceById(String urlConnection, ConnectionProvider provider, String id, ResourceType type) throws Exception {
        if (urlConnection == null || urlConnection.isBlank()) {
            urlConnection = getUrlConnection(provider);
        }
        initializer(urlConnection, provider);
        return resourceConnection.findResourceById(id, type);
    }

    public static List<Resource> findListByType(String urlConnection, ConnectionProvider provider, ResourceType type) throws Exception {
        if (urlConnection == null || urlConnection.isBlank()) {
            urlConnection = getUrlConnection(provider);
        }
        initializer(urlConnection, provider);
        return resourceConnection.findListByType(Optional.of(type));
    }
}

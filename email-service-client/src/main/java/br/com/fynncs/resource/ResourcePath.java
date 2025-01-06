package br.com.fynncs.resource;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.service.ResourceService;
import br.com.fynncs.services.ReaderProperties;

public abstract class ResourcePath {

    public static String path(ConnectionProvider provider) throws Exception {
        ReaderProperties properties = new ReaderProperties();
        properties.read(properties.path("applications.properties", ResourceService.class));
        return ResourceService.findResourceById(provider, "EMAILSERVICE", ResourceType.URLWEB).getInfo()
                .get(properties.getSpecificProperties("environment"));
    }

}

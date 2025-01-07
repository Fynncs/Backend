package br.com.fynncs.resource;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.service.ResourceService;

public abstract class ResourcePath {

    public static String path(ConnectionProvider provider) throws Exception {
        return ResourceService.findResourceById(provider, "EMAILSERVICE", ResourceType.URLWEB).getInfo()
                .get(ResourceService.getEnvironment());
    }

}

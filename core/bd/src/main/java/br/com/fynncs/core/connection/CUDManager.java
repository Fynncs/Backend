package br.com.fynncs.core.connection;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.interfaces.ICUDManager;
import br.com.fynncs.core.model.Resource;

class CUDManager extends Connection implements ICUDManager {
    public CUDManager() {
    }

    public CUDManager(Resource resource, ConnectionProvider provider) throws Exception {
        super(resource, provider);
    }

    @Override
    public int persist(Object value){
        return 0;
    }
}

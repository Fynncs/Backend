package br.com.fynncs.core.dao;

import br.com.fynncs.core.dao.postgres.ResourceConnection;
import br.com.fynncs.core.enums.ConnectionProvider;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public abstract class CreateConnection {

    private static final Map<ConnectionProvider, Boolean> driverInstantiated = new HashMap<>();

    private static void driverInstance(ConnectionProvider provider) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (driverInstantiated.getOrDefault(provider, Boolean.FALSE)) return;
        switch (provider) {
            case POSTGRES -> {
                Class.forName("org.postgresql.Driver").newInstance();
                driverInstantiated.put(provider, Boolean.TRUE);
            }
            default -> throw new InstantiationException("Error when instantiating!");
        }
    }

    public static IResourceConnection createResourceConnection(String urlConnection, ConnectionProvider provider) throws Exception {
        driverInstance(provider);
        switch (provider) {
            case POSTGRES -> {
                return new ResourceConnection(DriverManager.getConnection(urlConnection));
            }
            default -> throw new Exception("Connection to provider not developed");
        }
    }
}

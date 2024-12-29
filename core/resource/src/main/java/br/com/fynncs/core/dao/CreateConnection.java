package br.com.fynncs.core.dao;

import br.com.fynncs.core.dao.postgres.ResourceConnection;
import br.com.fynncs.core.enums.ConnectionProvider;

import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public abstract class CreateConnection {

    private static Map<ConnectionProvider, Boolean> driverInstaced = new HashMap<>();

    private static void driverInstace(ConnectionProvider provider) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (driverInstaced.getOrDefault(provider, Boolean.FALSE)) return;
        switch (provider) {
            case POSTGRES -> {
                Class.forName("org.postgresql.Driver").newInstance();
                driverInstaced.put(provider, Boolean.TRUE);
            }
            default -> throw new InstantiationException("Error when instantiating!");
        }
    }

    public static IResourceConnection createResourceConnection(String urlConnection, ConnectionProvider provider) throws Exception {
        driverInstace(provider);
        switch (provider) {
            case POSTGRES -> {
                return new ResourceConnection(DriverManager.getConnection(urlConnection));
            }
            default -> throw new Exception("Connection to provider not developed");
        }
    }
}

package br.com.fynncs.core.connection;

import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.model.Resource;
import br.com.fynncs.core.service.ResourceService;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

class Connection implements java.sql.Connection {

    private java.sql.Connection connection;
    private ConnectionProvider provider;

    public Connection() {
    }

    public Connection(Resource resource, ConnectionProvider provider) throws Exception {
        driverInstance(provider);
        this.provider = provider;
        connection = DriverManager.getConnection(connectionText(resource.getInfo().get(environment()), null));
    }

    public Connection(Resource resource, ConnectionProvider provider, String dataBase) throws Exception {
        driverInstance(provider);
        this.provider = provider;
        connection = DriverManager.getConnection(connectionText(resource.getInfo().get(environment()), dataBase));
    }

    private static void driverInstance(ConnectionProvider provider) throws Exception {
        switch (provider) {
            case POSTGRES -> {
                Class.forName("org.postgresql.Driver").newInstance();
            }
            default -> throw new InstantiationException("Error when instantiating!");
        }
    }

    public static boolean hasTable(ConnectionProvider provider, Connection connection, String tableName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            return false;
        }

        if (provider == null) {
            provider = ConnectionProvider.POSTGRES;
        }

        boolean exists = false;

        switch (provider) {
            case POSTGRES -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT to_regclass(?)")) {
                    statement.setString(1, tableName);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            resultSet.getString(1);
                            if (!resultSet.wasNull()) {
                                exists = true;
                            }
                        }
                    }
                }
            }
        }
        return exists;
    }

    public static boolean hasColumn(ConnectionProvider provider, Connection connection,
                                    String tableName, String columnName) throws SQLException {
        if (connection == null || connection.isClosed()
                || tableName == null || tableName.trim().isEmpty()
                || columnName == null || columnName.trim().isEmpty()) {
            return false;
        }

        String[] names = tableName.split("\\.");

        String schemaName = null;
        if (names.length > 0) {
            schemaName = names[0];
        }

        if (names.length > 1) {
            tableName = names[1];
        }

        return hasColumn(provider, connection, schemaName, tableName, columnName);
    }

    public static boolean hasColumn(ConnectionProvider provider, Connection connection,
                                    String schemaName, String tableName, String columnName) throws SQLException {
        if (connection == null || connection.isClosed()
                || schemaName == null || schemaName.isBlank()
                || tableName == null || tableName.isBlank()
                || columnName == null || columnName.isBlank()) {
            return false;
        }

        schemaName = schemaName.trim();
        tableName = tableName.trim();
        columnName = columnName.trim();

        if (provider == null) {
            provider = ConnectionProvider.POSTGRES;
        }

        boolean exists = false;

        switch (provider) {
            case POSTGRES -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT column_name"
                                + " FROM information_schema.columns"
                                + " WHERE table_catalog = ?"
                                + " AND table_schema = ?"
                                + " AND table_name = ?"
                                + " AND column_name = ?;")) {
                    statement.setString(1, connection.getCatalog());
                    statement.setString(2, schemaName);
                    statement.setString(3, tableName);
                    statement.setString(4, columnName);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            resultSet.getString(1);
                            if (!resultSet.wasNull()) {
                                exists = true;
                            }
                        }
                    }
                }
            }
        }
        return exists;
    }

    public static boolean hasColumn(ResultSet resultSet, String columnName) throws SQLException {
        try {
            resultSet.findColumn(columnName);
            return true;
        } catch (SQLException ignored) {
        }
        return false;
    }

    private String connectionText(
            String connectionText, String dataBaseName) {
        if (connectionText != null && !connectionText.isEmpty()
                && dataBaseName != null && !dataBaseName.isEmpty()) {
            int pos1 = connectionText.lastIndexOf("/");
            int pos2 = connectionText.lastIndexOf("?");
            if (pos2 == -1) {
                pos2 = connectionText.length();
            }

            if (pos1 != -1 && pos2 != -1 && pos2 > pos1) {
                connectionText = connectionText.substring(0, pos1 + 1)
                        + dataBaseName
                        + connectionText.substring(pos2);
            }
        }
        return connectionText;
    }

    public ConnectionProvider getProvider() {
        return provider;
    }

    public java.sql.Connection getConnection() {
        return connection;
    }

    private String environment() throws Exception {
        return ResourceService.getEnvironment();
    }

    public void startTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    public void commitTransaction() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
            connection.setAutoCommit(true);
        }
    }

    public void revertTransaction() throws SQLException {
        if (connection != null && !connection.isClosed() && !connection.getAutoCommit()) {
            connection.rollback();
            connection.setAutoCommit(true);
        }
    }

    public boolean inTransaction() throws SQLException {
        return !connection.getAutoCommit();
    }

    public boolean hasDataBase(String dataBaseName, ConnectionProvider provider) throws Exception {
        if (connection == null || connection.isClosed()) {
            return false;
        }

        boolean exists = false;

        switch (provider) {
            case POSTGRES -> {
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT 1 from pg_database WHERE datname=?")) {
                    statement.setString(1, dataBaseName);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        int result;
                        if (resultSet.next()) {
                            result = resultSet.getInt(1);
                            if (result == 1) {
                                exists = true;
                            }
                        }
                    }
                }
            }
        }

        return exists;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return connection.createStatement();
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return connection.prepareCall(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return connection.nativeSQL(sql);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException ignored) {
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return connection.isClosed();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return connection.getMetaData();
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return connection.isReadOnly();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        connection.setReadOnly(readOnly);
    }

    @Override
    public String getCatalog() throws SQLException {
        return connection.getCatalog();
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        connection.setCatalog(catalog);
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return connection.getTransactionIsolation();
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        connection.setTransactionIsolation(level);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return connection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        connection.clearWarnings();
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return connection.getTypeMap();
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        connection.setTypeMap(map);
    }

    @Override
    public int getHoldability() throws SQLException {
        return connection.getHoldability();
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        connection.setHoldability(holdability);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return connection.setSavepoint();
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return connection.setSavepoint(name);
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        connection.rollback(savepoint);
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        connection.releaseSavepoint(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return connection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return connection.prepareStatement(sql, columnNames);
    }

    @Override
    public Clob createClob() throws SQLException {
        return connection.createClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        return connection.createBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        return connection.createNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return connection.createSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return connection.isValid(timeout);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        connection.setClientInfo(name, value);
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return connection.getClientInfo(name);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return connection.getClientInfo();
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        connection.setClientInfo(properties);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return connection.createArrayOf(typeName, elements);
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return connection.createStruct(typeName, attributes);
    }

    @Override
    public String getSchema() throws SQLException {
        return connection.getSchema();
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        connection.setSchema(schema);
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        connection.abort(executor);
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        connection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return connection.getNetworkTimeout();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connection.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connection.isWrapperFor(iface);
    }
}

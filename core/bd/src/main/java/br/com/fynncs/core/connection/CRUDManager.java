package br.com.fynncs.core.connection;

import br.com.fynncs.core.comum.AssemblyStatement;
import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.interfaces.ICRUDManager;
import br.com.fynncs.core.interfaces.IRowMapper;
import br.com.fynncs.core.model.Resource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CRUDManager extends CUDManager implements ICRUDManager {

    public CRUDManager() {
    }

    public CRUDManager(Resource resource, ConnectionProvider provider) throws Exception {
        super(resource, provider);
    }

    public CRUDManager(Resource resource, ConnectionProvider provider, String dataBase) throws Exception {
        super(resource, provider, dataBase);
    }

    @Override
    public <T> T queryObject(String textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException {
        try (PreparedStatement statement = prepareStatement(textSQL)) {
            assemblyStatement(statement, verifierObjectToArray(values, textSQL));
            return createObject(statement.executeQuery(), rowMapper);
        }
    }

    @Override
    public <T> T queryObject(StringBuilder textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException {
        return queryObject(textSQL.toString(), values, rowMapper);
    }

    @Override
    public <T> T queryObject(String textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException {
        try (PreparedStatement statement = prepareStatement(textSQL)) {
            assemblyStatement(statement, verifierObjectToArray(value, textSQL));
            return createObject(statement.executeQuery(), rowMapper);
        }
    }

    @Override
    public <T> T queryObject(StringBuilder textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException {
        return queryObject(textSQL.toString(), value, rowMapper);
    }

    @Override
    public <T> List<T> queryList(String textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException {
        try (PreparedStatement statement = prepareStatement(textSQL)) {
            assemblyStatement(statement, verifierObjectToArray(values, textSQL));
            return createList(statement.executeQuery(), rowMapper);
        }
    }

    @Override
    public <T> List<T> queryList(StringBuilder textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException {
        return queryList(textSQL.toString(), values, rowMapper);
    }

    @Override
    public <T> List<T> queryList(String textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException {
        try (PreparedStatement statement = prepareStatement(textSQL)) {
            assemblyStatement(statement, verifierObjectToArray(value, textSQL));
            return createList(statement.executeQuery(), rowMapper);
        }
    }

    @Override
    public <T> List<T> queryList(StringBuilder textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException {
        return queryList(textSQL.toString(), value, rowMapper);
    }

    private void assemblyStatement(PreparedStatement statement, List<Object> values) throws SQLException {
        if (values != null && !values.isEmpty()) {
            int count = 0;
            for (Object value : values) {
                AssemblyStatement.setStatement(value, statement, getConnection(), ++count);
            }
        }
    }

    private List<Object> verifierObjectToArray(Object value, String textSQL) {
        if (value instanceof ArrayList<?>) {
            return values((List<?>) value, textSQL);
        }
        return value != null ? List.of(value) : null;
    }

    private List<Object> values(List<?> values, String textSQL) {
        List<Object> list = new ArrayList<>();
        if (values != null && !values.isEmpty()) {
            if (textSQL.toLowerCase().trim().startsWith("select")) {
                list.addAll(values.stream().filter(Objects::nonNull).toList());
            } else {
                list = new ArrayList<>(values);
            }
        }
        return list;
    }

    private <T> T createObject(ResultSet resultSet, IRowMapper<T> rowMapper) throws SQLException {
        return resultSet.next() ? rowMapper.createObject(resultSet) : null;
    }

    private <T> List<T> createList(ResultSet resultSet, IRowMapper<T> rowMapper) throws SQLException {
        List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(rowMapper.createObject(resultSet));
        }
        return list;
    }
}

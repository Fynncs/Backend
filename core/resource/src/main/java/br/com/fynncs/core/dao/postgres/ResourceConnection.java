package br.com.fynncs.core.dao.postgres;

import br.com.fynncs.core.dao.IResourceConnection;
import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.model.Resource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResourceConnection implements IResourceConnection {

    private final Connection connection;

    public ResourceConnection(Connection connection) {
        this.connection = connection;
    }

    private StringBuilder createTextSQL(Optional<List<String>> additionalFields) {
        StringBuilder textSQL = new StringBuilder();
        textSQL.append("select id, ");
        textSQL.append("type, ");
        textSQL.append("description, ");
        textSQL.append("info");
        additionalFields.ifPresent(x -> textSQL.append(", ").append(x.parallelStream().collect(Collectors.joining(", "))));
        textSQL.append(" from general.resource rc ");
        return textSQL;
    }


    @Override
    public Resource findResourceById(String id, ResourceType type) throws SQLException {
        StringBuilder textSQL = createTextSQL(Optional.empty());
        textSQL.append(" WHERE rc.id = ? AND rc.type = ?");
        PreparedStatement statement = connection.prepareStatement(textSQL.toString());
        statement.setString(1, id);
        statement.setString(2, type.toString());
        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? createResource(resultSet) : null;
    }

    @Override
    public List<Resource> findListByType(Optional<ResourceType> type) throws SQLException {
        StringBuilder textSQL = createTextSQL(Optional.empty());
        if (type.isPresent()) {
            textSQL.append(" WHERE rc.type = ?");
        }
        PreparedStatement statement = connection.prepareStatement(textSQL.toString());
        if (type.isPresent()) {
            statement.setString(1, type.get().toString());
        }
        return createListResource(statement.executeQuery());
    }

    private List<Resource> createListResource(ResultSet resultSet) throws SQLException {
        List<Resource> resources = new ArrayList<>();
        while (resultSet.next()) {
            resources.add(createResource(resultSet));
        }
        return resources;
    }

    private Resource createResource(ResultSet resultSet) throws SQLException {
        Resource resource = new Resource();
        resource.setDescription(resultSet.getString("description"));
        resource.setId(resultSet.getString("id"));
        resource.setInfo((Map<String, String>) resultSet.getObject("info"));
        switch (resultSet.getString("type")) {
            case "URLWEB" -> resource.setType(ResourceType.URLWEB);
            case "DATABASECONNECTION" -> resource.setType(ResourceType.DATABASECONNECTION);
        }
        return resource;
    }
}

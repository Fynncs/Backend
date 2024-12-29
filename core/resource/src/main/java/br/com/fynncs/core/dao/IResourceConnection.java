package br.com.fynncs.core.dao;

import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.model.Resource;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IResourceConnection {

    Resource findResourceById(String id, ResourceType type) throws SQLException;
    List<Resource> findListByType(Optional<ResourceType> type) throws SQLException;
}

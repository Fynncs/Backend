package br.com.fynncs.core.comum;

 import br.com.fynncs.core.connection.CRUDManager;
import br.com.fynncs.core.enums.ResourceType;
import br.com.fynncs.core.model.Resource;
import br.com.fynncs.core.service.ResourceService;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTable {

    public AbstractTable() {
//        try (Connection conn = ) {   TODO: recuperar a conexão aberta
            String tableName = this.getClass().getSimpleName().toLowerCase();

            if (!tableExists(conn, tableName)) {
                String createTableSQL = generateCreateTableSQL(tableName);
                conn.createStatement().execute(createTableSQL);
            } else {
                // logar ou ignorar caso já exista a tabela no BD
            }

//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private boolean tableExists(Connection conn, String tableName) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, null);
            return tables.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateCreateTableSQL(String tableName) {
        StringBuilder sql = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");

        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                sql.append(column.name()).append(" ").append(column.type()).append(", ");
            }
        }

        sql.setLength(sql.length() - 2);
        sql.append(");");

        return sql.toString();
    }

    private void findAllConnection() throws Exception {
        List<Resource> resources = ResourceService.findListByType(ResourceType.DATABASECONNECTION);
        for(Resource resource: resources){
            try(CRUDManager manager = new CRUDManager(resource, ResourceService.getConnectionProvider(resource))) {
                manager.getProvider();
            }
        }
    }

    private List<String> findAllSchema(CRUDManager manager) throws SQLException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT schema_name ");
        query.append("FROM information_schema.schemata ");
        query.append("where schema_name not in ('public','information_schema','pg_catalog','pg_toast') ");
        List<String> schemasName = new ArrayList<>();
        try(PreparedStatement statement = manager.prepareStatement(query.toString());
            ResultSet resultSet = statement.executeQuery()){
            while (resultSet.next()){
                schemasName.add(resultSet.getString("schema_name"));
            }
        }
        return schemasName;
    }
}

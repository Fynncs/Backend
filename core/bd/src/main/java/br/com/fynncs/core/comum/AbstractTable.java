package br.com.fynncs.core.comum;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

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
}

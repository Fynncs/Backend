package br.com.fynncs.core.comum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public abstract class AssemblyStatement {

    private final static Map<Class<?>, Integer> numberClass = Map.of(
            Object[].class, 0,
            String[].class, 0,
            java.util.ArrayList.class, 1,
            String.class, 2,
            UUID.class, 2);

    public static void setStatement(Object value, PreparedStatement statement, Connection connection, int count) throws SQLException {
        if (value == null) {
            statement.setNull(count, SQLObjectType.type(null));
            return;
        }
        if (numberClass.containsKey(value.getClass())) {
            switch (numberClass.get(value.getClass())) {
                case 0:
                    statement.setObject(count, connection.createArrayOf(SQLObjectType.typeString(((Object[]) value)[0]), (Object[]) value),
                            SQLObjectType.type(value));
                    return;
                case 1:
                    statement.setObject(count, connection.createArrayOf(SQLObjectType.typeString(((ArrayList<?>) value).get(0)),
                                    ((ArrayList<?>) value).toArray()),
                            SQLObjectType.type(value));
                    return;
                case 2:
                    if (value instanceof UUID || Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")
                            .matcher((String) value).matches()) {
                        assert value instanceof String;
                        UUID uuid = UUID.fromString((String) value);
                        statement.setObject(count, uuid, SQLObjectType.type(uuid));
                    } else {
                        statement.setObject(count, value, SQLObjectType.type(value));
                    }
                    return;
            }
        } else if (value instanceof Object[]) {
            statement.setObject(count, connection.createArrayOf(SQLObjectType.typeString(((Object[]) value)[0]), (Object[]) value),
                    SQLObjectType.type(value));
            return;
        }
        statement.setObject(count, value, SQLObjectType.type(value));
    }
}

import java.lang.reflect.Field;

public abstract class AbstractTable implements iTable {

    @Override
    public String createTableSQL() {
        StringBuilder sql = new StringBuilder("CREATE TABLE ");
        sql.append(getClass().getSimpleName().toLowerCase()).append(" (");

        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                String columnName = column.name().isEmpty() ? field.getName() : column.name();
                sql.append(columnName).append(" ").append(column.type()).append(", ");
            }
        }

        sql.setLength(sql.length() - 2);
        sql.append(");");

        return sql.toString();
    }
}
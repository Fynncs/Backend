package br.com.fynncs.core.connection;

import br.com.fynncs.core.ModelState;
import br.com.fynncs.core.comum.AssemblyStatement;
import br.com.fynncs.core.comum.SQLObjectType;
import br.com.fynncs.core.enums.ConnectionProvider;
import br.com.fynncs.core.enums.State;
import br.com.fynncs.core.interfaces.ICUDManager;
import br.com.fynncs.core.model.Resource;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


class CUDManager extends Connection implements ICUDManager {

    private final List<Object> values = new ArrayList<>();

    public CUDManager() {
    }

    public CUDManager(Resource resource, ConnectionProvider provider) throws Exception {
        super(resource, provider);
    }

    public void clearValues() {
        values.clear();
    }

    @Override
    public int persist(Object value) throws SQLException, IllegalAccessException {
        switch (stateModelState(value)) {
            case NEW -> {
                return persist(insertSQL(value));
            }
            case MODIFIED -> {
                return persist(updateSQL(value));
            }
            case DELETED -> {
                return persist(deleteSQL(value));
            }
            default -> {
                return 0;
            }
        }
    }

    private int persist(StringBuilder textSQL) throws SQLException {
        if (textSQL == null) {
            return 0;
        }
        try (PreparedStatement statement = prepareStatement(textSQL.toString())) {
            int cont = 0;
            for (Object value : values) {
                AssemblyStatement.setStatement(value, statement, getConnection(), ++cont);
            }
            clearValues();
            return statement.executeUpdate();
        } catch (SQLException sqle) {
            clearValues();
            throw new SQLException(sqle);
        }
    }

    private StringBuilder insertSQL(Object value) throws SQLException, IllegalAccessException {
        StringBuilder textSQL = new StringBuilder();
        textSQL.append("INSERT INTO ").append(tableName(value.getClass()));
        textSQL.append(" (").append(columnsName(value));
        textSQL.append(") VALUES (").append(createQuery(value)).append(")");
        return textSQL;
    }

    private StringBuilder updateSQL(Object value) throws SQLException, IllegalAccessException {
        StringBuilder textSQL = new StringBuilder();
        textSQL.append("UPDATE ").append(schemaName(value.getClass())).append(tableName(value.getClass())).append(" SET ");
        textSQL.append(updateColumnsName(value));
        if (values == null || values.isEmpty()) {
            return null;
        }
        textSQL.append(createWhere(value, " WHERE "));
        return textSQL;
    }

    private StringBuilder deleteSQL(Object value) throws SQLException, IllegalAccessException {
        StringBuilder textSQL = new StringBuilder();
        textSQL.append("DELETE FROM ").append(schemaName(value.getClass())).append(tableName(value.getClass()));
        textSQL.append(createWhere(value, " WHERE "));
        return textSQL;
    }

    private String schemaName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null || table.schema() == null) {
            return "";
        }
        return table.schema().concat(".");
    }

    private String tableName(Class<?> clazz) throws SQLException {
        Table table = clazz.getAnnotation(Table.class);
        if (table == null || table.name() == null || table.name().trim().isEmpty()) {
            throw new SQLException("Table name can't defined");
        }
        return schemaName(clazz).concat(table.name());
    }

    private StringBuilder columnsName(Object value) throws SQLException, IllegalAccessException {
        return columnsName(value, "");
    }

    private StringBuilder columnsName(Column column, String delimiter, StringBuilder columns) throws SQLException {
        if (column == null) {
            throw new SQLException("Column can't defined");
        }
        if (!column.insertable()) {
            return columns;
        }
        return columns.append(delimiter).append(column.name());
    }

    private StringBuilder columnsName(Object value, String delimiter) throws SQLException, IllegalAccessException {
        StringBuilder columns = new StringBuilder();
        for (Field field : declaredFields(value)) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                continue;
            }
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    columns.append(delimiter).append(joinColumn.name());
                    delimiter = ", ";
                    Field fieldFiltered = binaryShare(filterListColumn(field.getType().getDeclaredFields()), joinColumn.referencedColumnName().hashCode());
                    if (fieldFiltered == null) {
                        continue;
                    }
                    fieldFiltered.setAccessible(true);
                    values.add(cast(fieldFiltered.get(field.get(value)).getClass(), fieldFiltered.get(field.get(value))));
                    field.setAccessible(false);
                    continue;
                }
                field.setAccessible(false);
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            columns = columnsName(column, delimiter, columns);
            delimiter = ", ";
            if (column.insertable()) {
                values.add(cast(field.get(value).getClass(), field.get(value)));
            }
            field.setAccessible(false);
        }
        return columns;
    }

    private StringBuilder createQuery(Object value) {
        StringBuilder query = new StringBuilder();
        String delimiter = "";
        for (Field field : declaredFields(value)) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                continue;
            }
            if (!field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    query.append(delimiter).append("?");
                    delimiter = ", ";
                    continue;
                }
                continue;
            }
            Column Column = field.getAnnotation(Column.class);
            if (!Column.insertable()) {
                continue;
            }
            query.append(delimiter).append("?");
            delimiter = ", ";
        }
        return query;
    }

    private StringBuilder createWhere(Object value, String delimiter) throws IllegalAccessException {
        StringBuilder query = new StringBuilder();
        for (Field field : declaredListFieldsFiltredPK(value, false)) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                continue;
            }
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    Field fieldFiltered = binaryShare(filterListColumn(field.getType().getDeclaredFields()), joinColumn.referencedColumnName().hashCode());
                    if (fieldFiltered == null) {
                        continue;
                    }
                    fieldFiltered.setAccessible(true);
                    values.add(cast(fieldFiltered.get(field.get(value)).getClass(), fieldFiltered.get(field.get(value))));
                    fieldFiltered.setAccessible(false);
                    query.append(delimiter).append(joinColumn.name()).append(" = ? ");
                    delimiter = " AND ";
                    field.setAccessible(false);
                }
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (!column.unique()) {
                continue;
            }
            values.add(cast(field.get(value).getClass(), field.get(value)));
            field.setAccessible(false);
            query.append(delimiter).append(column.name()).append(" = ? ");
            delimiter = " AND ";
        }
        return query;
    }

    private StringBuilder updateColumnsName(Object value) throws SQLException, IllegalAccessException {
        StringBuilder textSQL = new StringBuilder();
        String delimiter = "";
        for (Field field : declaredFieldsFiltrado(value)) {
            if (field.isAnnotationPresent(OneToMany.class)) {
                continue;
            }
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Column.class)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
                    Field fieldFiltred = binaryShare(filterListColumn(field.getType().getDeclaredFields()), joinColumn.referencedColumnName().hashCode());
                    if (fieldFiltred == null) {
                        continue;
                    }
                    fieldFiltred.setAccessible(true);
                    if (!checkerModifiedAttributes(field.get(value), joinColumn.name())) {
                        fieldFiltred.setAccessible(false);
                        continue;
                    }
                    textSQL.append(delimiter).append(joinColumn.name()).append(" = ? ");
                    delimiter = ", ";
                    values.add(cast(fieldFiltred.get(field.get(value)).getClass(), fieldFiltred.get(field.get(value))));
                    fieldFiltred.setAccessible(false);
                }
                field.setAccessible(false);
                continue;
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.updatable() && checkerModifiedAttributes(value, column.name())) {
                textSQL = columnsName(column, delimiter, textSQL).append(" = ?");
                delimiter = ", ";
                values.add(cast(field.get(value).getClass(), field.get(value)));
            }
            field.setAccessible(false);
        }
        return textSQL;
    }

    private Object cast(Class<?> clazz, Object value) {
        if (value == null) {
            return null;
        }
        String simpleName = SQLObjectType.typeString(value);
        if (simpleName.equalsIgnoreCase("timestamp")) {
            return new Timestamp(((Date) value).getTime());
        } else if (simpleName.equalsIgnoreCase("uuid")) {
            return UUID.fromString((String) value);
        }
        return clazz.cast(value);
    }

    private boolean isInstance(Object value, Class<?> clazz) {
        return clazz.isInstance(value);
    }

    private boolean checkState(Object value, String state) {
        if (value instanceof java.util.ArrayList) {
            for (Object valor : (java.util.ArrayList<?>) value) {
                State modelState = stateModelState(valor);
                if (modelState.name().equalsIgnoreCase(state)) {
                    return true;
                }
            }
            return false;
        }
        State modelState = stateModelState(value);
        return modelState.name().equalsIgnoreCase(state);
    }

    private State stateModelState(Object value) {
        ModelState modelState = (ModelState) cast(ModelState.class, value);
        return modelState.getState();
    }

    private boolean checkerModifiedAttributes(Object value, String columnNome) {
        ModelState modelState = (ModelState) cast(ModelState.class, value);
        return modelState.checkerModifiedAttributes(columnNome);
    }

    private List<Field> declaredFields(Object value) {
        return Arrays.stream(value.getClass().getDeclaredFields()).toList();
    }

    private List<Field> declaredFieldsFiltrado(Object value) {
        return declaredFields(value).stream().filter(field -> {
            if (field.isAnnotationPresent(OneToMany.class)) {
                return false;
            }
            if (!field.isAnnotationPresent(Column.class)
                    && field.isAnnotationPresent(JoinColumn.class)) {
                field.setAccessible(true);
                try {
                    if (field.get(value) == null
                            || !checkState(field.get(value), State.MODIFIED.name())) {
                        field.setAccessible(false);
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                field.setAccessible(false);
                return true;
            }
            Column column = field.getAnnotation(Column.class);
            if (column != null && column.updatable()) {
                field.setAccessible(true);
                if (!checkerModifiedAttributes(value, column.name())) {
                    return false;
                }
                field.setAccessible(false);
                return true;
            }
            return true;
        }).collect(Collectors.toList());
    }

    private List<Field> declaredListFieldsFiltredPK(Object value, boolean hasAnnotationColumn) {
        List<Field> fields = new ArrayList<>();
        if (!hasAnnotationColumn) {
            for (Field field : declaredFields(value)) {
                if (field.isAnnotationPresent(OneToMany.class)) {
                    continue;
                }
                if (!field.isAnnotationPresent(Column.class)) {
                    fields.add(field);
                }
            }
        }
        fields.addAll(filterListPK(declaredFields(value)));
        return fields;
    }

    private List<Field> filterListPK(List<Field> fields) {
        List<Field> fieldsFiltered = new ArrayList<>();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null && (column.unique())) {
                fieldsFiltered.add(field);
            }
        }
        return fieldsFiltered;
    }

    private Field[] filterListColumn(Field[] fields) {
        Field[] fieldsFiltered = createArray(null);
        int cont = 0;
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            if (column != null) {
                if (cont >= fieldsFiltered.length) {
                    fieldsFiltered = createArray(fieldsFiltered);
                }
                fieldsFiltered[cont++] = field;
            }
        }
        return fieldsFiltered;
    }

    private Field[] createArray(Field[] fields) {
        if (fields == null) {
            return new Field[1];
        }
        Field[] fields1 = new Field[fields.length + 1];
        System.arraycopy(fields, 0, fields1, 0, fields.length);
        return fields1;
    }

    private Field binaryShare(Field[] fields, int targetSimpleNameHash) {
        Arrays.sort(fields, (Field o1, Field o2)
                -> Integer.compare(o1.getAnnotation(Column.class).name().hashCode(), o2.getAnnotation(Column.class).name().hashCode()));
        int min = 0;
        int max = fields.length - 1;
        while (min <= max) {
            int mid = min + (max - min) / 2;
            int target = fields[mid].getAnnotation(Column.class).name().hashCode();
            if (target == targetSimpleNameHash) {
                return fields[mid];
            }
            if (target < targetSimpleNameHash) {
                min = mid + 1;
            } else {
                max = mid - 1;
            }
        }
        return null;
    }
}

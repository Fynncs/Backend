package br.com.fynncs.core.interfaces;

import java.sql.SQLException;
import java.util.List;

public interface ICRUDManager extends ICUDManager{
    <T> T queryObject(String textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException;

    <T> T queryObject(StringBuilder textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException;

    <T> T queryObject(String textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException;

    <T> T queryObject(StringBuilder textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException;

    <T> List<T> queryList(String textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException;

    <T> List<T> queryList(StringBuilder textSQL, List<Object> values, IRowMapper<T> rowMapper) throws SQLException;

    <T> List<T> queryList(String textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException;

    <T> List<T> queryList(StringBuilder textSQL, Object value, IRowMapper<T> rowMapper) throws SQLException;
}

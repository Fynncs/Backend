package br.com.fynncs.core.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IRowMapper<T> {

    T createObject(ResultSet resultSet) throws SQLException;
}

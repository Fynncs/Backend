package br.com.fynncs.core.interfaces;

import java.sql.SQLException;

public interface ICUDManager {
    int persist(Object value) throws SQLException, IllegalAccessException;
}

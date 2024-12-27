package br.com.fynncs.interfaces;

import java.util.Properties;

public interface IReaderProperties extends IReader {
    Boolean save(String fileName, Properties properties);

    Properties getProperties();

    void setProperties(Properties properties);

    String getSpecificProperties(String key) throws Exception;

    void addProperties(String key, String value) throws Exception;

    Boolean containsProperties(String key);
}

package br.com.fynncs.security.reader.properties.interfaces;

import java.io.IOException;
import java.util.Properties;

public interface IReaderProperties {

    Properties read(String fileName) throws IOException;

    Properties getProperties();

    void setProperties(Properties properties);

    String getSpecificPropertie(String value) throws Exception;

    String getSpecificPropertieDecrypt(String value) throws Exception;

    void addPropertie(String key, String value) throws Exception;
    void addEncryptPropertie(String key, String value) throws Exception;

    Boolean save(String fileName) throws Exception;
}

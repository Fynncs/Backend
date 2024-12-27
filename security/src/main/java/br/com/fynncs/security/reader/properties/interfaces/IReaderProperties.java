package br.com.fynncs.security.reader.properties.interfaces;

import java.io.IOException;
import java.util.Properties;

public interface IReaderProperties {

    public Properties read(String fileName) throws IOException;

    public Properties getProperties();

    public void setProperties(Properties properties);

    public String getSpecificPropertie(String value) throws Exception;

    public String getSpecificPropertieDecrypt(String value) throws Exception;

    public void addPropertie(String key, String value) throws Exception;
    public void addEncryptPropertie(String key, String value) throws Exception;

    public Boolean save(String fileName) throws Exception;
}

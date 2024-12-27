package br.com.fynncs.interfaces;

import java.util.Properties;

public interface IReaderEncryptedProperties extends IReaderProperties{
    String getSpecificPropertiesDecrypt(String key) throws Exception;
    void addEncryptProperties(String key, String value) throws Exception;
}

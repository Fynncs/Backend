package br.com.fynncs.security.reader.properties;

import br.com.fynncs.core.Encryption;
import br.com.fynncs.security.reader.properties.interfaces.IReaderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Map;
import java.util.Properties;

@Service
public class ReaderProperties implements IReaderProperties {

    private Properties properties;

    private final Encryption encryption;

    @Autowired
    public ReaderProperties(Encryption encryption) {
        this.encryption = encryption;
    }

    @Override
    public Properties read(String fileName) throws IOException {
        InputStream fis;
        File fileProperties = new File(fileName);
        if (fileProperties.exists()) {
            fis = new FileInputStream(fileProperties);
        } else {
            fis = getClass().getClassLoader().
                    getResourceAsStream(fileName);
        }
        if (fis != null) {
            properties = new Properties();
            properties.load(fis);
            fis.close();
        }
        return properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getSpecificPropertie(String value) throws Exception {
        propertiesIsNull();
        return properties.getProperty(value);
    }

    public String getSpecificPropertieDecrypt(String value) throws Exception {
        propertiesIsNull();
        return encryption.decrypt(properties.getProperty(value), encryption.KEY);
    }

    public void addPropertie(String key, String value) throws Exception {
        propertiesIsNull();
        properties.setProperty(key, value);
    }

    public void addEncryptPropertie(String key, String value) throws Exception {
        propertiesIsNull();
        properties.setProperty(key, encryption.encrypt(value, encryption.KEY));
    }

    private void propertiesIsNull() throws Exception {
        if (properties == null) {
            throw new Exception("Properties is null!");
        }
    }

    public Boolean save(String fileName) throws Exception {
        propertiesIsNull();
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                StringBuilder builder = new StringBuilder();
                builder.append(entry.getKey()).append("=");
                builder.append(encryption.encrypt((String) entry.getValue(), encryption.KEY)).append("\n");
                writer.write(builder.toString());
            }
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
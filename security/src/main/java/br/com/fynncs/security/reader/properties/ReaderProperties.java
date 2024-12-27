package br.com.fynncs.security.reader.properties;

import br.com.fynncs.core.Encryption;
import br.com.fynncs.security.reader.properties.interfaces.IReaderProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        String path = ReaderProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1, path.indexOf("target"));
        path += "src/main/resources/" + fileName;
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        try (InputStream inputStream = new FileInputStream(path)) {
            properties = new Properties();
            properties.load(inputStream);
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
        String path = ReaderProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = path.substring(1, path.indexOf("target"));
        path += "src/main/resources/" + fileName;
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        try (FileWriter writer = new FileWriter(path)) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                StringBuilder builder = new StringBuilder();
                builder.append(entry.getKey()).append("=");
                builder.append("security.config.path".equalsIgnoreCase((String) entry.getKey()) ? (String) entry.getValue():
                        encryption.encrypt((String) entry.getValue(), encryption.KEY)).append("\n");
                writer.write(builder.toString());
            }
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}

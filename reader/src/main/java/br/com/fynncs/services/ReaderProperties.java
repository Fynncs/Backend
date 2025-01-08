package br.com.fynncs.services;

import br.com.fynncs.interfaces.IReaderProperties;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class ReaderProperties extends Reader implements IReaderProperties {

    private Properties properties;

    @Override
    public InputStream read(String fileName) throws Exception {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
            properties = new Properties();
            properties.load(inputStream);
            return inputStream;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void read(InputStream inputStream) throws Exception {
        setProperties(new Properties());
        getProperties().load(inputStream);
    }

    @Override
    public Boolean save(String fileName, Properties properties) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(entry.getKey()).append("=");
                buffer.append(entry.getValue()).append("\n");
                writer.write(buffer.toString());
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String getSpecificProperties(String key) throws Exception {
        return properties.getProperty(key);
    }

    @Override
    public void addProperties(String key, String value) throws Exception {
        properties.setProperty(key, value);
    }

    @Override
    public Boolean containsProperties(String key) {
        return properties.containsKey(key);
    }
}

package br.com.fynncs.services;

import br.com.fynncs.core.Encryption;
import br.com.fynncs.interfaces.IReaderEncryptedProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Service
public class ReaderEncryptedProperties extends ReaderProperties implements IReaderEncryptedProperties {

    private Encryption encryption =  new Encryption();

    public ReaderEncryptedProperties() {
        super();
    }

    @Override
    public InputStream read(String fileName) throws Exception {
        try(InputStream inputStream = new FileInputStream(fileName)) {
            setProperties(new Properties());
            getProperties().load(inputStream);
            return inputStream;
        }
    }

    @Override
    public Boolean save(String fileName, Properties properties) {
        try(FileWriter writer = new FileWriter(fileName)) {
            for(Map.Entry<Object, Object> entry: properties.entrySet()){
                StringBuffer buffer = new StringBuffer();
                buffer.append(entry.getKey()).append("=");
                try {
                    encryption.decrypt((String) entry.getValue(), encryption.KEY);
                    buffer.append(entry.getValue());
                }catch (Exception ex){
                    buffer.append(encryption.encrypt((String) entry.getValue(), encryption.KEY));
                }
                buffer.append("\n");
                writer.write(buffer.toString());
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSpecificPropertiesDecrypt(String key) throws Exception {
        return encryption.decrypt(getSpecificProperties(key), encryption.KEY);
    }

    @Override
    public void addEncryptProperties(String key, String value) throws Exception {
        addProperties(key, encryption.encrypt(value, encryption.KEY));
    }
}

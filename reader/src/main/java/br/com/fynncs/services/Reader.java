package br.com.fynncs.services;

import br.com.fynncs.interfaces.IReader;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class Reader implements IReader {


    @Override
    public InputStream read(String fileName) throws Exception {
        try (InputStream inputStream = new FileInputStream(fileName)) {
            return inputStream;
        }
    }

    @Override
    public Boolean save(String fileName, String value) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(value);
            return Boolean.TRUE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }

    @Override
    public Boolean save(String fileName, List<String> values) {
        try (FileWriter writer = new FileWriter(fileName)) {
            for (String value : values) {
                save(writer, value);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            return Boolean.FALSE;
        }
    }

    private void save(FileWriter writer, String value) throws IOException {
        writer.write(value);
    }

    public String path(String fileName, Class<?> clazz) {
        StringBuffer buffer = new StringBuffer();
        String path = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        buffer.append(URLDecoder.decode(path.substring(1, path.indexOf("target")), StandardCharsets.UTF_8));
        buffer.append("src/main/resources/");
        buffer.append(fileName);
        return buffer.toString();
    }

}

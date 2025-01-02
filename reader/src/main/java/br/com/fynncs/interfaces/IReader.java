package br.com.fynncs.interfaces;

import java.io.InputStream;
import java.util.List;

public interface IReader {

    InputStream read(String fileName) throws Exception;
    Boolean save(String fileName, String value);
    Boolean save(String fileName, List<String> values);

}

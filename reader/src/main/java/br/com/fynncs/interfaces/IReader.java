package br.com.fynncs.interfaces;

import java.io.InputStream;
import java.util.List;

public interface IReader {

    InputStream read(String fileName) throws Exception;

    void read(InputStream inputStream) throws Exception;

    Boolean save(String fileName, String value);

    Boolean save(String fileName, List<String> values);

}

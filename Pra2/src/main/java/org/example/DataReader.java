package org.example;

import java.io.IOException;
import java.util.List;

public interface DataReader {
    List<String> read(String source) throws IOException;
}
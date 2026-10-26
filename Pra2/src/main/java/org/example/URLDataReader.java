package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class URLDataReader implements DataReader {

    @Override
    public List<String> read(String source) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        URI.create(source).toURL().openStream(),
                        StandardCharsets.UTF_8))) {

            return reader.lines().toList();
        }
    }
}
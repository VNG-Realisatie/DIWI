package com.vng.generic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class ResourceUtil {
    public static InputStream getResourceAsStream(String name) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        return classloader.getResourceAsStream(name);
    }

    public static String getResourceAsString(String name) throws IOException {
        try (InputStream is = getResourceAsStream(name)) {
            if (is == null) {
                throw new IOException("Resource not found " + name);
            }
            try (
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }
}

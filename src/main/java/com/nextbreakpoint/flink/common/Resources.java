package com.nextbreakpoint.flink.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Resources {
    private Resources() {}

    public static InputStream asInputStream(String path) throws IOException {
        return asInputStream(path, Resources.class.getClassLoader());
    }

    public static InputStream asInputStream(String path, ClassLoader classLoader) throws IOException {
        if (path.startsWith("classpath:")) {
            return classLoader.getResourceAsStream(path.substring(10));
        } else if (path.startsWith("file:")) {
            return new FileInputStream(path.substring(5));
        }
        return new FileInputStream(path);
    }

    public static Map<String, String> loadConfigMap(String path) throws IOException {
        try (InputStream stream = asInputStream(path)) {
            return new ObjectMapper().readValue(stream, new TypeReference<HashMap<String, String>>() {});
        }
    }

    public static String loadText(String path) throws IOException {
        try (InputStream stream = asInputStream(path)) {
            return new String(readBytes(stream), StandardCharsets.UTF_8);
        }
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        }
    }
}

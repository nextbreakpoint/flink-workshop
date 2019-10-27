package com.nextbreakpoint.flink.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Resources {
    private Resources() {
    }

    public static InputStream createInputStream(String path, ClassLoader classLoader) throws IOException {
        if (path.startsWith("classpath:")) {
            return classLoader.getResourceAsStream(path.substring(10));
        } else if (path.startsWith("file:")) {
            return new FileInputStream(path.substring(5));
        }
        return new FileInputStream(path);
    }
}

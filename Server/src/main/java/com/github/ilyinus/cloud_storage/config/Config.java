package com.github.ilyinus.cloud_storage.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Config {
    private static Path rootFolder;
    private static int maxFileSize;
    private static int port;

    public static boolean load() {
        rootFolder = Paths.get("C:\\Downloads\\ServerFolder");
        maxFileSize = 1024 * 1024 * 10;
        port = 8000;
        return true;
    }

    public static Path getRootFolder() {
        return rootFolder;
    }

    public static int getMaxFileSize() {
        return maxFileSize;
    }

    public static int getPort() {
        return port;
    }
}

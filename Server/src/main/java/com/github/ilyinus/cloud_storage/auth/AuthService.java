package com.github.ilyinus.cloud_storage.auth;

public interface AuthService {
    boolean start();
    boolean stop();
    UserConfig authorize(String username, String password);
}

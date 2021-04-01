package com.github.ilyinus.cloud_storage.auth;

import java.nio.file.Path;

public class UserConfig {
    private final String username;
    private final String pwd_hash;
    private final Path home_path;
    private boolean isAuthorized;

    public UserConfig(String username, String pwdHash, Path homePath) {
        this.username = username;
        this.pwd_hash = pwdHash;
        this.home_path = homePath;
    }

    public Path getHomePath() {
        return home_path;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getUsername() {
        return username;
    }

    public String getPwdHash() {
        return pwd_hash;
    }
}

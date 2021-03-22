package server.auth;

import java.nio.file.Path;

public class UserConfig {
    private final String USERNAME;
    private final String PWD_HASH;
    private final Path HOME_PATH;
    private boolean isAuthorized;

    public UserConfig(String username, String pwdHash, Path homePath) {
        this.USERNAME = username;
        this.PWD_HASH = pwdHash;
        this.HOME_PATH = homePath;
    }

    public Path getHomePath() {
        return HOME_PATH;
    }

    public boolean isAuthorized() {
        return isAuthorized;
    }

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
    }

    public String getUsername() {
        return USERNAME;
    }

    public String getPwdHash() {
        return PWD_HASH;
    }
}

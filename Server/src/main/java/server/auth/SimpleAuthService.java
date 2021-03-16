package server.auth;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class SimpleAuthService implements AuthService{
    private static final Map<String, UserConfig> USERS = new HashMap<>();

    static {
        USERS.put("root", new UserConfig("root", "root", Paths.get("root")));
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public UserConfig authorize(String username, String password) {
        UserConfig user = USERS.get(username);

        if (user != null && user.getPwdHash().equals(password)) {
            user.setAuthorized(true);
        } else {
            user = new UserConfig(null, null, null);
        }

        return user;
    }
}

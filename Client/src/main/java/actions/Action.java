package actions;

import java.nio.file.Path;

public class Action {
    private Path oldPath;
    private Path path;
    private ActionType type;

    public Action(Path path, ActionType type) {
        this.path = path;
        this.type = type;
    }

    public Action(Path oldPath, Path path, ActionType type) {
        this.oldPath = oldPath;
        this.path = path;
        this.type = type;
    }

    public Path getOldPath() {
        return oldPath;
    }

    public Path getPath() {
        return path;
    }

    public ActionType getType() {
        return type;
    }
}

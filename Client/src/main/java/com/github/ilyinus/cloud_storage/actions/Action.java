package com.github.ilyinus.cloud_storage.actions;

import java.nio.file.Path;

public class Action implements Comparable<Action> {
    private int priority;
    private Path oldPath;
    private Path path;
    private ActionType type;

    public Action(Path path, ActionType type) {
        this(null, path, type);
    }

    public Action(Path oldPath, Path path, ActionType type) {
        this.path = path;
        this.type = type;
        this.oldPath = oldPath;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    @Override
    public int compareTo(Action o) {
        return priority - o.priority;
    }
}

package com.github.ilyinus.cloud_storage.messages;

public class ErrorMessage extends Message {
    private final String description;

    public ErrorMessage(String description) {
        super(MessageType.ERROR);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

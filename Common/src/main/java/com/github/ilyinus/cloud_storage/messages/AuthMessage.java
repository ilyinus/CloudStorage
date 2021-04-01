package com.github.ilyinus.cloud_storage.messages;

public class AuthMessage extends Message{
    private String username;
    private String password;
    private String description;

    public AuthMessage(MessageType messageType) {
        super(messageType);
    }

    public AuthMessage(String username, String password) {
        super(MessageType.AUTH);
        this.username = username;
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

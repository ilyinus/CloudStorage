package com.github.ilyinus.cloud_storage.messages;

public class DeleteMessage extends Message{
    private String fileName;

    public DeleteMessage(String fileName) {
        super(MessageType.DELETE_FILE);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}

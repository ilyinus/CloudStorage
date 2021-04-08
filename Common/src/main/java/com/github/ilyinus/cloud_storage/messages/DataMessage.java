package com.github.ilyinus.cloud_storage.messages;

public class DataMessage extends Message {
    private final String uuid;
    private final String fileName;
    private final boolean finalPart;
    private String md5;
    private final byte[] data;

    public DataMessage(String uuid, String fileName, byte[] data, boolean finalPart, String md5) {
        super(MessageType.DATA);
        this.uuid = uuid;
        this.fileName = fileName;
        this.data = data;
        this.finalPart = finalPart;
        this.md5 = md5;
    }

    public DataMessage(String uuid, String fileName, byte[] data, boolean finalPart) {
        this(uuid, fileName, data, finalPart, null);
    }

    public String getMd5() {
        return md5;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isFinalPart() {
        return finalPart;
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

}

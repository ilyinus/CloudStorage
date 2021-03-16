package messages;

public class DataMessage extends Message {
    private final String uuid;
    private final String fileName;
    private final boolean finalPart;
    private final byte[] data;

    public DataMessage(String uuid, String fileName, byte[] data, boolean finalPart) {
        super(MessageType.DATA);
        this.uuid = uuid;
        this.fileName = fileName;
        this.data = data;
        this.finalPart = finalPart;
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

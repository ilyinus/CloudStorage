package messages;

public class RenameMessage extends Message{
    private String oldName;
    private String fileName;

    public RenameMessage(String oldName, String fileName) {
        super(MessageType.RENAME_FILE);
        this.oldName = oldName;
        this.fileName = fileName;
    }

    public String getOldName() {
        return oldName;
    }

    public String getFileName() {
        return fileName;
    }
}

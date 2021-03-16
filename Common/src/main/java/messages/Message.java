package messages;

import java.io.Serializable;

public abstract class Message implements Serializable {
    protected MessageType type;

    public Message(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

}

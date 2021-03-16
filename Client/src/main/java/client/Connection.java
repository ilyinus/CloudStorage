package client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import messages.Message;

import java.io.IOException;
import java.net.Socket;

public class Connection implements AutoCloseable {

    private Socket socket;
    private ObjectEncoderOutputStream out;
    private ObjectDecoderInputStream in;

    public Connection(String host, int port) throws IOException {
        socket = new Socket("localhost", 8000);
        out = new ObjectEncoderOutputStream(socket.getOutputStream());
        in = new ObjectDecoderInputStream(socket.getInputStream(),1024 * 1024 * 10);
    }

    public void sendMessage(Message msg) throws IOException {
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException();
        }
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    @Override
    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

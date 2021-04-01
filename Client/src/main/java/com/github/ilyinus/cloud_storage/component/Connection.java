package com.github.ilyinus.cloud_storage.component;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import com.github.ilyinus.cloud_storage.messages.Message;

import java.io.IOException;
import java.net.Socket;

public class Connection implements AutoCloseable {

    private final Socket socket;
    private final ObjectEncoderOutputStream out;
    private final ObjectDecoderInputStream in;

    public Connection(String host, int port) throws IOException {
        this.socket = new Socket("localhost", 8000);
        this.out = new ObjectEncoderOutputStream(socket.getOutputStream());
        this.in = new ObjectDecoderInputStream(socket.getInputStream(),1024 * 1024 * 10);
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

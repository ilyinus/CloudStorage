package com.github.ilyinus.cloud_storage.controllers;

import com.github.ilyinus.cloud_storage.actions.Action;
import com.github.ilyinus.cloud_storage.actions.ActionType;
import com.github.ilyinus.cloud_storage.component.Connection;
import com.github.ilyinus.cloud_storage.component.FileWatcher;
import com.github.ilyinus.cloud_storage.crypto.CryptoService;
import com.github.ilyinus.cloud_storage.crypto.MessageDigestImpl;
import com.github.ilyinus.cloud_storage.messages.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

public class MainController {
    private Connection connection;
    private Path folder;
    private PriorityBlockingQueue<Action> queue;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setFolder(Path folder) {
        this.folder = folder;
    }

    public void init() {
        queue = new PriorityBlockingQueue<>(100);

        FileWatcher watcher = new FileWatcher(folder, queue);
        Thread watcherThread = new Thread(watcher);
        watcherThread.setDaemon(true);
        watcherThread.start();

        Thread transferThread = new Thread(() -> {
            while (true) {
                try {
                    sendFile(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        transferThread.setDaemon(true);
        transferThread.start();
    }

    private void sendFile(Action action) {
        try {
            String fileName = action.getPath().getFileName().toString();

            if (action.getType() == ActionType.DELETE_FILE) {
                connection.sendMessage(new DeleteMessage(fileName));
            } else if (action.getType() == ActionType.DELIVER_DATA) {

                int bufferSize = 1024 * 1024 * 5;
                String msgUUID = UUID.randomUUID().toString();

                if (Files.size(action.getPath()) == 0) {
                    byte[] data = new byte[0];
                    connection.sendMessage(new DataMessage(msgUUID,
                            fileName,
                            data,
                            true,
                            new MessageDigestImpl("md5", data).getHash()));
                } else {
                    CryptoService crypto = new MessageDigestImpl("md5");
                    BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(action.getPath(), StandardOpenOption.READ));
                    byte[] buffer = new byte[bufferSize];
                    int len;
                    boolean isFinalPart;
                    Message msg;

                    while ((len = bis.read(buffer)) != -1) {
                        isFinalPart = bis.available() == 0;
                        crypto.update(buffer, len);

                        if (isFinalPart) {
                            msg = new DataMessage(msgUUID,
                                    fileName,
                                    len == bufferSize ? buffer : Arrays.copyOf(buffer, len),
                                    true,
                                    crypto.getHash());
                        } else {
                            msg = new DataMessage(msgUUID,
                                    fileName,
                                    len == bufferSize ? buffer : Arrays.copyOf(buffer, len),
                                    false);
                        }
                            connection.sendMessage(msg);

                    }

                    bis.close();

                }

                Message answer = connection.readMessage();

                if (answer.getType() != MessageType.APPROVE) {
                    action.setPriority(Integer.MIN_VALUE);
                    queue.put(action);
                }

            } else if (action.getType() == ActionType.RENAME_FILE) {
                Message msg = new RenameMessage(action.getOldPath().getFileName().toString(), fileName);
                connection.sendMessage(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

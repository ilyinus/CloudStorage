package controllers;

import actions.Action;
import actions.ActionType;
import client.Connection;
import filewatcher.FileWatcher;
import javafx.fxml.Initializable;
import messages.DataMessage;
import messages.DeleteMessage;
import messages.Message;
import messages.RenameMessage;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MainController implements Initializable {
    private Connection connection;
    private Path folder;
    private BlockingQueue<Action> queue;

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void setFolder(Path folder) {
        this.folder = folder;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void init() {
        queue = new ArrayBlockingQueue<>(100);

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
                    connection.sendMessage(new DataMessage(msgUUID, fileName, new byte[0], true));
                } else {
                    BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(action.getPath(), StandardOpenOption.READ));
                    byte[] buffer = new byte[bufferSize];
                    int len;

                    while ((len = bis.read(buffer)) != -1) {
                        Message msg = new DataMessage(msgUUID,
                                fileName,
                                len == bufferSize ? buffer : Arrays.copyOf(buffer, len),
                                bis.available() == 0);
                        connection.sendMessage(msg);
                    }

                    bis.close();
                }
            } else if (action.getType() == ActionType.RENAME_FILE) {
                Message msg = new RenameMessage(action.getOldPath().getFileName().toString(), fileName);
                connection.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

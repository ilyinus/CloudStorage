package com.github.ilyinus.cloud_storage.controllers;

import com.github.ilyinus.cloud_storage.component.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.github.ilyinus.cloud_storage.messages.AuthMessage;
import com.github.ilyinus.cloud_storage.messages.Message;
import com.github.ilyinus.cloud_storage.messages.MessageType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class AuthController implements Initializable {

    public Button okButton;
    public Button cancel;
    public Button choiceButton;
    public TextField username;
    public TextField password;
    public TextField server;
    public TextField folder;

    private Stage primaryStage;
    private Connection connection;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server.appendText("localhost:8000");
        username.appendText("root");
        password.appendText("root");
        folder.appendText("C:\\Downloads\\ClientFolder");
    }

    private boolean establishConnection() {
        boolean result = true;

        try {
            String[] params = server.getText().trim().split(":");

            if (params.length == 2) {
                connection = new Connection(params[0], Integer.parseInt(params[1]));
            } else {
                showAlert(Alert.AlertType.WARNING, "Incorrect server address");
                result = false;
            }

        } catch (IOException | NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, e.toString());
            result = false;
        }

        return result;

    }

    public void authentication(ActionEvent actionEvent) {

        if (!Files.isDirectory(Paths.get(folder.getText()))) {
            showAlert(Alert.AlertType.WARNING, "Incorrect folder");
            return;
        }

        if (!establishConnection())
            return;

        Message authMessage = new AuthMessage(username.getText(), password.getText());

        try {
            connection.sendMessage(authMessage);
            Message answer = connection.readMessage();

            if (answer.getType() == MessageType.AUTH_OK) {
                openMainDialog();
            } else if (answer.getType() == MessageType.AUTH_FAIL) {
                showAlert(Alert.AlertType.WARNING, ((AuthMessage) answer).getDescription());
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void openMainDialog() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load());

            MainController controller = fxmlLoader.getController();
            controller.setConnection(connection);
            controller.setFolder(Paths.get(folder.getText()));
            controller.init();

            primaryStage.setTitle("Cloud storage");
            primaryStage.setResizable(false);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancel(ActionEvent actionEvent) {
        primaryStage.close();
    }

    public void choiceButton(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            folder.appendText(selectedDirectory.getAbsolutePath());
        }
    }

    public void showAlert(Alert.AlertType type, String text) {
        Alert alert = new Alert(type);
        alert.setTitle("Info");
        alert.setHeaderText(text);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.show();
    }

}

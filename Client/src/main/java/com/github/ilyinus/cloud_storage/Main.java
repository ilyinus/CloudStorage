package com.github.ilyinus.cloud_storage;

import com.github.ilyinus.cloud_storage.controllers.AuthController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/auth.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Auth");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);

        AuthController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.bsuir.chat.server.loader;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppLoader extends Application {
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        AppLoader.stage = stage;
        Scene scene = new Scene(loadServerFXML());
        stage.setResizable(false);
        stage.setTitle("Server");
        stage.setScene(scene);
        stage.show();
    }

    private Parent loadServerFXML() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppLoader.class.getClassLoader().
                getResource("server.fxml"));
        return fxmlLoader.load();
    }
}

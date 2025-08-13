package org.example.agendafacil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1210, 700);

        stage.setTitle("AgendaFacil");
        stage.setScene(scene);


        stage.setResizable(true); // impede o redimensionamento
        stage.centerOnScreen();    // centraliza a janela na tela
        stage.setMaximized(true);

        stage.show();
    }



    public static void main(String[] args) {
        launch();
    }
}
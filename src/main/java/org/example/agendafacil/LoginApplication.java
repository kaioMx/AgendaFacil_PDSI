package org.example.agendafacil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var fxml = LoginApplication.class.getResource("login-view.fxml");
        if (fxml == null) throw new IllegalStateException("login-view.fxml não encontrado em resources/org/example/agendafacil/");
        var root = FXMLLoader.load(fxml);

        Scene scene = new Scene((Parent) root, 420, 500); // card ~compacto
        stage.setTitle("AgendaFácil — Login");
        stage.setScene(scene);

        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}

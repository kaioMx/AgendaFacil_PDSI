package org.example.agendafacil.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Button btnNovaTarefa;

    @FXML
    private void abrirNovaTarefa() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tarefa-form.fxml"));
            Parent root = fxmlLoader.load();

            Stage novaJanela = new Stage();
            novaJanela.setTitle("Nova Tarefa");
            novaJanela.setScene(new Scene(root));
            novaJanela.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
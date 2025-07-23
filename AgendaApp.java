package org.example;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class AgendaApp extends Application {

    private ObservableList<String> tarefas = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        Label title = new Label("Agenda Fácil");
        title.getStyleClass().add("title");


        TextField searchField = new TextField();
        searchField.setPromptText("Buscar tarefas...");
        searchField.getStyleClass().add("search-field");


        ListView<String> taskList = new ListView<>(tarefas);
        taskList.getStyleClass().add("task-list");


        TextField newTaskField = new TextField();
        newTaskField.setPromptText("Digite uma nova tarefa");
        newTaskField.getStyleClass().add("search-field");


        Button addButton = new Button("+ Nova Tarefa");
        addButton.getStyleClass().add("add-button");


        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");




        addButton.setOnAction(e -> {
            String nova = newTaskField.getText().trim();
            if (!nova.isEmpty() && !tarefas.contains(nova)) {
                tarefas.add(nova);
                newTaskField.clear();
                errorLabel.setText("");
            } else if (tarefas.contains(nova)) {
                errorLabel.setText("Essa tarefa já existe!");
            }
        });


        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                taskList.setItems(tarefas);
                errorLabel.setText("");
            } else {
                ObservableList<String> filtradas = FXCollections.observableArrayList();
                for (String tarefa : tarefas) {
                    if (tarefa.toLowerCase().contains(newVal.toLowerCase())) {
                        filtradas.add(tarefa);
                    }
                }
                if (filtradas.isEmpty()) {
                    errorLabel.setText("Nenhuma tarefa encontrada.");
                } else {
                    errorLabel.setText("");
                }
                taskList.setItems(filtradas);
            }
        });


        VBox contentBox = new VBox(15, searchField, taskList, newTaskField, addButton, errorLabel);
        contentBox.setId("content");

        BorderPane root = new BorderPane();
        root.setTop(title);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 500, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        stage.setTitle("Agenda Fácil");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


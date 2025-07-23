package org.example.agendafacil.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class TarefaController {

    @FXML
    private VBox novaCategoriaBox;

    @FXML
    private TextField tituloField, horaInicioField, horaFimField;

    @FXML
    private DatePicker dataPicker;

    @FXML
    private TextArea descricaoArea;

    @FXML
    private ComboBox<String> categoriaComboBox;

    @FXML
    private TextField novaCategoriaField;

    @FXML
    private ColorPicker corCategoriaPicker;

    @FXML
    public void initialize() {
        // Inicialização extra se precisar no futuro
    }

    @FXML
    private void mostrarCamposNovaCategoria() {
        novaCategoriaBox.setVisible(true);
        novaCategoriaBox.setManaged(true);
    }

    public void salvarTarefa() {
        String titulo = tituloField.getText();
        String descricao = descricaoArea.getText();
        String horaInicio = horaInicioField.getText();
        String horaFim = horaFimField.getText();
        String data = dataPicker.getValue() != null ? dataPicker.getValue().toString() : "";

        String categoriaSelecionada = categoriaComboBox.getSelectionModel().getSelectedItem();
        String novaCategoria = novaCategoriaField.getText();
        String corSelecionada = corCategoriaPicker.getValue() != null ? toHex(corCategoriaPicker.getValue()) : null;

        // Se criou nova categoria
        if (!novaCategoria.isBlank() && corSelecionada != null) {
            categoriaComboBox.getItems().add(novaCategoria + " (" + corSelecionada + ")");
            categoriaComboBox.getSelectionModel().selectLast();
            System.out.println("Nova categoria criada: " + novaCategoria + " com cor " + corSelecionada);
        }

        String categoriaFinal = categoriaComboBox.getValue();

        System.out.println("Tarefa Salva:");
        System.out.println("Título: " + titulo);
        System.out.println("Descrição: " + descricao);
        System.out.println("Data: " + data);
        System.out.println("Início: " + horaInicio + " | Fim: " + horaFim);
        System.out.println("Categoria: " + categoriaFinal);
        System.out.println("----");
    }

    public void limparFormulario() {
        tituloField.clear();
        descricaoArea.clear();
        horaInicioField.clear();
        horaFimField.clear();
        dataPicker.setValue(null);
        novaCategoriaField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        corCategoriaPicker.setValue(null);
        novaCategoriaBox.setVisible(false);
        novaCategoriaBox.setManaged(false);
    }

    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}

package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.StringConverter;

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
    private ComboBox<String> corCategoriaComboBox;

    // Paleta de cores disponíveis
    private final String[] coresDisponiveis = {
            "#e74c3c", "#f39c12", "#2ecc71", "#3498db", "#9b59b6",
            "#1abc9c", "#34495e", "#e67e22", "#d35400", "#7f8c8d"
    };

    @FXML
    public void initialize() {
        // Preenche ComboBox de cores
        corCategoriaComboBox.getItems().addAll(coresDisponiveis);

        // Exibe cor visualmente nas opções
        corCategoriaComboBox.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(String cor, boolean empty) {
                        super.updateItem(cor, empty);
                        if (cor == null || empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Rectangle rect = new Rectangle(20, 20, Color.web(cor));
                            setText(cor);
                            setGraphic(rect);
                        }
                    }
                };
            }
        });

        // Também mostra a cor no campo selecionado
        corCategoriaComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String cor, boolean empty) {
                super.updateItem(cor, empty);
                if (cor == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Rectangle rect = new Rectangle(20, 20, Color.web(cor));
                    setText(cor);
                    setGraphic(rect);
                }
            }
        });
    }

    public void salvarTarefa() {
        String titulo = tituloField.getText();
        String descricao = descricaoArea.getText();
        String horaInicio = horaInicioField.getText();
        String horaFim = horaFimField.getText();
        String data = dataPicker.getValue() != null ? dataPicker.getValue().toString() : "";

        String categoriaSelecionada = categoriaComboBox.getSelectionModel().getSelectedItem();
        String novaCategoria = novaCategoriaField.getText();
        String corSelecionada = corCategoriaComboBox.getValue();

        // Caso o usuário tenha criado uma nova categoria
        if (!novaCategoria.isBlank() && corSelecionada != null) {
            categoriaComboBox.getItems().add(novaCategoria + " (" + corSelecionada + ")");
            categoriaComboBox.getSelectionModel().selectLast(); // seleciona a que acabou de adicionar
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
        corCategoriaComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void mostrarCamposNovaCategoria() {
        novaCategoriaBox.setVisible(true);
        novaCategoriaBox.setManaged(true);
    }
}

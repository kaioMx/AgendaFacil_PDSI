package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.*;

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
        carregarCategorias();
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

        String categoriaFinal = categoriaSelecionada;
        int idCategoria = -1;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:script/agenda.db")) {

            if (!novaCategoria.isBlank() && corSelecionada != null) {
                String insertCategoria = "INSERT INTO categoria (nome, cor) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertCategoria, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, novaCategoria);
                    stmt.setString(2, corSelecionada);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        idCategoria = rs.getInt(1);
                        categoriaFinal = novaCategoria + " (" + corSelecionada + ")";
                        categoriaComboBox.getItems().add(categoriaFinal);
                        categoriaComboBox.getSelectionModel().selectLast();
                        System.out.println("Nova categoria criada: " + categoriaFinal);
                    }
                }
            } else {

                String getCategoriaId = "SELECT idCategoria FROM categoria WHERE nome = ?";
                try (PreparedStatement stmt = conn.prepareStatement(getCategoriaId)) {
                    stmt.setString(1, categoriaFinal.split(" \\(")[0]);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        idCategoria = rs.getInt("idCategoria");
                    }
                }
            }


            String insertTarefa = "INSERT INTO tarefa (fk_idUsuario, titulo, descricao, data, horaInicio, horaFim, status, somNotificacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            int idTarefa = -1;
            try (PreparedStatement stmt = conn.prepareStatement(insertTarefa, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, 1); //usuário fixo por enquanto
                stmt.setString(2, titulo);
                stmt.setString(3, descricao);
                stmt.setString(4, data);
                stmt.setString(5, horaInicio);
                stmt.setString(6, horaFim);
                stmt.setString(7, "pendente");
                stmt.setBytes(8, null); //null por enquanto implementar depois

                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idTarefa = rs.getInt(1);
                }
            }

            /*if (idTarefa != -1 && idCategoria != -1) {
                String insertRelacao = "INSERT INTO cria_categoria (fk_idTarefa, fk_idCategoria) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertRelacao)) {
                    stmt.setInt(1, idTarefa);
                    stmt.setInt(2, idCategoria);
                    stmt.executeUpdate();
                }
            }*/

            System.out.println("Tarefa Salva no banco:");
            System.out.println("Título: " + titulo);
            System.out.println("Descrição: " + descricao);
            System.out.println("Data: " + data);
            System.out.println("Início: " + horaInicio + " | Fim: " + horaFim);
            System.out.println("Categoria: " + categoriaFinal);
            System.out.println("----");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void carregarCategorias() {
        //categoriaComboBox.getItems().clear();

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:script/agenda.db")) {
            String sql = "SELECT nome, cor FROM categoria";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String nome = rs.getString("nome");
                    String cor = rs.getString("cor");
                    categoriaComboBox.getItems().add(nome + " (" + cor + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

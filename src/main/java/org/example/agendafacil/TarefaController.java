package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.agendafacil.model.TarefaModel;

import java.sql.*;
import java.util.List;

public class TarefaController {

    private List<TarefaModel> listaTarefas;
    private Runnable onTarefaSalva;

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
        carregarCategorias();
        aplicarMascaraHora(horaInicioField);
        aplicarMascaraHora(horaFimField);
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
                stmt.setBytes(8, null); //som ainda não implementado

                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    idTarefa = rs.getInt(1);
                }
            }

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

        // Adiciona a tarefa na memória para o calendário
        if (listaTarefas != null) {
            TarefaModel nova = new TarefaModel();
            nova.setTitulo(titulo);
            nova.setDescricao(descricao);
            nova.setData(data);
            nova.setHoraInicio(horaInicio);
            nova.setHoraFim(horaFim);
            nova.setCorCategoria(corSelecionada);
            nova.setStatus("pendente");
            listaTarefas.add(nova);
        }

        if (onTarefaSalva != null) {
            onTarefaSalva.run();
        }
    }

    public void carregarCategorias() {
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

    public void setListaTarefas(List<TarefaModel> listaTarefas) {
        this.listaTarefas = listaTarefas;
    }

    public void setOnTarefaSalva(Runnable onTarefaSalva) {
        this.onTarefaSalva = onTarefaSalva;
    }

    private void aplicarMascaraHora(TextField campo) {
        campo.setTextFormatter(new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();

            // Só permite números e dois-pontos
            if (!novoTexto.matches("[0-9:]*")) return null;

            // Limita a 5 caracteres (HH:mm)
            if (novoTexto.length() > 5) return null;

            // Adiciona ":" automaticamente após HH
            if (novoTexto.length() == 2 && !novoTexto.contains(":")) {
                change.setText(change.getText() + ":");
                change.setCaretPosition(change.getCaretPosition() + 1);
            }

            return change;
        }));
    }
}

package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.example.agendafacil.model.TarefaModel;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TarefaController {

    private List<TarefaModel> listaTarefas;
    private Runnable onTarefaSalva;

    @FXML
    private VBox novaCategoriaBox;

    @FXML
    public TextField tituloField;
    @FXML
    public TextField horaInicioField;
    @FXML
    public TextField horaFimField;

    @FXML
    public DatePicker dataPicker;

    @FXML
    public TextArea descricaoArea;

    @FXML
    public ComboBox<String> categoriaComboBox;

    @FXML
    private TextField novaCategoriaField;

    @FXML
    public ColorPicker corCategoriaPicker;

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

        if (titulo.isBlank() || data.isBlank() || horaInicio.isBlank() || horaFim.isBlank()) {
            mostrarAlertaErro("Campos obrigatórios", "Por favor, preencha todos os campos obrigatórios.");
            return;
        }

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


                if (idTarefa != -1 && idCategoria != -1) {
                    String insertCriaCategoria = "INSERT INTO cria_categoria (fk_idTarefa, fk_idCategoria) VALUES (?, ?)";
                    try (PreparedStatement stmtCriaCat = conn.prepareStatement(insertCriaCategoria)) {
                        stmtCriaCat.setInt(1, idTarefa);
                        stmtCriaCat.setInt(2, idCategoria);

                        stmtCriaCat.executeUpdate();
                        System.out.println("Relação tarefa-categoria salva com sucesso.");
                    }
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
            mostrarAlertaErro("Erro ao salvar a tarefa", "Ocorreu um erro ao tentar salvar a tarefa no banco de dados.\nVerifique os dados ou tente novamente.");
            return; // evita continuar com tarefa inválida
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
        mostrarAlertaSucesso("Tarefa salva", "Sua tarefa foi salva com sucesso!");


    }

    private void mostrarAlertaSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION); // <- Tipo INFO
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }


    private void mostrarAlertaErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
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
        // Define uma dica visual para o usuário
        Tooltip tooltip = new Tooltip("Digite a hora no formato HH:mm (ex: 08:30)");
        campo.setTooltip(tooltip);

        campo.setTextFormatter(new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();

            // Permite apenas números e dois-pontos
            if (!novoTexto.matches("[0-9:]*")) return null;

            // Limita a 5 caracteres (HH:mm)
            if (novoTexto.length() > 5) return null;

            // Garante que o ":" só seja inserido na terceira posição
            if (novoTexto.length() == 2 && !novoTexto.contains(":")) {
                change.setText(change.getText() + ":");
                change.setCaretPosition(change.getCaretPosition() + 1);
            }

            // Validações adicionais opcionais:
            if (novoTexto.length() == 5 && novoTexto.contains(":")) {
                String[] partes = novoTexto.split(":");
                if (partes.length == 2) {
                    try {
                        int hora = Integer.parseInt(partes[0]);
                        int minuto = Integer.parseInt(partes[1]);

                        if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
                            return null; // valor inválido
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }

            return change;
        }));
    }


    public LocalTime converterHora(String texto) {
        try {
            return LocalTime.parse(texto); // espera "HH:mm"
        } catch (DateTimeParseException e) {
            System.out.println("Formato de hora inválido: " + texto);
            return null;
        }
    }
    // No controller
    public boolean camposValidos(String titulo, String horaInicio, String data) {
        return titulo != null && !titulo.isBlank()
                && horaInicio != null && horaInicio.matches("\\d{2}:\\d{2}")
                && data != null && !data.isBlank();
    }

}

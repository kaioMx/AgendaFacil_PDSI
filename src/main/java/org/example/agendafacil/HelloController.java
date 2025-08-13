package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.example.agendafacil.model.TarefaModel;

import java.io.IOException;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HelloController {

    @FXML
    private GridPane gridHeader;

    @FXML
    private Button btnNovaTarefa;

    @FXML
    private Label labelPeriodo;

    @FXML
    private GridPane gridAgenda;

    private LocalDate dataInicialSemana;

    private final List<Label> labelsDias = new ArrayList<>();
    private final List<TarefaModel> tarefas = new ArrayList<>();

    @FXML
    public void initialize() {
        dataInicialSemana = LocalDate.now().with(DayOfWeek.MONDAY);
        carregarTarefasDoBanco();
        atualizarCalendario();


    }

    @FXML
    private void abrirNovaTarefa() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("tarefa-form.fxml"));
            Parent root = fxmlLoader.load();

            TarefaController controller = fxmlLoader.getController();
            controller.setListaTarefas(tarefas);
            controller.setOnTarefaSalva(this::atualizarCalendario);

            Stage novaJanela = new Stage();
            novaJanela.setTitle("Nova Tarefa");
            novaJanela.setScene(new Scene(root));
            novaJanela.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void semanaAnterior() {
        dataInicialSemana = dataInicialSemana.minusWeeks(1);
        atualizarCalendario();
    }

    @FXML
    private void proximaSemana() {
        dataInicialSemana = dataInicialSemana.plusWeeks(1);
        atualizarCalendario();
    }

    private void atualizarCalendario() {
        atualizarTextoPeriodo();

        // Limpa a grade (exceto cabeçalho)
        gridAgenda.getChildren().removeIf(node -> {
            Integer col = GridPane.getColumnIndex(node);
            // Remove tudo exceto a coluna 0 (onde ficam os horários)
            return col != null && col > 0;
        });




        // Cria alvos invisíveis para drag & drop
        for (int linha = 0; linha <= 16; linha++) { // 7h até 23h (linha 0 a 16)
            for (int coluna = 1; coluna <= 7; coluna++) { // dias da semana (1 a 7)

                Label target = new Label();
                target.setMinSize(100, 40); // ajustável para o tamanho da célula
                target.setStyle("-fx-border-color: transparent;");

                final int finalLinha = linha;
                final int finalColuna = coluna;

                target.setOnDragOver(event -> {
                    if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                    event.consume();
                });

                target.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        int idTarefa = Integer.parseInt(db.getString());

                        // Nova hora = linha + 7 (ex: linha 0 = 7h)
                        String novaHora = String.format("%02d:00", finalLinha + 7);
                        LocalDate novaData = dataInicialSemana.plusDays(finalColuna - 1);

                        atualizarTarefaNoBanco(idTarefa, novaData.toString(), novaHora);

                        carregarTarefasDoBanco();  // atualiza lista
                        atualizarCalendario();    // redesenha tudo

                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                GridPane.setColumnIndex(target, coluna);
                GridPane.setRowIndex(target, linha);
                gridAgenda.getChildren().add(target);
            }
        }

        // Desenha as tarefas
        for (TarefaModel tarefaModel : tarefas) {
            try {
                LocalDate dataTarefa = LocalDate.parse(tarefaModel.getData());
                String horaTexto = tarefaModel.getHoraInicio();
                if (horaTexto == null || !horaTexto.matches("\\d{2}:\\d{2}")) {
                    System.out.println("Formato de hora inválido: " + horaTexto);
                    continue;
                }

                int hora = Integer.parseInt(horaTexto.split(":")[0]);
                int linha = hora - 7;

                if (linha < 0 || linha > 16) {
                    System.out.println("Hora fora do intervalo do calendário: " + horaTexto);
                    continue;
                }

                for (int i = 0; i < 7; i++) {
                    LocalDate diaColuna = dataInicialSemana.plusDays(i);
                    if (diaColuna.equals(dataTarefa)) {
                        int coluna = i + 1;

                        Label tarefa = new Label(tarefaModel.getTitulo());

                        String cor = tarefaModel.getCorCategoria();
                        if (cor == null || cor.isBlank()) {
                            cor = "#ff99cc"; // cor padrão
                        }

                        tarefa.getStyleClass().add("label-tarefa");
                        tarefa.setStyle("-fx-background-color: " + cor + ";"); // só define a cor da categoria

                        // Ativa o drag
                        // Ativa o drag com efeito visual da tarefa sendo arrastada
                        tarefa.setOnDragDetected(event -> {
                            Dragboard db = tarefa.startDragAndDrop(TransferMode.MOVE);

                            ClipboardContent content = new ClipboardContent();
                            content.putString(String.valueOf(tarefaModel.getIdTarefa()));
                            db.setContent(content);

                            // Define uma miniatura visual da tarefa sendo arrastada
                            db.setDragView(tarefa.snapshot(null, null));

                            event.consume();
                        });

                        GridPane.setColumnIndex(tarefa, coluna);
                        GridPane.setRowIndex(tarefa, linha);
                        gridAgenda.getChildren().add(tarefa);
                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao renderizar tarefa: " + tarefaModel.getTitulo());
                e.printStackTrace();
            }
            // Redesenha as horas na coluna 0

        }

        for (int i = 0; i < 17; i++) {
            int hora = i + 7;
            String textoHora;

            if (hora < 12) {
                textoHora = hora + " AM";
            } else if (hora == 12) {
                textoHora = "12 PM";
            } else {
                textoHora = (hora - 12) + " PM";
            }

            Label horaLabel = new Label(textoHora);
            horaLabel.setStyle("-fx-text-fill: #444; -fx-font-size: 12px;");
            GridPane.setValignment(horaLabel, VPos.CENTER);
            GridPane.setRowIndex(horaLabel, i);
            GridPane.setColumnIndex(horaLabel, 0);
            gridAgenda.getChildren().add(horaLabel);
        }
    }

    void carregarTarefasDoBanco() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:script/agenda.db")) {
            String sql = "SELECT " +
                    "t.idTarefa, t.titulo, t.descricao, t.data, t.horaInicio, t.horaFim, t.horaAlarme, t.status, " +
                    "c.cor AS corCategoria " +
                    "FROM tarefa t " +
                    "JOIN cria_categoria cc ON t.idTarefa = cc.fk_idTarefa " +
                    "JOIN categoria c ON cc.fk_idCategoria = c.idCategoria";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                tarefas.clear(); // limpa antes para não duplicar
                while (rs.next()) {
                    TarefaModel t = new TarefaModel();
                    t.setIdTarefa(rs.getInt("idTarefa")); // <- ID da tarefa
                    t.setTitulo(rs.getString("titulo"));
                    t.setDescricao(rs.getString("descricao"));
                    t.setData(rs.getString("data"));
                    t.setHoraInicio(rs.getString("horaInicio"));
                    t.setHoraFim(rs.getString("horaFim"));
                    t.setHoraAlarme(rs.getString("horaAlarme"));
                    t.setStatus(rs.getString("status"));
                    t.setCorCategoria(rs.getString("corCategoria")); // <- cor da categoria associada

                    tarefas.add(t);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void atualizarTextoPeriodo() {
        LocalDate fimSemana = dataInicialSemana.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM", new Locale("pt", "BR"));
        String texto = formatter.format(dataInicialSemana) + " - " + formatter.format(fimSemana);
        labelPeriodo.setText(texto);
    }
    // Método auxiliar para testes unitários que calcula o texto do período da semana
    public String calcularTextoPeriodo(LocalDate dataInicial) {
        LocalDate fimSemana = dataInicial.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM", new Locale("pt", "BR"));
        return formatter.format(dataInicial) + " - " + formatter.format(fimSemana);
    }

    public List<TarefaModel> getTarefas() {
        return tarefas;
    }

    private void atualizarTarefaNoBanco(int idTarefa, String novaData, String novaHora) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:script/agenda.db")) {
            String sql = "UPDATE tarefa SET data = ?, horaInicio = ? WHERE idTarefa = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, novaData);
                stmt.setString(2, novaHora);
                stmt.setInt(3, idTarefa);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML

    private void refreshTarefas() {

        gridAgenda.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            return col != null && col > 0 && row != null && row > 0;
        });

        // Atualiza calendário e recarrega tarefas
        atualizarCalendario();
        carregarTarefasDoBanco();
    }




}

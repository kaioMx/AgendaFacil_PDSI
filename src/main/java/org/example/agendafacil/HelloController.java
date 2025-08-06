package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        atualizarCalendario();
        carregarTarefasDoBanco();
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

        gridAgenda.getChildren().removeIf(node -> {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);
            return col != null && col > 0 && row != null && row >= 0;
        });

        for (TarefaModel tarefaModel : tarefas) {
            try {
                LocalDate dataTarefa = LocalDate.parse(tarefaModel.getData());
                String horaTexto = tarefaModel.getHoraInicio();
                if (horaTexto == null || !horaTexto.matches("\\d{2}:\\d{2}")) {
                    System.out.println("Formato de hora inválido: " + horaTexto);
                    continue; // pula essa tarefa
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
                            cor = "#ff99cc"; // cor padrão se não tiver categoria
                        }

                        tarefa.setStyle("-fx-background-color: " + cor + "; -fx-background-radius: 20; -fx-text-fill: black; -fx-padding: 5 10;");
                        GridPane.setColumnIndex(tarefa, coluna);
                        GridPane.setRowIndex(tarefa, linha);
                        gridAgenda.getChildren().add(tarefa);

                    }
                }
            } catch (Exception e) {
                System.out.println("Erro ao renderizar tarefa: " + tarefaModel.getTitulo());
            }
        }
    }
    private void carregarTarefasDoBanco() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:script/agenda.db")) {
            String sql = "SELECT titulo, descricao, data, horaInicio, horaFim, status FROM tarefa";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                tarefas.clear(); // limpa antes para não duplicar
                while (rs.next()) {
                    TarefaModel t = new TarefaModel();
                    t.setTitulo(rs.getString("titulo"));
                    t.setDescricao(rs.getString("descricao"));
                    t.setData(rs.getString("data"));
                    t.setHoraInicio(rs.getString("horaInicio"));
                    t.setHoraFim(rs.getString("horaFim"));
                    t.setStatus(rs.getString("status"));
                    
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
}

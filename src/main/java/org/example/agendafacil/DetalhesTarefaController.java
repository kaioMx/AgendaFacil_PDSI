package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.agendafacil.model.TarefaModel;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class DetalhesTarefaController {

    @FXML private Label labelTitulo;
    @FXML private Label labelCategoria;
    @FXML private Label labelHorario;
    @FXML private Label labelData; // ✅ NOVO
    @FXML private TextArea areaDescricao;
    @FXML private Button btnEditar;
    @FXML private Button btnFechar;

    private TarefaModel tarefa;
    private Consumer<TarefaModel> onEditarCallback;

    public void setTarefa(TarefaModel tarefa) {
        this.tarefa = tarefa;
        if (tarefa == null) {
            preencherPlaceholders();
            btnEditar.setDisable(true);
            return;
        }

        labelTitulo.setText(nullTo("(Sem título)", tarefa.getTitulo()));
        areaDescricao.setText(nullTo("(Sem descrição)", tarefa.getDescricao()));

        String hi = nullTo("--:--", tarefa.getHoraInicio());
        String hf = nullTo("--:--", tarefa.getHoraFim());
        labelHorario.setText(hi + " - " + hf);

        // ✅ Exibe a data
        labelData.setText(formatarData(tarefa.getData()));


        // Cor da categoria
        labelCategoria.setText("");
        aplicarCorCategoria(tarefa.getCorCategoria());
    }

    public void setOnEditar(Consumer<TarefaModel> onEditar) {
        this.onEditarCallback = onEditar;
        if (btnEditar != null) btnEditar.setDisable(onEditar == null);
    }

    @FXML
    private void onEditar() {
        if (onEditarCallback != null && tarefa != null) {
            onEditarCallback.accept(tarefa);
            ((Stage) btnEditar.getScene().getWindow()).close();
        }
    }

    @FXML
    private void onFechar() {
        ((Stage) btnFechar.getScene().getWindow()).close();
    }

    private void preencherPlaceholders() {
        labelTitulo.setText("(Sem título)");
        labelCategoria.setText("Sem categoria");
        labelHorario.setText("--:-- - --:--");
        labelData.setText("(Sem data)"); // ✅ Placeholder
        areaDescricao.setText("(Sem descrição)");
    }

    private void aplicarCorCategoria(String corHex) {
        if (corHex == null) return;
        String cor = corHex.startsWith("#") ? corHex : ("#" + corHex);
        if (!cor.matches("#?[0-9a-fA-F]{6}")) return;
        labelCategoria.setStyle("-fx-background-color:" + cor + "; -fx-text-fill:white;");
    }

    private String nullTo(String fallback, String v) {
        return (v == null || v.isBlank()) ? fallback : v;
    }

    public static void showModal(Stage owner, TarefaModel tarefa, Consumer<TarefaModel> onEditar) {
        try {
            FXMLLoader loader = new FXMLLoader(DetalhesTarefaController.class.getResource("detalhes_tarefa-view.fxml"));
            Parent root = loader.load();

            DetalhesTarefaController controller = loader.getController();
            controller.setOnEditar(onEditar);
            controller.setTarefa(tarefa);

            Stage modal = new Stage(StageStyle.UTILITY);
            if (owner != null) modal.initOwner(owner);
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setResizable(false);
            modal.setTitle("Detalhes da Tarefa");
            modal.setScene(new Scene(root));
            modal.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Não foi possível abrir os detalhes.").showAndWait();
        }
    }


    private static final DateTimeFormatter BR_DATE  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private String formatarData(String data) {
        if (data == null || data.isBlank()) return "(Sem data)";
        // tenta ISO: yyyy-MM-dd
        try {
            LocalDate d = LocalDate.parse(data, ISO_DATE);
            return d.format(BR_DATE);
        } catch (DateTimeParseException ignored) {}

        // tenta se já veio em dd/MM/yyyy
        try {
            LocalDate d = LocalDate.parse(data, BR_DATE);
            return d.format(BR_DATE);
        } catch (DateTimeParseException ignored) {}

        // tenta dd-MM-yyyy
        try {
            DateTimeFormatter alt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate d = LocalDate.parse(data, alt);
            return d.format(BR_DATE);
        } catch (DateTimeParseException ignored) {}

        // fallback: mostra como veio
        return data;
    }

}

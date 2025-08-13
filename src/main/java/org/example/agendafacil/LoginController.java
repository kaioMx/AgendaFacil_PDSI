package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LoginController {

    // Esse método você chamaria quando abrir a tela de login
    public void configurarJanelaTransparente(Stage stage, Scene scene) {
        // Define que a janela não terá bordas padrão do SO
        stage.initStyle(StageStyle.TRANSPARENT);

        // Define que a cena será transparente (pra sumir com o fundo branco)
        scene.setFill(Color.TRANSPARENT);

        // Opcional: trava tamanho se for um card fixo
        stage.setResizable(false);
    }

    // Exemplo de inicialização da tela
    @FXML
    public void initialize() {
        System.out.println("Tela de login carregada!");
        // Aqui você pode inicializar campos, eventos, etc.
    }

    // Método pra testar: chame isso no seu App principal
    public static void mostrar(Stage stage) throws Exception {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;"); // container raiz transparente

        Scene scene = new Scene(root, 400, 600);
        LoginController controller = new LoginController();
        controller.configurarJanelaTransparente(stage, scene);

        stage.setScene(scene);
        stage.show();
    }
}

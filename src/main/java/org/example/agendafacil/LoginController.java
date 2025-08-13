package org.example.agendafacil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.agendafacil.database.ConexaoSQLite;

import java.sql.*;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private ToggleButton showPassToggle;
    @FXML
    private TextField passwordVisibleField;

    private CriarAlerta criarAlerta = new CriarAlerta();

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

        setupShowPasswordToggle();
    }

    // Método pra testar: chame isso no seu App principal
    public static void mostrar(Stage stage) throws Exception {
        /*StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;"); // container raiz transparente

        Scene scene = new Scene(root, 400, 600);
        LoginController controller = new LoginController();
        controller.configurarJanelaTransparente(stage, scene);

        stage.setScene(scene);
        stage.show();*/


        FXMLLoader fxmlLoader = new FXMLLoader(LoginController.class.getResource("login.fxml"));
        Parent root = fxmlLoader.load();
        LoginController controller = fxmlLoader.getController();

        Scene scene = new Scene(root, 400, 600);
        controller.configurarJanelaTransparente(stage, scene);

        stage.setScene(scene);
        stage.show();

    }

    @FXML
    private void login() {
        String getEmail = emailField.getText();
        String getSenha = passwordField.getText();

        String userEmail = getEmail != null ? emailField.getText() : "";
        String senhaDigitada = getSenha != null ? passwordField.getText() : "";

        if (userEmail.isBlank() || senhaDigitada.isBlank()) {
            criarAlerta.alerta(Alert.AlertType.WARNING, "Preencha e‑mail e senha.");
            return;
        }

        String sql = "SELECT idUsuario, senha FROM usuario WHERE email = ?";

        try (Connection conn = ConexaoSQLite.abrirConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("entrei usuario invalido");
                    criarAlerta.alerta(Alert.AlertType.ERROR, "Usuário não encontrado.");
                    return;
                }

                int id = rs.getInt("idUsuario");
                String hashArmazenado = rs.getString("senha");

                boolean ok = HashSenha.verify(senhaDigitada, hashArmazenado);

                // Fallback para legado: sem hash salvo (sem ":") e igual ao digitado
                if (!ok && hashArmazenado != null && !hashArmazenado.contains(":") && senhaDigitada.equals(hashArmazenado)) {
                    ok = true;
                    // Upgrade imediato: salva já com hash
                    String novoHash = HashSenha.hash(senhaDigitada);
                    try (PreparedStatement up = conn.prepareStatement(
                            "UPDATE usuario SET senha=? WHERE idUsuario=?")) {
                        up.setString(1, novoHash);
                        up.setInt(2, id);
                        up.executeUpdate();
                    }
                }

                if (ok) {
                    criarAlerta.alerta(Alert.AlertType.INFORMATION, "Login OK! (id=" + id + ")");
                    // FAZER A LÓGICA DE ir para próxima tela
                } else {
                    criarAlerta.alerta(Alert.AlertType.ERROR, "Senha incorreta.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            criarAlerta.alerta(Alert.AlertType.ERROR, "Erro de banco: " + ex.getMessage());
        } finally {
            ConexaoSQLite.fecharConexao();
        }
    }

    private void setupShowPasswordToggle() {
        // Se não veio do FXML, cria e coloca do lado do PasswordField
        if (passwordVisibleField == null) {
            passwordVisibleField = new TextField();
            passwordVisibleField.setPromptText("Senha");
            passwordVisibleField.setVisible(false);
            passwordVisibleField.setManaged(false);

            // Insere no mesmo HBox do passwordField
            if (passwordField.getParent() instanceof HBox hbox) {
                int idx = hbox.getChildren().indexOf(passwordField);
                hbox.getChildren().add(idx + 1, passwordVisibleField);
                HBox.setHgrow(passwordVisibleField, Priority.ALWAYS);
            } else {
                throw new IllegalStateException("passwordField não está dentro de um HBox");
            }
        }

        // Alternância de visibilidade/gerenciamento
        passwordVisibleField.visibleProperty().bind(showPassToggle.selectedProperty());
        passwordVisibleField.managedProperty().bind(showPassToggle.selectedProperty());
        passwordField.visibleProperty().bind(showPassToggle.selectedProperty().not());
        passwordField.managedProperty().bind(showPassToggle.selectedProperty().not());

        // Sincroniza texto entre os dois campos
        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());

        // Texto do toggle
        showPassToggle.setText("Mostrar");
        showPassToggle.selectedProperty().addListener((obs, was, is) ->
                showPassToggle.setText(is ? "Ocultar" : "Mostrar"));
    }


}

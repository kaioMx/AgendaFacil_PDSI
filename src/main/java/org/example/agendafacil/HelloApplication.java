package org.example.agendafacil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;            // AWT para SystemTray
import java.io.IOException;

public class HelloApplication extends Application {

    private TrayIcon trayIcon;

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega sua UI
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1210, 700);

        stage.setTitle("AgendaFacil");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.setMaximized(true);

        // Não finalize a JVM quando a janela for fechada
        Platform.setImplicitExit(false);

        // Configura a bandeja do sistema
        setupSystemTray(stage);

        // "Fechar" = esconder e continuar rodando na bandeja
        stage.setOnCloseRequest(event -> {
            event.consume();
            stage.hide();
            showTrayMessage("AgendaFácil está rodando em segundo plano.");
        });

        stage.show();

        // Remove o ícone da bandeja ao encerrar de verdade
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (trayIcon != null) {
                SystemTray.getSystemTray().remove(trayIcon);
            }
        }));
    }

    private void setupSystemTray(Stage stage) {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray não suportado neste sistema.");
            return;
        }

        try {
            var url = HelloApplication.class.getResource(
                    "/org/example/agendafacil/imagens/teste.png"
            );
            if (url == null) {
                System.err.println("Ícone não encontrado no classpath.");
                return;
            }
            Image image = Toolkit.getDefaultToolkit().getImage(url);

// AWT
            PopupMenu menu = new PopupMenu();

            MenuItem abrir = new MenuItem("Abrir AgendaFácil");
            abrir.addActionListener(e -> Platform.runLater(() -> {
                if (!stage.isShowing()) stage.show();
                stage.toFront();
            }));

            MenuItem sair = new MenuItem("Sair");
            sair.addActionListener(e -> Platform.runLater(() -> {
                if (trayIcon != null) SystemTray.getSystemTray().remove(trayIcon);
                Platform.exit();
                System.exit(0);
            }));

            menu.add(abrir);
            menu.addSeparator();
            menu.add(sair);

            trayIcon = new TrayIcon(image, "AgendaFácil", menu);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> Platform.runLater(() -> {
                if (!stage.isShowing()) stage.show();
                stage.toFront();
            }));

            SystemTray.getSystemTray().add(trayIcon);

        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Falha ao configurar SystemTray: " + ex.getMessage());
        }
        stage.getIcons().add(
                new javafx.scene.image.Image(
                        getClass().getResourceAsStream("/org/example/agendafacil/imagens/logo.png")
                )
        );
    }


    private void showTrayMessage(String msg) {
        if (trayIcon != null) {
            trayIcon.displayMessage("AgendaFácil", msg, TrayIcon.MessageType.INFO);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

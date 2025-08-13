module org.example.agendafacil {
    // JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;      // <— precisa para Application/Stage/Scene

    // Outras dependências
    requires java.desktop;         // <— AWT/SystemTray (uma única vez)
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;

    // Aberturas para o FXMLLoader
    opens org.example.agendafacil to javafx.fxml;
    // Se você tiver controladores em outro pacote, descomente a linha abaixo:
    // opens org.example.agendafacil.controller to javafx.fxml;

    // Exports
    exports org.example.agendafacil;
    exports org.example.agendafacil.database;
}

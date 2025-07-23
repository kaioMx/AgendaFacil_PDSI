module org.example.agendafacil {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens org.example.agendafacil to javafx.fxml;
    exports org.example.agendafacil;
    exports org.example.agendafacil.controller;
    opens org.example.agendafacil.controller to javafx.fxml;
    exports org.example.agendafacil.view;
    opens org.example.agendafacil.view to javafx.fxml;
}
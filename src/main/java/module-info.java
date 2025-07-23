module org.example.agendafacil {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens org.example.agendafacil to javafx.fxml;
    exports org.example.agendafacil;
}
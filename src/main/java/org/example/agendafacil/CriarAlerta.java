package org.example.agendafacil;

import javafx.scene.control.Alert;

public class CriarAlerta {

    public void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}

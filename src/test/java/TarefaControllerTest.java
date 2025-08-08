package org.example.agendafacil;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class TarefaControllerTest {

    @Test
    public void testToHexColorRed() throws Exception {
        TarefaController controller = new TarefaController();
        Method method = TarefaController.class.getDeclaredMethod("toHex", Color.class);
        method.setAccessible(true);

        String hex = (String) method.invoke(controller, Color.RED);
        assertEquals("#FF0000", hex);
    }

    @Test
    public void testToHexColorBlue() throws Exception {
        TarefaController controller = new TarefaController();
        Method method = TarefaController.class.getDeclaredMethod("toHex", Color.class);
        method.setAccessible(true);

        String hex = (String) method.invoke(controller, Color.BLUE);
        assertEquals("#0000FF", hex);
    }

    @Test
    public void testToHexColorCustom() throws Exception {
        TarefaController controller = new TarefaController();
        Method method = TarefaController.class.getDeclaredMethod("toHex", Color.class);
        method.setAccessible(true);

        Color cor = Color.rgb(123, 200, 50);
        String hex = (String) method.invoke(controller, cor);
        assertEquals("#7BC832", hex);
    }

    @Test
    public void testToHexColorNull() throws Exception {
        TarefaController controller = new TarefaController();
        Method method = TarefaController.class.getDeclaredMethod("toHex", Color.class);
        method.setAccessible(true);

        String hex = (String) method.invoke(controller, new Object[]{null});
        assertNull(hex);
    }
    @Test
    public void testCamposValidos() {
        TarefaController controller = new TarefaController();

        assertTrue(controller.camposValidos("Estudar", "08:00", "2025-08-06"));
        assertFalse(controller.camposValidos("", "08:00", "2025-08-06"));         // título vazio
        assertFalse(controller.camposValidos("Estudar", "xx:yy", "2025-08-06")); // hora inválida
        assertFalse(controller.camposValidos("Estudar", "08:00", ""));          // data vazia
    }


}

import org.example.agendafacil.HelloController;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HelloControllerTest {

    @Test
    public void testCalculoTextoPeriodoSemana() {
        // Cria uma instância do controlador
        HelloController controller = new HelloController();

        // Define uma data de segunda-feira como ponto de partida (4 de agosto de 2025)
        LocalDate segunda = LocalDate.of(2025, 8, 4);

        // Chama o método que calcula o texto do período da semana e normaliza para comparação
        String resultado = controller.calcularTextoPeriodo(segunda).toLowerCase().trim();

        // Verifica se o texto gerado corresponde ao esperado
        assertEquals("04 ago. - 10 ago.", resultado);
    }
    @Test
    public void testCarregarTarefasDoBanco() {
        HelloController controller = new HelloController();

        assertDoesNotThrow(() -> controller.carregarTarefasDoBanco(),
                "O método carregarTarefasDoBanco() lançou uma exceção!");


    }



}

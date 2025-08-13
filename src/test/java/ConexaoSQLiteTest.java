import org.example.agendafacil.database.ConexaoSQLite;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ConexaoSQLiteTest {

    @Test
    public void testConexaoNaoNula() {
        try (Connection conn = ConexaoSQLite.abrirConexao()) {
            assertNotNull(conn, "A conexão não deve ser nula");
            assertFalse(conn.isClosed(), "A conexão deve estar aberta");
        } catch (SQLException e) {
            fail("Erro ao conectar com o banco: " + e.getMessage());
        }
    }
}
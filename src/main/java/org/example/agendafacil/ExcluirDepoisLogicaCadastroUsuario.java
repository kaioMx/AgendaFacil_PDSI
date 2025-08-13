package org.example.agendafacil;

import javafx.scene.control.Alert;
import org.example.agendafacil.database.ConexaoSQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExcluirDepoisLogicaCadastroUsuario {

    private CriarAlerta criarAlerta;

    public void cadastrarUsuario(String novoEmail, String senhaPlana) {
        String sql = "INSERT INTO usuario (email, senha, nome) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoSQLite.abrirConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String hash = HashSenha.hash(senhaPlana); // <— gera hash seguro
            ps.setString(1, novoEmail);
            ps.setString(2, hash);
            ps.setString(3, "Novo Usuário");
            ps.executeUpdate();

            criarAlerta.alerta(Alert.AlertType.INFORMATION, "Usuário cadastrado!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            criarAlerta.alerta(Alert.AlertType.ERROR, "Erro ao cadastrar: " + ex.getMessage());
        } finally {
            ConexaoSQLite.fecharConexao();
        }


    }
}

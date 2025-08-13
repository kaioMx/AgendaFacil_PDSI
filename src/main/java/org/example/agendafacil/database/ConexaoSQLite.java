package org.example.agendafacil.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLite {

    private static final String URL = "jdbc:sqlite:script/agenda.db";
    private static Connection conexao = null;

    public static Connection abrirConexao() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL);
            System.out.println("Conexão aberta com sucesso!");
        }
        return conexao;
    }

    public static void fecharConexao() {
        if (conexao != null) {
            try {
                if (!conexao.isClosed()) {
                    conexao.close();
                    System.out.println("Conexão fechada com sucesso!");
                }
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão: " + e.getMessage());
            }
        }
    }
}


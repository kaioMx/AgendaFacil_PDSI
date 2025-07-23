package org.example.agendafacil.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoSQLite {
    private static final String URL = "jdbc:sqlite:script/agenda.db";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}


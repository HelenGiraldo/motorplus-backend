package com.motorplus.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        try {

            return DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/motorplus_db",
                    "root",
                    "Susanayhelen121523"
            );
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
package com.motorplus.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    return connection;
                }
            } catch (SQLException e) {
                System.err.println("Error al comprobar la conexión: " + e.getMessage());
            }
        }

        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("No se pudo encontrar el archivo db.properties");
            }

            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
            String driver = prop.getProperty("db.driver");

            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            return connection;

        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error conectando a MySQL");
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
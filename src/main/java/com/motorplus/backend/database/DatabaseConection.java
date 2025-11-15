package com.motorplus.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Properties;
import java.io.InputStream;

public class DatabaseConnection {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection != null) return connection;

        try (InputStream input = DatabaseConnection.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            Properties prop = new Properties();
            prop.load(input);

            String url = prop.getProperty("db.url");
            String user = prop.getProperty("db.user");
            String password = prop.getProperty("db.password");
            String driver = prop.getProperty("db.driver");

            Class.forName(driver);

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✔ Conexión exitosa a MySQL");
            return connection;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Error conectando a MySQL");
        }
    }
}

package com.analytics.student.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConnectionManager.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new DatabaseConnectionException("Unable to find db.properties in classpath.", null);
            }
            properties.load(input);
            // Optional: force load driver class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (IOException ex) {
            throw new DatabaseConnectionException("Error reading db.properties file.", ex);
        } catch (ClassNotFoundException ex) {
            throw new DatabaseConnectionException("MySQL JDBC driver not found.", ex);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
            );
        } catch (SQLException ex) {
            throw new DatabaseConnectionException("Failed to connect to the database.", ex);
        }
    }

    public static String getUrl() {
        return properties.getProperty("db.url", "");
    }

    public static String getUser() {
        return properties.getProperty("db.user", "");
    }

    public static String getPassword() {
        return properties.getProperty("db.password", "");
    }

    public static void updateCredentials(String url, String user, String password) {
        properties.setProperty("db.url", url);
        properties.setProperty("db.user", user);
        properties.setProperty("db.password", password);
    }
}

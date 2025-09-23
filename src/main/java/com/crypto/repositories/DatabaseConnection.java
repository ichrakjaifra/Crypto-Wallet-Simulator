package main.java.com.crypto.repositories;

import main.java.com.crypto.config.DatabaseConfig;
import main.java.com.crypto.utils.LoggerUtil;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            this.connection = DriverManager.getConnection(
                    DatabaseConfig.URL,
                    DatabaseConfig.USERNAME,
                    DatabaseConfig.PASSWORD
            );
            LoggerUtil.logInfo("Connexion à la base de données établie avec succès");
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur de connexion à la base de données: " + e.getMessage());
            throw new RuntimeException("Impossible de se connecter à la base de données", e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(
                        DatabaseConfig.URL,
                        DatabaseConfig.USERNAME,
                        DatabaseConfig.PASSWORD
                );
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la récupération de la connexion: " + e.getMessage());
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                LoggerUtil.logInfo("Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }
    }

    public boolean testConnection() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

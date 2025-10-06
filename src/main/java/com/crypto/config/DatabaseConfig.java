package main.java.com.crypto.config;

public class DatabaseConfig {
    public static final String URL = System.getenv("DB_URL");
    public static final String USERNAME = System.getenv("DB_USERNAME");
    public static final String PASSWORD = System.getenv("DB_PASSWORD");
    public static final String DRIVER = System.getenv("DB_DRIVER");

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver PostgreSQL non trouvé: " + e.getMessage());
        }
    }
}

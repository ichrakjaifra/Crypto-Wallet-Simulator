package main.java.com.crypto.config;

public class DatabaseConfig {
    public static final String URL = "jdbc:postgresql://localhost:5432/crypto_wallet";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "achraf123";
    public static final String DRIVER = "org.postgresql.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver PostgreSQL non trouvé: " + e.getMessage());
        }
    }
}

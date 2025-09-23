import main.java.com.crypto.repositories.DatabaseConnection;
import main.java.com.crypto.utils.LoggerUtil;

public class Main {
    public static void main(String[] args) {
        LoggerUtil.logInfo("=== Test de connexion à PostgreSQL ===");

        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn.testConnection()) {
                LoggerUtil.logInfo("Connexion réussie à la base de données !");
                System.out.println("Connexion réussie !");
            } else {
                LoggerUtil.logError("Connexion échouée à la base de données !");
                System.out.println("Connexion échouée !");
            }
        } catch (Exception e) {
            LoggerUtil.logError("Erreur lors du test de connexion : " + e.getMessage(), e);
            System.out.println("Erreur lors du test de connexion. Vérifiez les logs.");
        }
    }
}

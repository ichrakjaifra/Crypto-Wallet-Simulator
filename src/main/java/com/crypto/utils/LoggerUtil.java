package main.java.com.crypto.utils;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggerUtil {
    private static final Logger logger = Logger.getLogger("CryptoWalletSimulator");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        setupLogger();
    }

    private static void setupLogger() {
        try {

            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);

            FileHandler fileHandler = new FileHandler("logs/crypto-wallet.log", true);
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(java.util.logging.LogRecord record) {
                    return String.format("[%s] %s: %s - %s%n",
                            LocalDateTime.now().format(formatter),
                            record.getLevel(),
                            record.getLoggerName(),
                            record.getMessage());
                }
            });

            logger.addHandler(fileHandler);

        } catch (IOException e) {
            System.err.println("Erreur lors de la configuration du logger: " + e.getMessage());
        }
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warning(message);
    }

    public static void logError(String message) {
        logger.severe(message);
    }

    public static void logError(String message, Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);
    }

    public static void logDebug(String message) {
        logger.fine(message);
    }
}

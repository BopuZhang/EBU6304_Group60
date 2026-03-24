package system;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger utility class for system logging
 */
public class LoggerUtil {

    private static final String LOG_FILE = "logs/app.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Initialize log directory
     */
    static {
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    /**
     * Log user login action
     */
    public static void logLogin(String email, String role) {
        log("LOGIN", "User logged in: " + email + " (" + role + ")");
    }

    /**
     * Log user logout action
     */
    public static void logLogout(String email, String role) {
        log("LOGOUT", "User logged out: " + email + " (" + role + ")");
    }

    /**
     * Log user registration action
     */
    public static void logRegistration(String email, String role) {
        log("REGISTER", "New user registered: " + email + " (" + role + ")");
    }

    /**
     * Log create operation
     */
    public static void logCreate(String entityType, String details) {
        log("CREATE", entityType + " created: " + details);
    }

    /**
     * Log update operation
     */
    public static void logUpdate(String entityType, String details) {
        log("UPDATE", entityType + " updated: " + details);
    }

    /**
     * Log error information
     */
    public static void logError(String errorType, String message) {
        log("ERROR", errorType + " - " + message);
        // Also print to console for immediate visibility
        System.err.println(getTimestamp() + " [ERROR] " + errorType + " - " + message);
    }

    /**
     * Log general information
     */
    public static void logInfo(String message) {
        log("INFO", message);
    }

    /**
     * Main logging method
     */
    private static synchronized void log(String level, String message) {
        String timestamp = getTimestamp();
        String logEntry = timestamp + " [" + level + "] " + message;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    /**
     * Get current timestamp
     */
    private static String getTimestamp() {
        return LocalDateTime.now().format(formatter);
    }
}
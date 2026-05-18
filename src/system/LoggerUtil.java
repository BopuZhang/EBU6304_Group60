package system;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for system-wide logging operations.
 * <p>
 * This class provides static methods for logging various types of events
 * including user actions (login, logout, registration), CRUD operations,
 * errors, and general information. All logs are written to
 * {@code logs/app.log}.
 * </p>
 * <p>
 * Log entries follow the format: {@code timestamp [LEVEL] message}
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2026
 */
public class LoggerUtil {

    /** Path to the log file */
    private static final String LOG_FILE = "logs/app.log";

    /** Date/time formatter for log timestamps */
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Static initializer to ensure the log directory exists.
     */
    static {
        File logDir = new File("logs");
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
    }

    /**
     * Logs a user login action.
     *
     * @param email the user's email
     * @param role  the user's role
     */
    public static void logLogin(String email, String role) {
        log("LOGIN", "User logged in: " + email + " (" + role + ")");
    }

    /**
     * Logs a user logout action.
     *
     * @param email the user's email
     * @param role  the user's role
     */
    public static void logLogout(String email, String role) {
        log("LOGOUT", "User logged out: " + email + " (" + role + ")");
    }

    /**
     * Logs a user registration action.
     *
     * @param email the new user's email
     * @param role  the new user's role
     */
    public static void logRegistration(String email, String role) {
        log("REGISTER", "New user registered: " + email + " (" + role + ")");
    }

    /**
     * Logs an entity creation operation.
     *
     * @param entityType the type of entity created (e.g., "Job", "Application")
     * @param details    additional details about the creation
     */
    public static void logCreate(String entityType, String details) {
        log("CREATE", entityType + " created: " + details);
    }

    /**
     * Logs an entity update operation.
     *
     * @param entityType the type of entity updated
     * @param details    additional details about the update
     */
    public static void logUpdate(String entityType, String details) {
        log("UPDATE", entityType + " updated: " + details);
    }

    /**
     * Logs an entity deletion operation.
     *
     * @param entityType the type of entity deleted
     * @param details    additional details about the deletion
     */
    public static void logDelete(String entityType, String details) {
        log("DELETE", entityType + " deleted: " + details);
    }

    /**
     * Logs an error condition.
     * <p>
     * Error messages are also printed to {@code System.err} for immediate
     * visibility.
     * </p>
     *
     * @param errorType the type/category of error
     * @param message   the error message
     */
    public static void logError(String errorType, String message) {
        log("ERROR", errorType + " - " + message);
        System.err.println(getTimestamp() + " [ERROR] " + errorType + " - " + message);
    }

    /**
     * Logs general information.
     *
     * @param message the information message
     */
    public static void logInfo(String message) {
        log("INFO", message);
    }

    /**
     * Internal method to write a log entry to the log file.
     * <p>
     * This method is synchronized to ensure thread-safe file writes.
     * </p>
     *
     * @param level   the log level (e.g., "INFO", "ERROR", "LOGIN")
     * @param message the log message
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
     * Returns the current timestamp formatted for log entries.
     *
     * @return the formatted timestamp string
     */
    private static String getTimestamp() {
        return LocalDateTime.now().format(formatter);
    }
}
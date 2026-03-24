package system;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * File utility class for data persistence
 */
public class FileUtil {

    private static final String DATA_DIR = "data/";

    /**
     * Save user list to file
     */
    public static void saveUsers(List<User> users) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
                LoggerUtil.logInfo("Created data directory: " + DATA_DIR);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "users.txt"))) {
                for (User user : users) {
                    writer.write(user.toString());
                    writer.newLine();
                }
                LoggerUtil.logInfo("Users saved successfully. Total: " + users.size());
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to save users: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load user list from file
     */
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(DATA_DIR + "users.txt");

        if (!file.exists()) {
            LoggerUtil.logInfo("Users file does not exist, starting with empty list");
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    User user = new User(parts[0], parts[1], parts[2], parts[3]);
                    users.add(user);
                } else {
                    LoggerUtil.logError("File Format", "Invalid line format: " + line);
                }
            }
            LoggerUtil.logInfo("Users loaded successfully. Total: " + users.size());
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
}
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

    // ========== Job Operations ==========
    public static List<Job> loadJobs() {
        List<Job> jobs = new ArrayList<>();
        File file = new File(DATA_DIR + "jobs.txt");
        if (!file.exists()) return jobs;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Job job = Job.fromString(line);
                if (job != null) jobs.add(job);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load jobs: " + e.getMessage());
        }
        return jobs;
    }

    public static void saveJobs(List<Job> jobs) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "jobs.txt"))) {
                for (Job job : jobs) {
                    writer.write(job.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to save jobs: " + e.getMessage());
        }
    }

    // ========== Application Operations ==========
    public static List<Application> loadApplications() {
        List<Application> apps = new ArrayList<>();
        File file = new File(DATA_DIR + "applications.txt");
        if (!file.exists()) return apps;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Application app = Application.fromString(line);
                if (app != null) apps.add(app);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load applications: " + e.getMessage());
        }
        return apps;
    }

    public static void saveApplications(List<Application> apps) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "applications.txt"))) {
                for (Application app : apps) {
                    writer.write(app.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to save applications: " + e.getMessage());
        }
    }
    // ========== Profile Operations ==========
    public static List<Profile> loadProfiles() {
        List<Profile> profiles = new ArrayList<>();
        File file = new File(DATA_DIR + "profiles.txt");
        if (!file.exists()) return profiles;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Profile profile = Profile.fromString(line);
                if (profile != null) profiles.add(profile);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load profiles: " + e.getMessage());
        }
        return profiles;
    }

    public static void saveProfiles(List<Profile> profiles) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "profiles.txt"))) {
                for (Profile profile : profiles) {
                    writer.write(profile.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to save profiles: " + e.getMessage());
        }
    }

    public static Profile getProfileByEmail(String email) {
        List<Profile> profiles = loadProfiles();
        System.out.println("=== getProfileByEmail ===");
        System.out.println("Looking for email: " + email);
        System.out.println("Total profiles: " + profiles.size());

        for (Profile profile : profiles) {
            System.out.println("Checking profile email: " + profile.getEmail());
            if (profile.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Found match!");
                return profile;
            }
        }
        System.out.println("No match found");
        return null;
    }

    // ========== Notification Operations ==========
    public static List<Notification> loadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        File file = new File(DATA_DIR + "notifications.txt");
        if (!file.exists()) return notifications;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Notification notification = Notification.fromString(line);
                if (notification != null) notifications.add(notification);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load notifications: " + e.getMessage());
        }
        return notifications;
    }

    public static void saveNotifications(List<Notification> notifications) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_DIR + "notifications.txt"))) {
                for (Notification notification : notifications) {
                    writer.write(notification.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to save notifications: " + e.getMessage());
        }
    }

    public static List<Notification> getNotificationsByRecipient(String email) {
        List<Notification> allNotifications = loadNotifications();
        List<Notification> userNotifications = new ArrayList<>();
        for (Notification notification : allNotifications) {
            if (notification.getRecipientEmail().equalsIgnoreCase(email)) {
                userNotifications.add(notification);
            }
        }
        return userNotifications;
    }

    public static int getUnreadNotificationCount(String email) {
        List<Notification> notifications = getNotificationsByRecipient(email);
        int count = 0;
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                count++;
            }
        }
        return count;
    }

    public static void sendNotification(String recipientEmail, String title, String content, String type) {
        List<Notification> notifications = loadNotifications();
        String notificationId = "NOT" + System.currentTimeMillis();
        String createTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        Notification notification = new Notification(notificationId, recipientEmail, title, content, type, createTime, false);
        notifications.add(notification);
        saveNotifications(notifications);
        LoggerUtil.logInfo("Notification sent to: " + recipientEmail + " - " + title);
    }

    public static void markNotificationAsRead(String notificationId) {
        List<Notification> notifications = loadNotifications();
        for (Notification notification : notifications) {
            if (notification.getNotificationId().equals(notificationId)) {
                notification.setRead(true);
                break;
            }
        }
        saveNotifications(notifications);
    }

    public static void markAllNotificationsAsRead(String email) {
        List<Notification> notifications = loadNotifications();
        for (Notification notification : notifications) {
            if (notification.getRecipientEmail().equalsIgnoreCase(email)) {
                notification.setRead(true);
            }
        }
        saveNotifications(notifications);
    }
}
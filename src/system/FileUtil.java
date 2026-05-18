package system;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file-based data persistence operations.
 * <p>
 * This class provides static methods for loading and saving all entity types
 * (Users, Jobs, Applications, Profiles, Notifications) to CSV-formatted text
 * files.
 * All data is stored in the {@code data/} directory relative to the application
 * root.
 * </p>
 * <p>
 * The class also provides convenience methods for common queries such as
 * finding a profile by email or retrieving notifications for a specific user.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2026
 */
public class FileUtil {

    /** Directory where all data files are stored */
    private static final String DATA_DIR = "data/";

    /**
     * Saves a list of users to the users data file.
     * <p>
     * Creates the data directory if it does not exist.
     * Each user is written as a CSV line via {@link User#toString()}.
     * </p>
     *
     * @param users the list of users to save
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
     * Loads the list of users from the users data file.
     * <p>
     * Returns an empty list if the file does not exist.
     * Invalid lines are logged as errors and skipped.
     * </p>
     *
     * @return the list of users (never null)
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

    /**
     * Loads the list of jobs from the jobs data file.
     *
     * @return the list of jobs (never null)
     */
    public static List<Job> loadJobs() {
        List<Job> jobs = new ArrayList<>();
        File file = new File(DATA_DIR + "jobs.txt");
        if (!file.exists())
            return jobs;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Job job = Job.fromString(line);
                if (job != null)
                    jobs.add(job);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load jobs: " + e.getMessage());
        }
        return jobs;
    }

    /**
     * Saves a list of jobs to the jobs data file.
     *
     * @param jobs the list of jobs to save
     */
    public static void saveJobs(List<Job> jobs) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists())
                dir.mkdirs();

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

    /**
     * Loads the list of applications from the applications data file.
     *
     * @return the list of applications (never null)
     */
    public static List<Application> loadApplications() {
        List<Application> apps = new ArrayList<>();
        File file = new File(DATA_DIR + "applications.txt");
        if (!file.exists())
            return apps;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Application app = Application.fromString(line);
                if (app != null)
                    apps.add(app);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load applications: " + e.getMessage());
        }
        return apps;
    }

    /**
     * Saves a list of applications to the applications data file.
     *
     * @param apps the list of applications to save
     */
    public static void saveApplications(List<Application> apps) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists())
                dir.mkdirs();

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

    /**
     * Loads the list of profiles from the profiles data file.
     *
     * @return the list of profiles (never null)
     */
    public static List<Profile> loadProfiles() {
        List<Profile> profiles = new ArrayList<>();
        File file = new File(DATA_DIR + "profiles.txt");
        if (!file.exists())
            return profiles;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Profile profile = Profile.fromString(line);
                if (profile != null)
                    profiles.add(profile);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load profiles: " + e.getMessage());
        }
        return profiles;
    }

    /**
     * Saves a list of profiles to the profiles data file.
     *
     * @param profiles the list of profiles to save
     */
    public static void saveProfiles(List<Profile> profiles) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists())
                dir.mkdirs();

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

    /**
     * Finds a profile by email address.
     * <p>
     * Performs a case-insensitive search.
     * </p>
     *
     * @param email the email to search for
     * @return the matching Profile, or null if not found
     */
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

    /**
     * Loads the list of notifications from the notifications data file.
     *
     * @return the list of notifications (never null)
     */
    public static List<Notification> loadNotifications() {
        List<Notification> notifications = new ArrayList<>();
        File file = new File(DATA_DIR + "notifications.txt");
        if (!file.exists())
            return notifications;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                Notification notification = Notification.fromString(line);
                if (notification != null)
                    notifications.add(notification);
            }
        } catch (IOException e) {
            LoggerUtil.logError("File Operation", "Failed to load notifications: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Saves a list of notifications to the notifications data file.
     *
     * @param notifications the list of notifications to save
     */
    public static void saveNotifications(List<Notification> notifications) {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists())
                dir.mkdirs();

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

    /**
     * Retrieves all notifications for a specific recipient.
     * <p>
     * Performs a case-insensitive email comparison.
     * </p>
     *
     * @param email the recipient's email
     * @return the list of notifications for the recipient (never null)
     */
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

    /**
     * Counts the number of unread notifications for a user.
     *
     * @param email the recipient's email
     * @return the count of unread notifications
     */
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

    /**
     * Sends a new notification to a recipient.
     * <p>
     * Generates a unique notification ID and timestamp automatically.
     * The notification is initially marked as unread.
     * </p>
     *
     * @param recipientEmail the recipient's email
     * @param title          the notification title
     * @param content        the notification content
     * @param type           the notification type
     */
    public static void sendNotification(String recipientEmail, String title, String content, String type) {
        List<Notification> notifications = loadNotifications();
        String notificationId = "NOT" + System.currentTimeMillis();
        String createTime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        Notification notification = new Notification(notificationId, recipientEmail, title, content, type, createTime,
                false);
        notifications.add(notification);
        saveNotifications(notifications);
        LoggerUtil.logInfo("Notification sent to: " + recipientEmail + " - " + title);
    }

    /**
     * Marks a specific notification as read.
     *
     * @param notificationId the ID of the notification to mark
     */
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

    /**
     * Marks all notifications for a user as read.
     *
     * @param email the recipient's email
     */
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
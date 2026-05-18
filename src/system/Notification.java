package system;

import java.io.Serializable;

/**
 * Represents a notification message in the TA Management System.
 * <p>
 * Notifications are sent to users to inform them about important events
 * such as application status changes, new applications, or system updates.
 * Each notification has a recipient, title, content, type, and read status.
 * </p>
 * <p>
 * The class implements {@link Serializable} to support persistence via file
 * storage.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2026
 */
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Unique identifier for this notification */
    private String notificationId;

    /** Email of the notification recipient */
    private String recipientEmail;

    /** Notification title/subject */
    private String title;

    /** Notification content/body */
    private String content;

    /** Notification type (e.g., "ACCEPT", "REJECT", "APPLY", "INFO") */
    private String type;

    /** Timestamp when the notification was created */
    private String createTime;

    /** Whether the notification has been read */
    private boolean read;

    /**
     * Constructs a new Notification.
     *
     * @param notificationId unique identifier
     * @param recipientEmail recipient's email
     * @param title          notification title
     * @param content        notification content
     * @param type           notification type
     * @param createTime     creation timestamp
     * @param read           initial read status
     */
    public Notification(String notificationId, String recipientEmail, String title,
            String content, String type, String createTime, boolean read) {
        this.notificationId = notificationId;
        this.recipientEmail = recipientEmail;
        this.title = title;
        this.content = content;
        this.type = type;
        this.createTime = createTime;
        this.read = read;
    }

    /**
     * Returns the notification ID.
     *
     * @return the notification ID
     */
    public String getNotificationId() {
        return notificationId;
    }

    /**
     * Returns the recipient's email.
     *
     * @return the recipient email
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }

    /**
     * Returns the notification title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the notification content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns the notification type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the creation timestamp.
     *
     * @return the create time
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * Returns whether the notification has been read.
     *
     * @return true if read, false otherwise
     */
    public boolean isRead() {
        return read;
    }

    /**
     * Sets the read status.
     *
     * @param read the new read status
     */
    public void setRead(boolean read) {
        this.read = read;
    }

    /**
     * Returns a string representation of this notification in CSV format.
     * <p>
     * Format:
     * {@code notificationId,recipientEmail,title,content,type,createTime,read}
     * </p>
     *
     * @return the CSV string representation
     */
    @Override
    public String toString() {
        return notificationId + "," + recipientEmail + "," + title + "," +
                content + "," + type + "," + createTime + "," + read;
    }

    /**
     * Parses a CSV string to create a Notification object.
     *
     * @param line the CSV string to parse
     * @return the parsed Notification, or null if the line format is invalid
     */
    public static Notification fromString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            return new Notification(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], Boolean.parseBoolean(parts[6]));
        }
        return null;
    }
}
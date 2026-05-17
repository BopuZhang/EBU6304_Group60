package system;

import java.io.Serializable;

public class Notification implements Serializable {
    private String notificationId;
    private String recipientEmail;
    private String title;
    private String content;
    private String type;
    private String createTime;
    private boolean read;

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

    public String getNotificationId() { return notificationId; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getType() { return type; }
    public String getCreateTime() { return createTime; }
    public boolean isRead() { return read; }

    public void setRead(boolean read) { this.read = read; }

    @Override
    public String toString() {
        return notificationId + "," + recipientEmail + "," + title + "," +
                content + "," + type + "," + createTime + "," + read;
    }

    public static Notification fromString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length >= 7) {
            return new Notification(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], Boolean.parseBoolean(parts[6]));
        }
        return null;
    }
}

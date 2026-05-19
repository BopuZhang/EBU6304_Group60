package system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * The notification center frame for viewing and managing notifications.
 * <p>
 * This frame displays all notifications for the current user, allowing them
 * to mark individual notifications as read or mark all as read.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class NotificationFrame extends JFrame {

    /** The currently logged-in user */
    private final User currentUser;

    /** Panel containing the notifications list */
    private JPanel notificationsPanel;

    /** Panel containing individual notification cards */
    private JPanel listPanel;

    /** List of notifications for the current user */
    private List<Notification> notifications;

    /** Callback to be executed when read status changes (e.g., update dashboard badge) */
    private Runnable onReadChangeCallback;

    /**
     * Constructs the notification frame for the specified user.
     *
     * @param user the logged-in user
     */
    public NotificationFrame(User user) {
        this(user, null);
    }

    /**
     * Constructs the notification frame with a callback for read status changes.
     *
     * @param user               the logged-in user
     * @param onReadChangeCallback callback to run when notifications are marked read
     */
    public NotificationFrame(User user, Runnable onReadChangeCallback) {
        this.currentUser = user;
        this.onReadChangeCallback = onReadChangeCallback;

        setTitle("Notification Center");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        notifications = FileUtil.getNotificationsByRecipient(currentUser.getEmail());

        initUI();
        setVisible(true);
    }

    /**
     * Initializes the user interface components.
     */
    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📬 My Notifications");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton markAllReadBtn = UIHelper.createButton("Mark All as Read", UIHelper.SECONDARY_COLOR);
        markAllReadBtn.addActionListener(e -> markAllAsRead());
        headerPanel.add(markAllReadBtn, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BorderLayout());
        notificationsPanel.setBackground(UIHelper.BACKGROUND_COLOR);

        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(UIHelper.BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBackground(UIHelper.BACKGROUND_COLOR);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        refreshNotifications(listPanel);

        notificationsPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(notificationsPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the top navigation bar.
     *
     * @return the top bar panel
     */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(12, 30, 12, 30)));
        topBar.setPreferredSize(new Dimension(getWidth(), 60));

        JLabel logoLabel = new JLabel("📋 TA Recruitment System");
        logoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        logoLabel.setForeground(UIHelper.PRIMARY_COLOR);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));
        closeBtn.setForeground(new Color(150, 150, 150));
        closeBtn.setBackground(Color.WHITE);
        closeBtn.setBorder(null);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());

        topBar.add(logoLabel, BorderLayout.WEST);
        topBar.add(closeBtn, BorderLayout.EAST);

        return topBar;
    }

    /**
     * Refreshes the notifications list from storage.
     *
     * @param listPanel the panel to populate with notifications
     */
    private void refreshNotifications(JPanel listPanel) {
        listPanel.removeAll();
        notifications = FileUtil.getNotificationsByRecipient(currentUser.getEmail());

        if (notifications.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);

            JLabel emptyIcon = new JLabel("📭");
            emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyText = new JLabel("No notifications yet");
            emptyText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
            emptyText.setForeground(new Color(150, 150, 150));
            emptyText.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(Box.createVerticalStrut(100));
            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(20));
            emptyPanel.add(emptyText);

            listPanel.add(emptyPanel);
        } else {
            notifications.sort((n1, n2) -> n2.getCreateTime().compareTo(n1.getCreateTime()));

            listPanel.add(Box.createVerticalStrut(10));
            for (Notification notification : notifications) {
                JPanel notificationCard = createNotificationCard(notification);
                listPanel.add(notificationCard);
                listPanel.add(Box.createVerticalStrut(10));
            }
            listPanel.add(Box.createVerticalGlue());
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    /**
     * Creates a card panel for displaying a single notification.
     *
     * @param notification the notification to display
     * @return the card panel
     */
    private JPanel createNotificationCard(Notification notification) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(notification.isRead() ? new Color(250, 250, 250) : Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        notification.isRead() ? new Color(220, 220, 220) : UIHelper.PRIMARY_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        card.setPreferredSize(new Dimension(600, 90));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        String typeIcon = getTypeIcon(notification.getType());
        JLabel titleLabel = new JLabel(typeIcon + " " + notification.getTitle());
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titleLabel.setForeground(notification.isRead() ? new Color(100, 100, 100) : UIHelper.PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel timeLabel = new JLabel(formatTime(notification.getCreateTime()));
        timeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        timeLabel.setForeground(new Color(150, 150, 150));
        headerPanel.add(timeLabel, BorderLayout.EAST);

        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(8));

        JLabel contentLabel = new JLabel(
                "<html><body style='width: 500px'>" + notification.getContent() + "</body></html>");
        contentLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        contentLabel.setForeground(new Color(80, 80, 80));
        contentPanel.add(contentLabel);

        if (!notification.isRead()) {
            JLabel unreadBadge = new JLabel("●");
            unreadBadge.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            unreadBadge.setForeground(UIHelper.DANGER_COLOR);
            unreadBadge.setBorder(new EmptyBorder(0, 0, 0, 10));
            card.add(unreadBadge, BorderLayout.EAST);
        }

        card.add(contentPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!notification.isRead()) {
                    FileUtil.markNotificationAsRead(notification.getNotificationId());
                    refreshNotifications(listPanel);
                    // Execute callback to update dashboard badge
                    if (onReadChangeCallback != null) {
                        onReadChangeCallback.run();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(notification.isRead() ? new Color(250, 250, 250) : Color.WHITE);
            }
        });

        return card;
    }

    private String getTypeIcon(String type) {
        switch (type) {
            case "APPLICATION":
                return "📝";
            case "STATUS_UPDATE":
                return "📊";
            case "SYSTEM":
                return "⚙️";
            case "REMINDER":
                return "⏰";
            default:
                return "📬";
        }
    }

    /**
     * Formats a timestamp string to a more readable format.
     *
     * @param timeStr the timestamp string in "yyyy-MM-dd HH:mm:ss" format
     * @return the formatted string in "MMM dd, HH:mm" format
     */
    private String formatTime(String timeStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, HH:mm");
            Date date = inputFormat.parse(timeStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return timeStr;
        }
    }

    /**
     * Marks all notifications as read for the current user.
     */
    private void markAllAsRead() {
        FileUtil.markAllNotificationsAsRead(currentUser.getEmail());
        refreshNotifications(listPanel);
        UIHelper.showInfoDialog(this, "All notifications marked as read", "Success");
        // Execute callback to update dashboard badge
        if (onReadChangeCallback != null) {
            onReadChangeCallback.run();
        }
    }
}
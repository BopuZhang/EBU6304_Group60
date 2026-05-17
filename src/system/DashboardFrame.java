package system;

import system.ta.*;
import system.mo.*;
import system.admin.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Modern card-based dashboard with no sidebar.
 * All functionality is accessed via beautifully designed cards.
 */
public class DashboardFrame extends JFrame {
    private final User currentUser;
    private final List<User> users;

    public DashboardFrame(User user, List<User> users) {
        this.currentUser = user;
        this.users = users;

        setTitle("TA Recruitment System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ----- Top Bar -----
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        // ----- Main Content (Card Grid) -----
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Welcome header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getName() + "!");
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
        welcomeLabel.setForeground(UIHelper.PRIMARY_COLOR);
        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole());
        roleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        roleLabel.setForeground(new Color(120, 120, 120));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(roleLabel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Card grid panel
        JPanel cardGrid = new JPanel(new GridBagLayout());
        cardGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        addCardsToGrid(cardGrid, gbc);
        mainPanel.add(cardGrid, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        topBar.setPreferredSize(new Dimension(getWidth(), 70));

        // Logo / System name
        JLabel logoLabel = new JLabel("📋 TA Recruitment System");
        logoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        logoLabel.setForeground(UIHelper.PRIMARY_COLOR);

        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);

        int unreadCount = FileUtil.getUnreadNotificationCount(currentUser.getEmail());
        JButton notificationBtn = new JButton("📬");
        notificationBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        notificationBtn.setBackground(Color.WHITE);
        notificationBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        notificationBtn.setFocusPainted(false);
        notificationBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationBtn.setToolTipText("Notifications" + (unreadCount > 0 ? " (" + unreadCount + " unread)" : ""));
        notificationBtn.addActionListener(e -> new NotificationFrame(currentUser));
        notificationBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                notificationBtn.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent e) {
                notificationBtn.setBackground(Color.WHITE);
            }
        });
        userPanel.add(notificationBtn);

        if (unreadCount > 0) {
            JLabel badgeLabel = new JLabel(String.valueOf(unreadCount));
            badgeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            badgeLabel.setForeground(Color.WHITE);
            badgeLabel.setBackground(new Color(220, 50, 50));
            badgeLabel.setOpaque(true);
            badgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
            userPanel.add(badgeLabel);
        }

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));

        JLabel userLabel = new JLabel(currentUser.getName());
        userLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        logoutBtn.setForeground(new Color(200, 70, 70));
        logoutBtn.setBackground(Color.WHITE);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutBtn.setForeground(Color.WHITE);
                logoutBtn.setBackground(new Color(200, 70, 70));
            }
            public void mouseExited(MouseEvent e) {
                logoutBtn.setForeground(new Color(200, 70, 70));
                logoutBtn.setBackground(Color.WHITE);
            }
        });

        userPanel.add(userIcon);
        userPanel.add(userLabel);
        userPanel.add(logoutBtn);

        topBar.add(logoLabel, BorderLayout.WEST);
        topBar.add(userPanel, BorderLayout.EAST);

        // Subtle bottom border
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                topBar.getBorder()
        ));

        return topBar;
    }

    private void addCardsToGrid(JPanel grid, GridBagConstraints gbc) {
        String role = currentUser.getRole();
        int col = 0, row = 0;

        if ("TA".equals(role)) {
            addCard(grid, gbc, col++, row, "📄 My Profile", "View your personal and account information",
                    e -> new TAProfileViewFrame(currentUser));
            addCard(grid, gbc, col++, row, "✨ Create Profile", "Set up your TA profile with details",
                    e -> new TAProfileCreateFrame(currentUser));
            addCard(grid, gbc, col++, row, "✏️ Edit Profile", "Update your existing profile information",
                    e -> new TAProfileEditFrame(currentUser));
            col = 0; row++;
            addCard(grid, gbc, col++, row, "📎 Upload CV", "Upload your CV in PDF format (max 5MB)",
                    e -> new TACVUploadFrame(currentUser));
            addCard(grid, gbc, col++, row, "🔍 Browse Positions", "View available TA positions and apply",
                    e -> new TAJobListFrame(currentUser));
            addCard(grid, gbc, col++, row, "📊 Application Status", "Track the status of your applications",
                    e -> new TAApplicationStatusFrame(currentUser));
        } else if ("MO".equals(role)) {
            addCard(grid, gbc, col++, row, "📢 Post Position", "Create a new job opening",
                    e -> new MOPostJobFrame(currentUser));
            addCard(grid, gbc, col++, row, "📋 My Positions", "View and manage your posted positions",
                    e -> new MOMyJobsFrame(currentUser));
            addCard(grid, gbc, col++, row, "👥 View Applicants", "Review and process applications",
                    e -> new MOViewApplicantsFrame(currentUser));
        } else if ("Admin".equals(role)) {
            addCard(grid, gbc, col++, row, "👨‍🎓 All TAs", "View and filter all teaching assistants",
                    e -> new AdminViewAllTAsFrame(users));
            addCard(grid, gbc, col++, row, "📈 TA Workload", "Overview of TA workload distribution",
                    e -> new AdminViewWorkloadFrame(users));
            addCard(grid, gbc, col++, row, "⚙️ Manage Workload", "Adjust TA assignments",
                    e -> new AdminManageWorkloadFrame(users));
            col = 0; row++;
            addCard(grid, gbc, col++, row, "📌 All Positions", "View all job postings",
                    e -> new AdminViewAllJobsFrame());
            addCard(grid, gbc, col++, row, "📜 System Logs", "Monitor system activity logs",
                    e -> new AdminViewLogsFrame());
        }

        // Fill empty cells with invisible placeholders for grid balance
        while (row < 2) {
            while (col < 3) {
                addPlaceholderCard(grid, gbc, col++, row);
            }
            col = 0;
            row++;
        }
    }

    private void addCard(JPanel grid, GridBagConstraints gbc, int col, int row,
                         String title, String description, java.awt.event.ActionListener action) {
        gbc.gridx = col;
        gbc.gridy = row;

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon (using emoji for simplicity)
        String iconChar = title.substring(0, title.indexOf(' '));
        JLabel iconLabel = new JLabel(iconChar);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        iconLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title.substring(title.indexOf(' ') + 1));
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

        JLabel descLabel = new JLabel("<html><body style='width: 180px'>" + description + "</body></html>");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(iconLabel);
        card.add(titleLabel);
        card.add(descLabel);
        card.add(Box.createVerticalGlue());

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 250));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UIHelper.PRIMARY_COLOR, 1, true),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
                        BorderFactory.createEmptyBorder(25, 25, 25, 25)
                ));
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        grid.add(card, gbc);
    }

    private void addPlaceholderCard(JPanel grid, GridBagConstraints gbc, int col, int row) {
        gbc.gridx = col;
        gbc.gridy = row;
        JPanel placeholder = new JPanel();
        placeholder.setOpaque(false);
        grid.add(placeholder, gbc);
    }

    private void logout() {
        int confirm = UIHelper.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            LoggerUtil.logLogout(currentUser.getEmail(), currentUser.getRole());
            dispose();
            new LoginFrame();
        }
    }
}
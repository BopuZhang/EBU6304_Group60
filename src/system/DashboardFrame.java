package system;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private List<User> users;
    private JPanel contentPanel;

    public DashboardFrame(User user, List<User> users) {
        this.currentUser = user;
        this.users = users;

        setTitle("TA Recruitment System - " + user.getRole() + " Dashboard");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // Top header panel
        JPanel topPanel = new JPanel();
        topPanel.setBackground(UIHelper.PRIMARY_COLOR);
        topPanel.setPreferredSize(new Dimension(900, 70));
        topPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = UIHelper.createButton("Logout", new Color(200, 70, 70));
        logoutBtn.setPreferredSize(new Dimension(100, 40));
        logoutBtn.addActionListener(e -> logout());
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Left menu panel
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        menuPanel.setPreferredSize(new Dimension(220, 650));
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Add menu items based on role
        if (currentUser.getRole().equals("TA")) {
            addMenuItem(menuPanel, "Create Personal Profile", e -> createProfile());
            addMenuItem(menuPanel, "Edit Personal Profile", e -> editProfile());
            addMenuItem(menuPanel, "Upload CV", e -> uploadCV());
            addMenuItem(menuPanel, "View Available Positions", e -> viewJobs()); // TA-05
            addMenuItem(menuPanel, "Apply for Position", e -> applyForJob()); // TA-06 新增菜单
            addMenuItem(menuPanel, "Check Application Status", e -> viewApplications()); // TA-07
        } else if (currentUser.getRole().equals("MO")) {
            addMenuItem(menuPanel, "Post Position", e -> postJob());
            addMenuItem(menuPanel, "View Positions I've Posted", e -> viewMyJobs());
            addMenuItem(menuPanel, "View Applicants", e -> viewApplicants());
            addMenuItem(menuPanel, "Edit Posted Position", e -> editPostedPosition());
        } else { // Admin
            addMenuItem(menuPanel, "View All Teaching Assistants", e -> viewAllTAs());
            addMenuItem(menuPanel, "View TA Workload", e -> viewWorkload());
            addMenuItem(menuPanel, "Manage Workload", e -> manageWorkload());
            addMenuItem(menuPanel, "View All Positions", e -> viewAllJobs());
            addMenuItem(menuPanel, "View System Logs", e -> viewLogs());
        }

        // Add glue at the end to push items to top
        menuPanel.add(Box.createVerticalGlue());

        add(menuPanel, BorderLayout.WEST);

        // Right content panel
        contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel defaultLabel = new JLabel("Select an option from the menu", SwingConstants.CENTER);
        defaultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        defaultLabel.setForeground(new Color(150, 150, 150));
        contentPanel.add(defaultLabel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void addMenuItem(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(Color.WHITE);
        button.setForeground(UIHelper.PRIMARY_COLOR);
        button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(button);
    }

    private void setContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ========== Helper Methods ==========

    private void addInfoRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel();
        row.setLayout(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setMaximumSize(new Dimension(600, 35));
        row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(240, 240, 240)));

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        row.add(labelLabel, BorderLayout.WEST);
        row.add(valueLabel, BorderLayout.EAST);

        panel.add(row);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void showPlaceholder(String featureName) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BorderLayout());

        JLabel placeholder = new JLabel(featureName + " will be implemented soon", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        placeholder.setForeground(new Color(150, 150, 150));
        panel.add(placeholder, BorderLayout.CENTER);

        setContent(panel);
    }

    private int getTACount() {
        int count = 0;
        for (User user : users) {
            if (user.getRole().equals("TA")) {
                count++;
            }
        }
        return count;
    }

    // ========== TA Functions ==========

    private void createProfile() {
        showPlaceholder("Create Personal Profile");
    }

    private void editProfile() {
        showPlaceholder("Edit Personal Profile");
    }

    private void viewProfile() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        addInfoRow(panel, "Email:", currentUser.getEmail());
        addInfoRow(panel, "Name:", currentUser.getName());
        addInfoRow(panel, "Role:", currentUser.getRole());

        setContent(panel);
    }

    private void uploadCV() {
        showPlaceholder("Upload CV");
    }

    // TA-05 查看可用岗位（优化版）
    private void viewJobs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Available Positions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        StringBuilder sb = new StringBuilder();
        sb.append("===== AVAILABLE POSITIONS =====\n\n");

        try {
            File jobFile = new File("data/jobs.txt");
            // SYS-01 数据持久化：文件不存在则创建
            if (!jobFile.exists()) {
                jobFile.getParentFile().mkdirs(); // 创建data文件夹
                jobFile.createNewFile();
                sb.append("No positions available at this time.\n");
            } else {
                BufferedReader br = new BufferedReader(new FileReader(jobFile));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    sb.append(line).append("\n");
                }
                br.close();
                // 无岗位数据时提示
                if (sb.toString().equals("===== AVAILABLE POSITIONS =====\n\n")) {
                    sb.append("No positions available at this time.\n");
                }
            }
            LoggerUtil.logInfo("TA " + currentUser.getEmail() + " viewed available positions"); // SYS-03 日志
        } catch (Exception e) {
            e.printStackTrace(); // SYS-02 错误处理
            sb.append("Failed to load positions: ").append(e.getMessage()).append("\n");
            LoggerUtil.logError("FILE_ERROR", "TA " + currentUser.getEmail() + " failed to load jobs: " + e.getMessage());
        }

        area.setText(sb.toString());
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(sp, BorderLayout.CENTER);

        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // TA-06 申请岗位（新增核心方法）
    private void applyForJob() {
        // 1. 弹出输入框输入Job ID
        String jobId = JOptionPane.showInputDialog(this,
                "Enter Job ID to apply (e.g., J001):",
                "Apply for Position",
                JOptionPane.PLAIN_MESSAGE);

        // 2. 校验输入
        if (jobId == null || jobId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Job ID cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        jobId = jobId.trim();

        // 3. 校验岗位是否存在
        boolean jobExists = false;
        try {
            File jobFile = new File("data/jobs.txt");
            if (jobFile.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(jobFile));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.startsWith(jobId + ",")) {
                        jobExists = true;
                        break;
                    }
                }
                br.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to verify job ID!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!jobExists) {
            JOptionPane.showMessageDialog(this, "Job ID " + jobId + " does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 4. 构建申请记录（格式：申请时间,申请人邮箱,Job ID,状态）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String applyTime = sdf.format(new Date());
        String applicationRecord = String.format("%s,%s,%s,pending",
                applyTime,
                currentUser.getEmail(),
                jobId);

        // 5. 写入applications.txt（追加模式）
        try {
            File appFile = new File("data/applications.txt");
            appFile.getParentFile().mkdirs(); // 确保data文件夹存在
            BufferedWriter bw = new BufferedWriter(new FileWriter(appFile, true));
            bw.write(applicationRecord);
            bw.newLine();
            bw.close();

            JOptionPane.showMessageDialog(this, "Application submitted successfully!\nJob ID: " + jobId, "Success", JOptionPane.INFORMATION_MESSAGE);
            LoggerUtil.logInfo("TA " + currentUser.getEmail() + " applied for job: " + jobId); // SYS-03 日志
        } catch (Exception e) {
            e.printStackTrace(); // SYS-02 错误处理
            JOptionPane.showMessageDialog(this, "Failed to submit application!", "Error", JOptionPane.ERROR_MESSAGE);
            LoggerUtil.logError("APPLICATION_ERROR", "TA " + currentUser.getEmail() + " failed to apply for job " + jobId + ": " + e.getMessage());
        }

        // 6. 提交后自动跳转到申请状态页面（TA-07）
        viewApplications();
    }

    // TA-07 查看申请状态（优化版）
    private void viewApplications() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("My Application Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        // 申请状态展示区域
        JTextArea area = new JTextArea();
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        StringBuilder sb = new StringBuilder();
        sb.append("===== MY APPLICATION STATUS =====\n\n");
        sb.append(String.format("%-20s %-15s %-12s\n", "Apply Time", "Job ID", "Status"));
        sb.append("---------------------------------------------\n");

        // 读取并筛选当前TA的申请
        boolean hasApplication = false;
        try {
            File appFile = new File("data/applications.txt");
            // SYS-01 数据持久化：文件不存在则创建
            if (!appFile.exists()) {
                appFile.getParentFile().mkdirs();
                appFile.createNewFile();
                sb.append("No applications submitted yet.\n");
            } else {
                BufferedReader br = new BufferedReader(new FileReader(appFile));
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    // 解析记录：申请时间,申请人邮箱,Job ID,状态
                    String[] parts = line.split(",");
                    if (parts.length != 4) continue; // 跳过格式错误的行（SYS-02）

                    String applyTime = parts[0];
                    String applicantEmail = parts[1];
                    String jobId = parts[2];
                    String status = parts[3];

                    // 筛选当前TA的申请
                    if (applicantEmail.equals(currentUser.getEmail())) {
                        hasApplication = true;
                        // 美化状态显示
                        String statusDisplay = switch (status.toLowerCase()) {
                            case "pending" -> "Pending ⏳";
                            case "approved" -> "Approved ✅";
                            case "rejected" -> "Rejected ❌";
                            default -> "Unknown ❓";
                        };
                        sb.append(String.format("%-20s %-15s %-12s\n", applyTime, jobId, statusDisplay));
                    }
                }
                br.close();
                if (!hasApplication) {
                    sb.append("No applications submitted yet.\n");
                }
            }
            LoggerUtil.logInfo("TA " + currentUser.getEmail() + " checked application status"); // SYS-03 日志
        } catch (Exception e) {
            e.printStackTrace(); // SYS-02 错误处理
            sb.append("Failed to load application status: ").append(e.getMessage()).append("\n");
            LoggerUtil.logError("APPLICATION_STATUS_ERROR", "TA " + currentUser.getEmail() + " failed to load application status: " + e.getMessage());
        }

        area.setText(sb.toString());
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(sp, BorderLayout.CENTER);

        contentPanel.removeAll();
        contentPanel.add(panel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ========== MO Functions ==========
    private void postJob() {
        showPlaceholder("Post Position");
    }

    private void viewMyJobs() {
        showPlaceholder("View Positions I've Posted");
    }

    private void viewApplicants() {
        showPlaceholder("View Applicants");
    }

    private void editPostedPosition() {
        showPlaceholder("Edit Posted Position");
    }

    // ========== Admin Functions ==========
    private void viewAllTAs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("All Teaching Assistants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        for (User user : users) {
            if (user.getRole().equals("TA")) {
                addInfoRow(panel, user.getName(), user.getEmail());
            }
        }

        if (getTACount() == 0) {
            JLabel emptyLabel = new JLabel("No TA registered yet");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(emptyLabel);
        }

        setContent(panel);
    }

    private void viewWorkload() {
        showPlaceholder("View TA Workload");
    }

    private void manageWorkload() {
        showPlaceholder("Manage Workload");
    }

    private void viewAllJobs() {
        showPlaceholder("View All Positions");
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Log logout action
            LoggerUtil.logLogout(currentUser.getEmail(), currentUser.getRole());
            dispose();
            new LoginFrame();
        }
    }

    private void viewLogs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("System Logs");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        JTextArea logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setEditable(false);

        // Load log file content
        try {
            File logFile = new File("logs/app.log");
            if (logFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                logArea.setText(content.toString());
            } else {
                logArea.setText("No logs available yet.");
            }
        } catch (IOException e) {
            logArea.setText("Failed to load logs: " + e.getMessage());
            LoggerUtil.logError("View Logs", "Failed to read log file: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> viewLogs()); // Refresh by calling again
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContent(panel);
    }
}
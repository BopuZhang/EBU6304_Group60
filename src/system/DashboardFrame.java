package system;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.io.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;

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
            addMenuItem(menuPanel, "My Profile", e -> viewProfile());
            addMenuItem(menuPanel, "Create Personal Profile", e -> createProfile());
            addMenuItem(menuPanel, "Edit Personal Profile", e -> editProfile());
            addMenuItem(menuPanel, "Upload CV", e -> uploadCV());
            addMenuItem(menuPanel, "View Available Positions", e -> viewJobs());  // 这里包含申请功能
            addMenuItem(menuPanel, "Check Application Status", e -> viewApplications());
        } else if (currentUser.getRole().equals("MO")) {
            addMenuItem(menuPanel, "Post Position", e -> postJob());
            addMenuItem(menuPanel, "View Positions I've Posted", e -> viewMyJobs());
            addMenuItem(menuPanel, "View Applicants", e -> viewApplicants());
        } else { // Admin
            addMenuItem(menuPanel, "View All Teaching Assistants", e -> viewAllTAs());
            addMenuItem(menuPanel, "View TA Workload", e -> viewWorkload());
            addMenuItem(menuPanel, "Manage Workload", e -> manageWorkload());
            addMenuItem(menuPanel, "View All Positions", e -> viewAllJobs());
            addMenuItem(menuPanel, "View System Logs", e -> viewLogs());
        }

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

    /**
     * Check if TA has already applied for a job
     */
    private boolean hasApplied(String jobId) {
        List<Application> apps = FileUtil.loadApplications();
        for (Application app : apps) {
            if (app.getTaEmail().equals(currentUser.getEmail()) && app.getJobId().equals(jobId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Submit application for a job
     */
    private void submitApplication(String jobId) {
        // Check if already applied
        if (hasApplied(jobId)) {
            JOptionPane.showMessageDialog(this, "You have already applied for this position!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get job details
        List<Job> jobs = FileUtil.loadJobs();
        Job targetJob = null;
        for (Job job : jobs) {
            if (job.getJobId().equals(jobId)) {
                targetJob = job;
                break;
            }
        }

        if (targetJob == null) {
            JOptionPane.showMessageDialog(this, "Position not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if position is still open
        if (!"OPEN".equals(targetJob.getStatus())) {
            JOptionPane.showMessageDialog(this, "This position is no longer accepting applications.", "Closed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check current accepted count
        List<Application> allApps = FileUtil.loadApplications();
        int acceptedCount = 0;
        for (Application app : allApps) {
            if (app.getJobId().equals(jobId) && "ACCEPTED".equals(app.getStatus())) {
                acceptedCount++;
            }
        }

        // Check if position is already full
        if (acceptedCount >= targetJob.getApplicantLimit()) {
            JOptionPane.showMessageDialog(this,
                    "This position has reached its applicant limit (" + targetJob.getApplicantLimit() + ").\n" +
                            "No more applications are being accepted.",
                    "Position Full", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create new application
        String applicationId = "APP" + System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String applyTime = sdf.format(new Date());

        Application newApp = new Application(applicationId, applyTime, currentUser.getEmail(), jobId, "PENDING");

        List<Application> apps = FileUtil.loadApplications();
        apps.add(newApp);
        FileUtil.saveApplications(apps);

        // Check if after this application, the position becomes full
        int newAcceptedCount = acceptedCount + 1;
        if (newAcceptedCount >= targetJob.getApplicantLimit()) {
            // Close the position automatically
            List<Job> updatedJobs = FileUtil.loadJobs();
            for (Job job : updatedJobs) {
                if (job.getJobId().equals(jobId)) {
                    job.setStatus("CLOSED");
                    LoggerUtil.logInfo("Job " + jobId + " automatically closed after reaching applicant limit (" + newAcceptedCount + "/" + targetJob.getApplicantLimit() + ")");
                    break;
                }
            }
            FileUtil.saveJobs(updatedJobs);
            JOptionPane.showMessageDialog(this,
                    "Application submitted successfully!\n\n" +
                            "Note: This position has now reached its applicant limit and has been closed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Application submitted successfully!\n" +
                            "Applicants: " + newAcceptedCount + " / " + targetJob.getApplicantLimit(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }

        LoggerUtil.logInfo("TA " + currentUser.getEmail() + " applied for job: " + jobId);

        // Refresh the job list
        viewJobs();
    }

    // ========== TA Functions ==========

    private void createProfile() {
        // Check if profile already exists
        Profile existingProfile = FileUtil.getProfileByEmail(currentUser.getEmail());
        if (existingProfile != null) {
            JOptionPane.showMessageDialog(this,
                    "You already have a profile. Use 'Edit Personal Profile' to update it.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            viewProfile();
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Create Personal Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Student ID (required, 9 digits)
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Student ID (9 digits):"), gbc);

        JTextField studentIdField = new JTextField(20);
        studentIdField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(studentIdField, gbc);
        row++;

        // Major
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Major:"), gbc);

        JComboBox<String> majorCombo = new JComboBox<>(new String[]{
                "Computer Science", "Software Engineering", "Information Technology",
                "Data Science", "Artificial Intelligence", "Other"
        });
        majorCombo.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(majorCombo, gbc);
        row++;

        // Grade/Year
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Grade/Year:"), gbc);

        JComboBox<String> gradeCombo = new JComboBox<>(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate"
        });
        gradeCombo.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(gradeCombo, gbc);
        row++;

        // Phone
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Phone Number:"), gbc);

        JTextField phoneField = new JTextField(20);
        phoneField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        row++;

        // Description (新增个人描述)
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Personal Description:"), gbc);

        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 60));
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);
        row++;

        panel.add(formPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = UIHelper.createButton("Save Profile", UIHelper.SUCCESS_COLOR);
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);

        saveBtn.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            String major = (String) majorCombo.getSelectedItem();
            String grade = (String) gradeCombo.getSelectedItem();
            String phone = phoneField.getText().trim();
            String description = descArea.getText().trim();

            // Validation
            if (studentId.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate student ID (9 digits)
            if (!studentId.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Student ID must be exactly 9 digits", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create profile
            Profile newProfile = new Profile(currentUser.getEmail(), studentId, major, grade, phone, "", description);

            List<Profile> profiles = FileUtil.loadProfiles();
            profiles.add(newProfile);
            FileUtil.saveProfiles(profiles);

            LoggerUtil.logCreate("Profile", currentUser.getEmail());

            JOptionPane.showMessageDialog(this, "Profile created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Show the created profile
            viewProfile();
        });

        cancelBtn.addActionListener(e -> {
            viewProfile();
        });

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        setContent(panel);
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

        // Basic user info
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("Account Information"));
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.setMaximumSize(new Dimension(500, 120));

        addInfoRow(userInfoPanel, "Name:", currentUser.getName());
        addInfoRow(userInfoPanel, "Email:", currentUser.getEmail());
        addInfoRow(userInfoPanel, "Role:", currentUser.getRole());

        panel.add(userInfoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Profile info
        Profile profile = FileUtil.getProfileByEmail(currentUser.getEmail());

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.setMaximumSize(new Dimension(500, 350));

        if (profile == null) {
            JLabel noProfileLabel = new JLabel("No profile created yet. Click 'Create Personal Profile' to set up your profile.");
            noProfileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            noProfileLabel.setForeground(new Color(150, 150, 150));
            profilePanel.add(noProfileLabel);
        } else {
            addInfoRow(profilePanel, "Student ID:", profile.getStudentId());
            addInfoRow(profilePanel, "Major:", profile.getMajor());
            addInfoRow(profilePanel, "Grade:", profile.getGrade());
            addInfoRow(profilePanel, "Phone:", profile.getPhone());
            if (profile.getDescription() != null && !profile.getDescription().isEmpty()) {
                addInfoRow(profilePanel, "Description:", profile.getDescription());
            }

            // CV section with View button
            if (profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
                addInfoRow(profilePanel, "CV:", profile.getCvPath());

                JPanel cvButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                cvButtonPanel.setBackground(Color.WHITE);
                cvButtonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JButton viewCvBtn = UIHelper.createButton("View CV", UIHelper.PRIMARY_COLOR);
                viewCvBtn.setMaximumSize(new Dimension(120, 30));
                viewCvBtn.addActionListener(e -> viewCvFile(profile.getCvPath()));
                cvButtonPanel.add(viewCvBtn);

                profilePanel.add(cvButtonPanel);
            } else {
                addInfoRow(profilePanel, "CV:", "Not uploaded yet");
            }
        }

        panel.add(profilePanel);

        setContent(panel);
    }

    private void editProfile() {
        // Check if profile exists
        Profile profile = FileUtil.getProfileByEmail(currentUser.getEmail());
        if (profile == null) {
            JOptionPane.showMessageDialog(this,
                    "You don't have a profile yet. Please create one first.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            createProfile();
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Edit Personal Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Student ID (read-only)
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Student ID:"), gbc);

        JLabel studentIdLabel = new JLabel(profile.getStudentId());
        studentIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentIdLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1;
        formPanel.add(studentIdLabel, gbc);
        row++;

        // Major
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Major:"), gbc);

        JComboBox<String> majorCombo = new JComboBox<>(new String[]{
                "Computer Science", "Software Engineering", "Information Technology",
                "Data Science", "Artificial Intelligence", "Other"
        });
        majorCombo.setSelectedItem(profile.getMajor());
        majorCombo.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(majorCombo, gbc);
        row++;

        // Grade/Year
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Grade/Year:"), gbc);

        JComboBox<String> gradeCombo = new JComboBox<>(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate"
        });
        gradeCombo.setSelectedItem(profile.getGrade());
        gradeCombo.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(gradeCombo, gbc);
        row++;

        // Phone
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Phone Number:"), gbc);

        JTextField phoneField = new JTextField(profile.getPhone(), 20);
        phoneField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(phoneField, gbc);
        row++;

        // Description
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Personal Description:"), gbc);

        JTextArea descArea = new JTextArea(3, 20);
        descArea.setText(profile.getDescription() != null ? profile.getDescription() : "");
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 60));
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);
        row++;

        panel.add(formPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = UIHelper.createButton("Save Changes", UIHelper.SUCCESS_COLOR);
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);

        saveBtn.addActionListener(e -> {
            String major = (String) majorCombo.getSelectedItem();
            String grade = (String) gradeCombo.getSelectedItem();
            String phone = phoneField.getText().trim();
            String description = descArea.getText().trim();

            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phone number cannot be empty", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update profile
            Profile updatedProfile = new Profile(
                    profile.getEmail(),
                    profile.getStudentId(),
                    major,
                    grade,
                    phone,
                    profile.getCvPath(),
                    description
            );

            List<Profile> profiles = FileUtil.loadProfiles();
            for (int i = 0; i < profiles.size(); i++) {
                if (profiles.get(i).getEmail().equalsIgnoreCase(currentUser.getEmail())) {
                    profiles.set(i, updatedProfile);
                    break;
                }
            }
            FileUtil.saveProfiles(profiles);

            LoggerUtil.logUpdate("Profile", currentUser.getEmail());

            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh view
            viewProfile();
        });

        cancelBtn.addActionListener(e -> {
            viewProfile();
        });

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        setContent(panel);
    }

    private void uploadCV() {
        // Check if profile exists
        Profile profile = FileUtil.getProfileByEmail(currentUser.getEmail());
        if (profile == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create your personal profile first before uploading CV.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            createProfile();
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Upload CV");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(500, 100));

        JLabel infoLabel = new JLabel("Upload your CV in PDF format (max 5MB)");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(infoLabel);

        if (profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
            JLabel currentLabel = new JLabel("Current CV: " + profile.getCvPath());
            currentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            currentLabel.setForeground(new Color(76, 175, 80));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            infoPanel.add(currentLabel);
        }

        panel.add(infoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // File selection
        JPanel filePanel = new JPanel();
        filePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filePanel.setBackground(Color.WHITE);
        filePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField filePathField = new JTextField(30);
        filePathField.setEditable(false);
        filePathField.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton browseBtn = UIHelper.createButton("Browse", UIHelper.SECONDARY_COLOR);
        browseBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "PDF Files (*.pdf)", "pdf"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                // Check file size (max 5MB = 5 * 1024 * 1024 bytes)
                if (selectedFile.length() > 5 * 1024 * 1024) {
                    JOptionPane.showMessageDialog(this,
                            "File size exceeds 5MB limit. Please select a smaller file.",
                            "File Too Large", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                filePathField.setText(selectedFile.getAbsolutePath());
            }
        });

        filePanel.add(filePathField);
        filePanel.add(browseBtn);
        panel.add(filePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Upload button
        JButton uploadBtn = UIHelper.createButton("Upload CV", UIHelper.SUCCESS_COLOR);
        uploadBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        uploadBtn.setMaximumSize(new Dimension(150, 40));
        uploadBtn.addActionListener(e -> {
            String filePath = filePathField.getText().trim();
            if (filePath.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a file first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create CVs directory if not exists
            File cvDir = new File("cvs");
            if (!cvDir.exists()) {
                cvDir.mkdirs();
            }

            try {
                // Generate unique filename
                String originalFileName = new File(filePath).getName();
                String newFileName = currentUser.getEmail().replace("@", "_").replace(".", "_")
                        + "_" + System.currentTimeMillis() + ".pdf";
                String destPath = "cvs/" + newFileName;

                // Copy file
                File source = new File(filePath);
                File dest = new File(destPath);
                java.nio.file.Files.copy(source.toPath(), dest.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Update profile with CV path
                List<Profile> profiles = FileUtil.loadProfiles();
                for (int i = 0; i < profiles.size(); i++) {
                    if (profiles.get(i).getEmail().equals(currentUser.getEmail())) {
                        Profile updatedProfile = new Profile(
                                profiles.get(i).getEmail(),
                                profiles.get(i).getStudentId(),
                                profiles.get(i).getMajor(),
                                profiles.get(i).getGrade(),
                                profiles.get(i).getPhone(),
                                destPath,
                                profiles.get(i).getDescription()
                        );
                        profiles.set(i, updatedProfile);
                        break;
                    }
                }
                FileUtil.saveProfiles(profiles);

                LoggerUtil.logUpdate("CV Upload", currentUser.getEmail() + " uploaded CV: " + destPath);
                JOptionPane.showMessageDialog(this, "CV uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Refresh view
                viewProfile();

            } catch (Exception ex) {
                LoggerUtil.logError("CV Upload", ex.getMessage());
                JOptionPane.showMessageDialog(this, "Failed to upload CV: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(uploadBtn);

        setContent(panel);
    }

    /**
     * View Available Positions - 带申请按钮的职位列表
     */
    private void viewJobs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Available Positions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Job> allJobs = FileUtil.loadJobs();
        List<Job> availableJobs = new ArrayList<>();
        List<Application> allApps = FileUtil.loadApplications();

        for (Job job : allJobs) {
            if ("OPEN".equals(job.getStatus())) {
                availableJobs.add(job);
            }
        }

        if (availableJobs.isEmpty()) {
            JLabel emptyLabel = new JLabel("No available positions at this time.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel, BorderLayout.CENTER);
            setContent(panel);
            return;
        }

        availableJobs.sort((j1, j2) -> j1.getDeadline().compareTo(j2.getDeadline()));

        // 创建一个包含表头和表格内容的容器
        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        // Header - 设置固定高度
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        headerPanel.setPreferredSize(new Dimension(0, 45));
        headerPanel.add(createHeaderLabel("Module Code"));
        headerPanel.add(createHeaderLabel("Module Name"));
        headerPanel.add(createHeaderLabel("Weekly Hours"));
        headerPanel.add(createHeaderLabel("Deadline"));
        headerPanel.add(createHeaderLabel("Applicants"));
        headerPanel.add(createHeaderLabel("Status"));
        headerPanel.add(createHeaderLabel("Action"));
        tableContainer.add(headerPanel);

        for (Job job : availableJobs) {
            int currentApplicants = (int) allApps.stream()
                    .filter(app -> app.getJobId().equals(job.getJobId()) && "ACCEPTED".equals(app.getStatus()))
                    .count();

            boolean applied = hasApplied(job.getJobId());
            boolean reachedLimit = currentApplicants >= job.getApplicantLimit();

            JPanel rowPanel = new JPanel(new GridLayout(1, 7, 5, 0));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            rowPanel.setPreferredSize(new Dimension(0, 50));

            rowPanel.add(createCellLabel(job.getModuleCode()));
            rowPanel.add(createCellLabel(job.getModuleName()));
            rowPanel.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
            rowPanel.add(createCellLabel(job.getDeadline()));

            JLabel applicantsLabel = createCellLabel(currentApplicants + " / " + job.getApplicantLimit());
            applicantsLabel.setForeground(reachedLimit ? new Color(244, 67, 54) : new Color(76, 175, 80));
            rowPanel.add(applicantsLabel);

            JLabel statusLabel;
            if (reachedLimit) {
                statusLabel = createCellLabel("Full");
                statusLabel.setForeground(new Color(244, 67, 54));
            } else if (applied) {
                statusLabel = createCellLabel("Applied");
                statusLabel.setForeground(new Color(76, 175, 80));
            } else {
                statusLabel = createCellLabel("Available");
                statusLabel.setForeground(new Color(79, 114, 139));
            }
            rowPanel.add(statusLabel);

            JButton applyBtn = new JButton("Apply");
            applyBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            applyBtn.setBackground(new Color(76, 175, 80));
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFocusPainted(false);
            applyBtn.setBorderPainted(false);
            applyBtn.setOpaque(true);
            applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (applied || reachedLimit) {
                applyBtn.setEnabled(false);
                applyBtn.setText(applied ? "Applied" : "Full");
                applyBtn.setBackground(new Color(150, 150, 150));
            } else {
                final Job finalJob = job;
                final int finalCurrentApplicants = currentApplicants;
                applyBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Apply for " + finalJob.getModuleCode() + " - " + finalJob.getModuleName() + "?\n" +
                                    "Current applicants: " + finalCurrentApplicants + " / " + finalJob.getApplicantLimit(),
                            "Confirm Application",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        submitApplication(finalJob.getJobId());
                    }
                });
            }

            // 将按钮放在一个面板中以保持居中
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnPanel.setBackground(Color.WHITE);
            btnPanel.add(applyBtn);
            rowPanel.add(btnPanel);

            tableContainer.add(rowPanel);
        }

        // 关键：将 tableContainer 放入 JScrollPane
        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        // 设置滚动速度
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        setContent(panel);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(79, 114, 139));
        return label;
    }

    private JLabel createCellLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(Color.BLACK);
        return label;
    }

    private void viewApplications() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Application Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Load applications
        List<Application> allApps = FileUtil.loadApplications();
        List<Application> myApps = new ArrayList<>();

        for (Application app : allApps) {
            if (app.getTaEmail().equals(currentUser.getEmail())) {
                myApps.add(app);
            }
        }

        if (myApps.isEmpty()) {
            JLabel emptyLabel = new JLabel("You haven't applied for any positions yet. Go to 'View Available Positions' to apply.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel, BorderLayout.CENTER);
            setContent(panel);
            return;
        }

        // Sort by apply time (newest first)
        myApps.sort((a1, a2) -> a2.getApplyTime().compareTo(a1.getApplyTime()));

        // Get jobs for job details
        List<Job> allJobs = FileUtil.loadJobs();

        // Create table container
        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        // Header - 固定高度
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        headerPanel.setPreferredSize(new Dimension(0, 45));
        headerPanel.add(createHeaderLabel("Apply Date"));
        headerPanel.add(createHeaderLabel("Position"));
        headerPanel.add(createHeaderLabel("Module Code"));
        headerPanel.add(createHeaderLabel("Weekly Hours"));
        headerPanel.add(createHeaderLabel("Status"));
        tableContainer.add(headerPanel);

        for (Application app : myApps) {
            // Find job details
            Job job = null;
            for (Job j : allJobs) {
                if (j.getJobId().equals(app.getJobId())) {
                    job = j;
                    break;
                }
            }

            JPanel rowPanel = new JPanel(new GridLayout(1, 5, 5, 0));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
            rowPanel.setPreferredSize(new Dimension(0, 50));

            rowPanel.add(createCellLabel(app.getApplyTime()));

            if (job != null) {
                rowPanel.add(createCellLabel(job.getModuleName()));
                rowPanel.add(createCellLabel(job.getModuleCode()));
                rowPanel.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
            } else {
                rowPanel.add(createCellLabel("Position not found"));
                rowPanel.add(createCellLabel("-"));
                rowPanel.add(createCellLabel("-"));
            }

            // Status with color
            String statusText;
            Color statusColor;
            switch (app.getStatus().toUpperCase()) {
                case "ACCEPTED":
                    statusText = "Accepted";
                    statusColor = new Color(76, 175, 80);
                    break;
                case "REJECTED":
                    statusText = "Rejected";
                    statusColor = new Color(244, 67, 54);
                    break;
                default:
                    statusText = "Pending";
                    statusColor = new Color(255, 152, 0);
                    break;
            }

            JLabel statusLabel = createCellLabel(statusText);
            statusLabel.setForeground(statusColor);
            rowPanel.add(statusLabel);

            tableContainer.add(rowPanel);
        }

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);
        setContent(panel);
    }
//========== MO Functions ==========
    /**
     * View applicants for MO's positions (with "All Positions" option)
     */
    private void viewApplicants() {
        // Load jobs posted by this MO
        List<Job> allJobs = FileUtil.loadJobs();
        List<Job> myJobs = new ArrayList<>();

        for (Job job : allJobs) {
            if (job.getMoEmail().equals(currentUser.getEmail())) {
                myJobs.add(job);
            }
        }

        if (myJobs.isEmpty()) {
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel emptyLabel = new JLabel("You haven't posted any positions yet. Go to 'Post Position' to create one.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel, BorderLayout.CENTER);
            setContent(panel);
            return;
        }

        // Create job selection panel
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with job selector
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);

        topPanel.add(new JLabel("Select Position:"));

        JComboBox<Object> jobCombo = new JComboBox<>();
        // Add "All Positions" option first
        jobCombo.addItem("All Positions");
        for (Job job : myJobs) {
            jobCombo.addItem(job);
        }

        jobCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Job) {
                    Job job = (Job) value;
                    value = job.getModuleCode() + " - " + job.getModuleName() + " (" + job.getStatus() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        jobCombo.setPreferredSize(new Dimension(350, 30));
        topPanel.add(jobCombo);

        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);

        final JPanel finalPanel = panel;
        final JComboBox<Object> finalJobCombo = jobCombo;

        refreshBtn.addActionListener(e -> {
            refreshApplicantsView(finalPanel, finalJobCombo);
        });
        topPanel.add(refreshBtn);

        panel.add(topPanel, BorderLayout.NORTH);

        // Content panel for applicants
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        panel.add(contentPanel, BorderLayout.CENTER);

        setContent(panel);

        // Load initial applicants
        refreshApplicantsView(panel, jobCombo);
    }
    private void postJob() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Post New Position");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form fields - 使用更大的尺寸
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // 增加间距
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Module Code - 增大宽度
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel moduleCodeLabel = new JLabel("Module Code:");
        moduleCodeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        moduleCodeLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(moduleCodeLabel, gbc);

        JTextField moduleCodeField = new JTextField(25);
        moduleCodeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleCodeField.setPreferredSize(new Dimension(350, 35));
        gbc.gridx = 1;
        formPanel.add(moduleCodeField, gbc);
        row++;

        // Module Name
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel moduleNameLabel = new JLabel("Module Name:");
        moduleNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        moduleNameLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(moduleNameLabel, gbc);

        JTextField moduleNameField = new JTextField(25);
        moduleNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleNameField.setPreferredSize(new Dimension(350, 35));
        gbc.gridx = 1;
        formPanel.add(moduleNameField, gbc);
        row++;

        // Description - 增大文本区域
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(descLabel, gbc);

        JTextArea descArea = new JTextArea(4, 30);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(350, 80));
        descScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);
        row++;

        // Weekly Hours
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel hoursLabel = new JLabel("Weekly Hours (1-20):");
        hoursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        hoursLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(hoursLabel, gbc);

        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        hoursSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        hoursSpinner.setPreferredSize(new Dimension(100, 35));
        gbc.gridx = 1;
        formPanel.add(hoursSpinner, gbc);
        row++;

        // Applicant Limit
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel limitLabel = new JLabel("Applicant Limit:");
        limitLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        limitLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(limitLabel, gbc);

        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        limitPanel.setBackground(Color.WHITE);

        JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        limitSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        limitSpinner.setPreferredSize(new Dimension(100, 35));
        limitPanel.add(limitSpinner);

        JLabel limitHint = new JLabel("(Number of TAs to hire)");
        limitHint.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        limitHint.setForeground(new Color(150, 150, 150));
        limitPanel.add(limitHint);

        gbc.gridx = 1;
        formPanel.add(limitPanel, gbc);
        row++;

        // Deadline
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel deadlineLabel = new JLabel("Deadline (YYYY-MM-DD):");
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        deadlineLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(deadlineLabel, gbc);

        JTextField deadlineField = new JTextField(15);
        deadlineField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        deadlineField.setPreferredSize(new Dimension(200, 35));
        deadlineField.setToolTipText("Format: 2024-12-31");
        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);
        row++;

        // Requirements - 增大文本区域
        gbc.gridy = row;
        gbc.gridx = 0;
        JLabel reqLabel = new JLabel("Requirements:");
        reqLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reqLabel.setPreferredSize(new Dimension(150, 30));
        formPanel.add(reqLabel, gbc);

        JTextArea reqArea = new JTextArea(4, 30);
        reqArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reqArea.setLineWrap(true);
        reqArea.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(reqArea);
        reqScroll.setPreferredSize(new Dimension(350, 80));
        reqScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        gbc.gridx = 1;
        formPanel.add(reqScroll, gbc);
        row++;

        panel.add(formPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton submitBtn = UIHelper.createButton("Post Position", UIHelper.SUCCESS_COLOR);
        submitBtn.setPreferredSize(new Dimension(150, 40));
        submitBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        submitBtn.addActionListener(e -> {
            // Validation
            String moduleCode = moduleCodeField.getText().trim();
            String moduleName = moduleNameField.getText().trim();
            String description = descArea.getText().trim();
            int hours = (int) hoursSpinner.getValue();
            int limit = (int) limitSpinner.getValue();
            String deadline = deadlineField.getText().trim();
            String requirements = reqArea.getText().trim();

            if (moduleCode.isEmpty() || moduleName.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate deadline format
            if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Deadline must be in format YYYY-MM-DD", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Generate Job ID
            String jobId = "JOB" + System.currentTimeMillis();

            // Create job
            Job newJob = new Job(jobId, currentUser.getEmail(), moduleCode, moduleName,
                    description, hours, deadline, requirements, "OPEN", limit);

            // Save to file
            List<Job> jobs = FileUtil.loadJobs();
            jobs.add(newJob);
            FileUtil.saveJobs(jobs);

            // Log
            LoggerUtil.logCreate("Job", jobId + " - " + moduleCode + " (Limit: " + limit + ")");

            JOptionPane.showMessageDialog(this,
                    "Position posted successfully!\n\n" +
                            "Job ID: " + jobId + "\n" +
                            "Module: " + moduleCode + " - " + moduleName + "\n" +
                            "Applicant Limit: " + limit,
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear form
            moduleCodeField.setText("");
            moduleNameField.setText("");
            descArea.setText("");
            hoursSpinner.setValue(5);
            limitSpinner.setValue(5);
            deadlineField.setText("");
            reqArea.setText("");

            // Refresh view
            viewMyJobs();
        });

        cancelBtn.addActionListener(e -> viewMyJobs());

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        setContent(panel);
    }
    /**
     * View jobs posted by the current MO (with edit, delete, toggle status, and applicant count)
     */
    private void viewMyJobs() {
        List<Job> allJobs = FileUtil.loadJobs();
        List<Job> myJobs = new ArrayList<>();

        for (Job job : allJobs) {
            if (job.getMoEmail().equals(currentUser.getEmail())) {
                myJobs.add(job);
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and buttons
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("My Posted Positions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        JButton newBtn = UIHelper.createButton("+ New Position", UIHelper.SUCCESS_COLOR);
        newBtn.addActionListener(e -> postJob());
        topPanel.add(newBtn, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        if (myJobs.isEmpty()) {
            JLabel emptyLabel = new JLabel("You haven't posted any positions yet. Click 'New Position' to create one.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel, BorderLayout.CENTER);
            setContent(panel);
            return;
        }

        // Sort by deadline
        myJobs.sort((j1, j2) -> j1.getDeadline().compareTo(j2.getDeadline()));

        // Load all applications once for efficiency
        List<Application> allApps = FileUtil.loadApplications();

        // 使用 JTable 确保对齐
        String[] columnNames = {"Job ID", "Module Code", "Module Name", "Weekly Hours", "Deadline", "Applicants", "Status", "Action"};

        Object[][] data = new Object[myJobs.size()][8];

        for (int i = 0; i < myJobs.size(); i++) {
            Job job = myJobs.get(i);
            int acceptedCount = (int) allApps.stream()
                    .filter(app -> app.getJobId().equals(job.getJobId()) && "ACCEPTED".equals(app.getStatus()))
                    .count();

            data[i][0] = job.getJobId();
            data[i][1] = job.getModuleCode();
            data[i][2] = job.getModuleName();
            data[i][3] = String.valueOf(job.getWeeklyHours());
            data[i][4] = job.getDeadline();

            // Applicants with color indicator
            String applicantsText = acceptedCount + " / " + job.getApplicantLimit();
            String applicantsColor = acceptedCount >= job.getApplicantLimit() ? "#F44336" : "#4CAF50";
            data[i][5] = "<html><font color='" + applicantsColor + "'>" + applicantsText + "</font></html>";

            // Status with color
            String statusText = "OPEN".equals(job.getStatus()) ? "Open ✓" : "Closed";
            String statusColor = "OPEN".equals(job.getStatus()) ? "#4CAF50" : "#969696";
            data[i][6] = "<html><font color='" + statusColor + "'>" + statusText + "</font></html>";

            // Store job object for action buttons
            data[i][7] = job;
        }

        // 创建表格模型
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // 只有Action列可编辑（用于按钮）
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 7) {
                    return Job.class;
                }
                return String.class;
            }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(65); // 增加行高确保按钮完整显示
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBorder(BorderFactory.createEmptyBorder());

        // 关键：关闭自动调整列宽，这样才能出现水平滚动条
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 设置表头样式 - 居中显示
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(new Color(79, 114, 139));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 设置表头渲染器，使所有列名居中
        javax.swing.table.DefaultTableCellRenderer headerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // 设置列宽 - 确保所有列都有足够宽度
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(0).setMinWidth(90);
        table.getColumnModel().getColumn(0).setMaxWidth(120);

        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setMinWidth(100);
        table.getColumnModel().getColumn(1).setMaxWidth(150);

        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setMinWidth(150);
        table.getColumnModel().getColumn(2).setMaxWidth(300);

        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setMinWidth(90);
        table.getColumnModel().getColumn(3).setMaxWidth(120);

        table.getColumnModel().getColumn(4).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setMinWidth(100);
        table.getColumnModel().getColumn(4).setMaxWidth(150);

        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setMinWidth(90);
        table.getColumnModel().getColumn(5).setMaxWidth(120);

        table.getColumnModel().getColumn(6).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setMinWidth(70);
        table.getColumnModel().getColumn(6).setMaxWidth(100);

        table.getColumnModel().getColumn(7).setPreferredWidth(200);
        table.getColumnModel().getColumn(7).setMinWidth(180);
        table.getColumnModel().getColumn(7).setMaxWidth(250);

        // 设置单元格渲染器支持HTML并居中
        table.setDefaultRenderer(String.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus, int row, int column) {
                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                return c;
            }
        });

        // 为Action列设置按钮渲染器和编辑器
        table.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), this, allApps));

        // 创建外层面板用于滚动
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(Color.WHITE);

        // 添加滚动条 - 同时支持水平和垂直滚动
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableWrapper, BorderLayout.CENTER);
        setContent(panel);
    }

    private void refreshApplicantsView(JPanel parentPanel, JComboBox<Object> jobCombo) {
        Object selectedItem = jobCombo.getSelectedItem();

        // Get all applications
        List<Application> allApps = FileUtil.loadApplications();
        List<Job> allJobs = FileUtil.loadJobs();

        // Get jobs posted by this MO
        List<Job> myJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (job.getMoEmail().equals(currentUser.getEmail())) {
                myJobs.add(job);
            }
        }

        // Determine which applications to show
        List<Application> jobApps = new ArrayList<>();
        Job selectedJob = null;

        if (selectedItem instanceof String && selectedItem.equals("All Positions")) {
            // Show all applications from all my jobs
            for (Job job : myJobs) {
                for (Application app : allApps) {
                    if (app.getJobId().equals(job.getJobId())) {
                        jobApps.add(app);
                    }
                }
            }
        } else if (selectedItem instanceof Job) {
            selectedJob = (Job) selectedItem;
            for (Application app : allApps) {
                if (app.getJobId().equals(selectedJob.getJobId())) {
                    jobApps.add(app);
                }
            }
        } else {
            return;
        }

        // Get profiles for TAs
        List<Profile> allProfiles = FileUtil.loadProfiles();
        List<User> allUsers = FileUtil.loadUsers();

        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        // Job info panel
        JPanel jobInfoPanel = new JPanel();
        jobInfoPanel.setLayout(new BoxLayout(jobInfoPanel, BoxLayout.Y_AXIS));
        jobInfoPanel.setBackground(new Color(240, 240, 240));
        jobInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        if (selectedItem instanceof String && selectedItem.equals("All Positions")) {
            int totalApplicants = 0;
            int totalAccepted = 0;
            for (Job job : myJobs) {
                for (Application app : allApps) {
                    if (app.getJobId().equals(job.getJobId())) {
                        totalApplicants++;
                        if ("ACCEPTED".equals(app.getStatus())) {
                            totalAccepted++;
                        }
                    }
                }
            }
            jobInfoPanel.add(new JLabel("All Positions - Summary"));
            jobInfoPanel.add(new JLabel("Total Positions: " + myJobs.size()));
            jobInfoPanel.add(new JLabel("Total Applicants: " + totalApplicants));
            jobInfoPanel.add(new JLabel("Total Accepted: " + totalAccepted));
        } else if (selectedJob != null) {
            jobInfoPanel.add(new JLabel("Position: " + selectedJob.getModuleCode() + " - " + selectedJob.getModuleName()));
            jobInfoPanel.add(new JLabel("Deadline: " + selectedJob.getDeadline()));
            jobInfoPanel.add(new JLabel("Weekly Hours: " + selectedJob.getWeeklyHours()));
            jobInfoPanel.add(new JLabel("Applicants: " + jobApps.size()));
            jobInfoPanel.add(new JLabel("Applicant Limit: " + selectedJob.getApplicantLimit()));
        }

        contentPanel.add(jobInfoPanel, BorderLayout.NORTH);

        if (jobApps.isEmpty()) {
            JLabel emptyLabel = new JLabel("No applicants for this selection.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            contentPanel.add(emptyLabel, BorderLayout.CENTER);
        } else {
            // 创建一个外层面板，用于放置表格和滚动条
            JPanel tableWrapper = new JPanel();
            tableWrapper.setLayout(new BorderLayout());
            tableWrapper.setBackground(Color.WHITE);

            // 确定列数
            int columnCount = (selectedItem instanceof String && selectedItem.equals("All Positions")) ? 8 : 7;
            boolean isAllPositions = (selectedItem instanceof String && selectedItem.equals("All Positions"));

            // 计算总宽度 - 设置每列的最小宽度
            int[] columnWidths;
            if (isAllPositions) {
                columnWidths = new int[]{150, 120, 100, 100, 180, 80, 110, 100};
            } else {
                columnWidths = new int[]{150, 120, 100, 100, 80, 110, 100};
            }

            // 创建表格容器 - 使用 BoxLayout
            JPanel tableContainer = new JPanel();
            tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
            tableContainer.setBackground(Color.WHITE);

            // 设置表格容器的首选大小，使其足够宽以显示所有列
            int totalWidth = 0;
            for (int width : columnWidths) {
                totalWidth += width;
            }
            totalWidth += (columnCount - 1) * 5; // 加上间距
            tableContainer.setPreferredSize(new Dimension(totalWidth, 0));

            // Header - 使用 GridBagLayout 以更好地控制列宽
            JPanel headerPanel = new JPanel(new GridBagLayout());
            headerPanel.setBackground(new Color(240, 240, 240));
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            headerPanel.setPreferredSize(new Dimension(totalWidth, 45));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 2, 0, 2);
            gbc.gridy = 0;

            int col = 0;

            // Apply Date
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel dateHeader = createHeaderLabel("Apply Date");
            dateHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(dateHeader, gbc);
            col++;

            // Name
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel nameHeader = createHeaderLabel("Name");
            nameHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(nameHeader, gbc);
            col++;

            // Student ID
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel idHeader = createHeaderLabel("Student ID");
            idHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(idHeader, gbc);
            col++;

            // Major
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel majorHeader = createHeaderLabel("Major");
            majorHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(majorHeader, gbc);
            col++;

            if (isAllPositions) {
                // Position
                gbc.gridx = col;
                gbc.weightx = 1.0;
                JLabel positionHeader = createHeaderLabel("Position");
                positionHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
                headerPanel.add(positionHeader, gbc);
                col++;
            }

            // Status
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel statusHeader = createHeaderLabel("Status");
            statusHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(statusHeader, gbc);
            col++;

            // Action
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel actionHeader = createHeaderLabel("Action");
            actionHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(actionHeader, gbc);
            col++;

            // Profile
            gbc.gridx = col;
            gbc.weightx = 1.0;
            JLabel profileHeader = createHeaderLabel("Profile");
            profileHeader.setPreferredSize(new Dimension(columnWidths[col], 30));
            headerPanel.add(profileHeader, gbc);

            tableContainer.add(headerPanel);

            final JPanel finalParentPanel = parentPanel;
            final JComboBox<Object> finalJobCombo = jobCombo;

            for (Application app : jobApps) {
                // Find the job for this application
                Job appJob = null;
                for (Job job : allJobs) {
                    if (job.getJobId().equals(app.getJobId())) {
                        appJob = job;
                        break;
                    }
                }

                // Find TA profile
                Profile taProfile = null;
                for (Profile p : allProfiles) {
                    if (p.getEmail().equalsIgnoreCase(app.getTaEmail())) {
                        taProfile = p;
                        break;
                    }
                }

                User taUser = null;
                for (User u : allUsers) {
                    if (u.getEmail().equalsIgnoreCase(app.getTaEmail())) {
                        taUser = u;
                        break;
                    }
                }

                // 创建行面板 - 使用 GridBagLayout
                JPanel rowPanel = new JPanel(new GridBagLayout());
                rowPanel.setBackground(Color.WHITE);
                rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                rowPanel.setPreferredSize(new Dimension(totalWidth, 60));

                GridBagConstraints rowGbc = new GridBagConstraints();
                rowGbc.fill = GridBagConstraints.HORIZONTAL;
                rowGbc.insets = new Insets(0, 2, 0, 2);
                rowGbc.gridy = 0;

                col = 0;

                // Apply Date
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                JLabel dateLabel = createCellLabel(app.getApplyTime());
                dateLabel.setPreferredSize(new Dimension(columnWidths[col], 45));
                rowPanel.add(dateLabel, rowGbc);
                col++;

                // Name
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                String taName = taUser != null ? taUser.getName() : app.getTaEmail();
                JLabel nameLabel = createCellLabel(taName);
                nameLabel.setPreferredSize(new Dimension(columnWidths[col], 45));
                rowPanel.add(nameLabel, rowGbc);
                col++;

                // Student ID
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                String studentId = taProfile != null ? taProfile.getStudentId() : "Not set";
                JLabel idLabel = createCellLabel(studentId);
                idLabel.setPreferredSize(new Dimension(columnWidths[col], 45));
                rowPanel.add(idLabel, rowGbc);
                col++;

                // Major
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                String major = taProfile != null ? taProfile.getMajor() : "Not set";
                JLabel majorLabel = createCellLabel(major);
                majorLabel.setPreferredSize(new Dimension(columnWidths[col], 45));
                rowPanel.add(majorLabel, rowGbc);
                col++;

                if (isAllPositions && appJob != null) {
                    // Position
                    rowGbc.gridx = col;
                    rowGbc.weightx = 1.0;
                    String positionText = appJob.getModuleCode() + " - " + appJob.getModuleName();
                    JLabel positionLabel = createCellLabel(positionText);
                    positionLabel.setPreferredSize(new Dimension(columnWidths[col], 45));
                    rowPanel.add(positionLabel, rowGbc);
                    col++;
                }

                // Status
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                JLabel statusLabel;
                switch (app.getStatus().toUpperCase()) {
                    case "ACCEPTED":
                        statusLabel = createCellLabel("Accepted");
                        statusLabel.setForeground(new Color(76, 175, 80));
                        break;
                    case "REJECTED":
                        statusLabel = createCellLabel("Rejected");
                        statusLabel.setForeground(new Color(244, 67, 54));
                        break;
                    default:
                        statusLabel = createCellLabel("Pending");
                        statusLabel.setForeground(new Color(255, 152, 0));
                        break;
                }
                statusLabel.setPreferredSize(new Dimension(columnWidths[col], 45));
                rowPanel.add(statusLabel, rowGbc);
                col++;

                // Action buttons
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                actionPanel.setBackground(Color.WHITE);
                actionPanel.setPreferredSize(new Dimension(columnWidths[col], 45));

                JButton acceptBtn = new JButton("Accept");
                acceptBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                acceptBtn.setBackground(new Color(76, 175, 80));
                acceptBtn.setForeground(Color.WHITE);
                acceptBtn.setFocusPainted(false);
                acceptBtn.setBorderPainted(false);
                acceptBtn.setOpaque(true);
                acceptBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JButton rejectBtn = new JButton("Reject");
                rejectBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                rejectBtn.setBackground(new Color(244, 67, 54));
                rejectBtn.setForeground(Color.WHITE);
                rejectBtn.setFocusPainted(false);
                rejectBtn.setBorderPainted(false);
                rejectBtn.setOpaque(true);
                rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                final Application finalApp = app;
                final Profile finalTaProfile = taProfile;
                final User finalTaUser = taUser;
                final Job finalAppJob = appJob;

                if (!app.getStatus().equals("PENDING")) {
                    acceptBtn.setEnabled(false);
                    acceptBtn.setBackground(new Color(150, 150, 150));
                    rejectBtn.setEnabled(false);
                    rejectBtn.setBackground(new Color(150, 150, 150));
                } else {
                    acceptBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Accept " + (finalTaUser != null ? finalTaUser.getName() : finalApp.getTaEmail()) +
                                        " for " + (finalAppJob != null ? finalAppJob.getModuleCode() : "position") + "?",
                                "Confirm Acceptance",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            updateApplicationStatus(finalApp.getApplicationId(), "ACCEPTED", finalAppJob);
                            refreshApplicantsView(finalParentPanel, finalJobCombo);
                        }
                    });

                    rejectBtn.addActionListener(e -> {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Reject " + (finalTaUser != null ? finalTaUser.getName() : finalApp.getTaEmail()) +
                                        " for " + (finalAppJob != null ? finalAppJob.getModuleCode() : "position") + "?",
                                "Confirm Rejection",
                                JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            updateApplicationStatus(finalApp.getApplicationId(), "REJECTED", finalAppJob);
                            refreshApplicantsView(finalParentPanel, finalJobCombo);
                        }
                    });
                }

                actionPanel.add(acceptBtn);
                actionPanel.add(rejectBtn);
                rowPanel.add(actionPanel, rowGbc);
                col++;

                // View Profile button
                rowGbc.gridx = col;
                rowGbc.weightx = 1.0;
                JPanel profileBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
                profileBtnPanel.setBackground(Color.WHITE);
                profileBtnPanel.setPreferredSize(new Dimension(columnWidths[col], 45));

                JButton viewProfileBtn = new JButton("View Profile");
                viewProfileBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                viewProfileBtn.setBackground(new Color(79, 114, 139));
                viewProfileBtn.setForeground(Color.WHITE);
                viewProfileBtn.setFocusPainted(false);
                viewProfileBtn.setBorderPainted(false);
                viewProfileBtn.setOpaque(true);
                viewProfileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                viewProfileBtn.addActionListener(e -> {
                    if (finalTaProfile != null) {
                        viewTaProfile(finalTaProfile, finalTaUser);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No profile data available for this TA.\n\n" +
                                        "The TA may not have created their profile yet.",
                                "Profile Not Found",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                });

                profileBtnPanel.add(viewProfileBtn);
                rowPanel.add(profileBtnPanel, rowGbc);

                tableContainer.add(rowPanel);
            }

            // 将表格容器放入 JScrollPane，支持水平和垂直滚动
            JScrollPane scrollPane = new JScrollPane(tableContainer);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            scrollPane.getHorizontalScrollBar().setUnitIncrement(20);

            tableWrapper.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(tableWrapper, BorderLayout.CENTER);
        }

        // Update the parent panel
        Component[] components = parentPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof BorderLayout) {
                JPanel oldContent = (JPanel) comp;
                oldContent.removeAll();
                oldContent.add(contentPanel, BorderLayout.CENTER);
                oldContent.revalidate();
                oldContent.repaint();
                break;
            }
        }
    }

    private void updateApplicationStatus(String applicationId, String newStatus, Job job) {
        List<Application> apps = FileUtil.loadApplications();
        for (Application app : apps) {
            if (app.getApplicationId().equals(applicationId)) {
                app.setStatus(newStatus);
                break;
            }
        }
        FileUtil.saveApplications(apps);

        // If accepting an applicant, check if the position has reached its limit
        if (newStatus.equals("ACCEPTED")) {
            // Count how many accepted applicants for this job
            int acceptedCount = 0;
            for (Application app : apps) {
                if (app.getJobId().equals(job.getJobId()) && "ACCEPTED".equals(app.getStatus())) {
                    acceptedCount++;
                }
            }

            // Only close the job if the limit has been reached
            if (acceptedCount >= job.getApplicantLimit()) {
                List<Job> jobs = FileUtil.loadJobs();
                for (Job j : jobs) {
                    if (j.getJobId().equals(job.getJobId())) {
                        j.setStatus("CLOSED");
                        LoggerUtil.logInfo("Job " + job.getJobId() + " closed after reaching applicant limit (" + acceptedCount + "/" + job.getApplicantLimit() + ")");
                        break;
                    }
                }
                FileUtil.saveJobs(jobs);
                JOptionPane.showMessageDialog(this,
                        "Position has now reached its applicant limit and has been closed.",
                        "Position Full", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        LoggerUtil.logUpdate("Application", applicationId + " -> " + newStatus);
        JOptionPane.showMessageDialog(this, "Application status updated to " + newStatus + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Edit an existing posted position
     */
    private void editPostedPosition(Job job) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Edit Position: " + job.getModuleCode() + " - " + job.getModuleName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Job ID (read-only)
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Job ID:"), gbc);

        JLabel jobIdLabel = new JLabel(job.getJobId());
        jobIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        jobIdLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1;
        formPanel.add(jobIdLabel, gbc);
        row++;

        // Module Code (read-only)
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Module Code:"), gbc);

        JLabel moduleCodeLabel = new JLabel(job.getModuleCode());
        moduleCodeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleCodeLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1;
        formPanel.add(moduleCodeLabel, gbc);
        row++;

        // Module Name (read-only)
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Module Name:"), gbc);

        JLabel moduleNameLabel = new JLabel(job.getModuleName());
        moduleNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        moduleNameLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1;
        formPanel.add(moduleNameLabel, gbc);
        row++;

        // Description
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Description:"), gbc);

        JTextArea descArea = new JTextArea(job.getDescription(), 3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 60));
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);
        row++;

        // Weekly Hours - 添加安全检查
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Weekly Hours (1-20):"), gbc);

        int weeklyHours = job.getWeeklyHours();
        if (weeklyHours < 1) weeklyHours = 5;
        if (weeklyHours > 20) weeklyHours = 20;
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(weeklyHours, 1, 20, 1));
        hoursSpinner.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(hoursSpinner, gbc);
        row++;

        // Applicant Limit - 添加安全检查
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Applicant Limit (1-50):"), gbc);

        int applicantLimit = job.getApplicantLimit();
        if (applicantLimit < 1) applicantLimit = 5;
        if (applicantLimit > 50) applicantLimit = 50;
        JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(applicantLimit, 1, 50, 1));
        limitSpinner.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(limitSpinner, gbc);

        JLabel limitHint = new JLabel("(Number of TAs to hire)");
        limitHint.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        limitHint.setForeground(new Color(150, 150, 150));
        gbc.gridx = 2;
        formPanel.add(limitHint, gbc);
        row++;

        // Deadline
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);

        JTextField deadlineField = new JTextField(job.getDeadline(), 20);
        deadlineField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);
        row++;

        // Requirements
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Requirements:"), gbc);

        JTextArea reqArea = new JTextArea(job.getRequirements(), 3, 20);
        reqArea.setLineWrap(true);
        reqArea.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(reqArea);
        reqScroll.setPreferredSize(new Dimension(250, 60));
        gbc.gridx = 1;
        formPanel.add(reqScroll, gbc);
        row++;

        panel.add(formPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Warning for jobs with applicants - 计算 acceptedCount
        List<Application> allApps = FileUtil.loadApplications();
        boolean hasApplicants = false;
        int acceptedCount = 0;
        for (Application app : allApps) {
            if (app.getJobId().equals(job.getJobId())) {
                hasApplicants = true;
                if ("ACCEPTED".equals(app.getStatus())) {
                    acceptedCount++;
                }
            }
        }

        // 创建 final 变量用于 lambda 表达式
        final int finalAcceptedCount = acceptedCount;
        final Job finalJob = job;

        if (hasApplicants) {
            JPanel warningPanel = new JPanel();
            warningPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            warningPanel.setBackground(new Color(255, 243, 224));
            warningPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 152, 0)));
            warningPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            warningPanel.setMaximumSize(new Dimension(500, 50));

            JLabel warningLabel = new JLabel("Warning: This position already has applicants. Modifying it may affect existing applications.");
            warningLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            warningLabel.setForeground(new Color(255, 152, 0));
            warningPanel.add(warningLabel);

            panel.add(warningPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton saveBtn = UIHelper.createButton("Save Changes", UIHelper.SUCCESS_COLOR);
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);

        saveBtn.addActionListener(e -> {
            String description = descArea.getText().trim();
            int hours = (int) hoursSpinner.getValue();
            int limit = (int) limitSpinner.getValue();
            String deadline = deadlineField.getText().trim();
            String requirements = reqArea.getText().trim();

            if (description.isEmpty() || deadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate deadline format
            if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Deadline must be in format YYYY-MM-DD", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if reducing limit below current accepted count
            if (limit < finalAcceptedCount) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Warning: You are reducing the applicant limit to " + limit +
                                ", but there are already " + finalAcceptedCount + " accepted applicants.\n\n" +
                                "This may cause issues. Are you sure you want to continue?",
                        "Limit Reduction Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            // Update job
            List<Job> jobs = FileUtil.loadJobs();
            for (int i = 0; i < jobs.size(); i++) {
                Job j = jobs.get(i);
                if (j.getJobId().equals(finalJob.getJobId())) {
                    Job updatedJob = new Job(
                            j.getJobId(),
                            j.getMoEmail(),
                            j.getModuleCode(),
                            j.getModuleName(),
                            description,
                            hours,
                            deadline,
                            requirements,
                            j.getStatus(),
                            limit
                    );
                    jobs.set(i, updatedJob);
                    break;
                }
            }
            FileUtil.saveJobs(jobs);

            LoggerUtil.logUpdate("Job", finalJob.getJobId() + " was updated (Hours: " + hours + ", Limit: " + limit + ")");
            JOptionPane.showMessageDialog(this, "Position updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Refresh view
            viewMyJobs();
        });

        cancelBtn.addActionListener(e -> viewMyJobs());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        panel.add(buttonPanel);

        setContent(panel);
    }

    // ========== Admin Functions ==========

    private void viewAllTAs() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // ========== 顶部区域 ==========
        JPanel northArea = new JPanel();
        northArea.setLayout(new BoxLayout(northArea, BoxLayout.Y_AXIS));
        northArea.setBackground(Color.WHITE);

        // 标题
        JLabel titleLabel = new JLabel("All Teaching Assistants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        northArea.add(titleLabel);
        northArea.add(Box.createRigidArea(new Dimension(0, 12)));

        // 筛选栏
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(new Color(250, 250, 250));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // 搜索
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Search:"), gbc);

        gbc.gridx = 1;
        JTextField searchField = new JTextField(12);
        searchField.setPreferredSize(new Dimension(120, 28));
        filterPanel.add(searchField, gbc);

        // 专业筛选
        gbc.gridx = 2;
        filterPanel.add(new JLabel("Major:"), gbc);

        gbc.gridx = 3;
        JComboBox<String> majorFilter = new JComboBox<>();
        majorFilter.addItem("All");
        majorFilter.addItem("Computer Science");
        majorFilter.addItem("Software Engineering");
        majorFilter.addItem("Information Technology");
        majorFilter.addItem("Data Science");
        majorFilter.addItem("Artificial Intelligence");
        majorFilter.addItem("Other");
        majorFilter.setPreferredSize(new Dimension(130, 28));
        filterPanel.add(majorFilter, gbc);

        // 年级筛选
        gbc.gridx = 4;
        filterPanel.add(new JLabel("Grade:"), gbc);

        gbc.gridx = 5;
        JComboBox<String> gradeFilter = new JComboBox<>();
        gradeFilter.addItem("All");
        gradeFilter.addItem("1st Year");
        gradeFilter.addItem("2nd Year");
        gradeFilter.addItem("3rd Year");
        gradeFilter.addItem("4th Year");
        gradeFilter.addItem("Graduate");
        gradeFilter.setPreferredSize(new Dimension(100, 28));
        filterPanel.add(gradeFilter, gbc);

        // 按钮
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JButton searchBtn = new JButton("Search");
        searchBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchBtn.setBackground(UIHelper.PRIMARY_COLOR);
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFocusPainted(false);
        searchBtn.setPreferredSize(new Dimension(70, 28));
        filterPanel.add(searchBtn, gbc);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        JButton resetBtn = new JButton("Reset");
        resetBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resetBtn.setBackground(new Color(150, 150, 150));
        resetBtn.setForeground(Color.BLACK);
        resetBtn.setFocusPainted(false);
        resetBtn.setPreferredSize(new Dimension(70, 28));
        filterPanel.add(resetBtn, gbc);

        northArea.add(filterPanel);
        northArea.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(northArea, BorderLayout.NORTH);

        // ========== 表格区域 ==========
        List<Profile> allProfiles = FileUtil.loadProfiles();
        Map<String, Profile> profileMap = new HashMap<>();
        for (Profile p : allProfiles) {
            profileMap.put(p.getEmail(), p);
        }

        List<User> taUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getRole().equals("TA")) {
                taUsers.add(user);
            }
        }

        // 创建表格面板
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(Color.WHITE);

        // 表头 - 固定高度
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        headerPanel.setPreferredSize(new Dimension(0, 32));

        JLabel nameHeader = new JLabel("Name", SwingConstants.CENTER);
        nameHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(nameHeader);

        JLabel idHeader = new JLabel("Student ID", SwingConstants.CENTER);
        idHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        idHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(idHeader);

        JLabel majorHeader = new JLabel("Major", SwingConstants.CENTER);
        majorHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        majorHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(majorHeader);

        JLabel gradeHeader = new JLabel("Grade", SwingConstants.CENTER);
        gradeHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        gradeHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(gradeHeader);

        JLabel actionHeader = new JLabel("Action", SwingConstants.CENTER);
        actionHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        actionHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(actionHeader);

        tablePanel.add(headerPanel);

        // 刷新表格的方法
        Runnable refreshTable = () -> {
            while (tablePanel.getComponentCount() > 1) {
                tablePanel.remove(tablePanel.getComponentCount() - 1);
            }

            String searchText = searchField.getText().trim().toLowerCase();
            String selectedMajor = (String) majorFilter.getSelectedItem();
            String selectedGrade = (String) gradeFilter.getSelectedItem();

            List<User> filtered = new ArrayList<>();
            for (User user : taUsers) {
                Profile p = profileMap.get(user.getEmail());

                if (!searchText.isEmpty() && !user.getName().toLowerCase().contains(searchText)
                        && !user.getEmail().toLowerCase().contains(searchText)) {
                    continue;
                }

                if (!selectedMajor.equals("All") && (p == null || !selectedMajor.equals(p.getMajor()))) {
                    continue;
                }

                if (!selectedGrade.equals("All") && (p == null || !selectedGrade.equals(p.getGrade()))) {
                    continue;
                }

                filtered.add(user);
            }

            if (filtered.isEmpty()) {
                JLabel empty = new JLabel("No TAs found", SwingConstants.CENTER);
                empty.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                empty.setForeground(new Color(150, 150, 150));
                empty.setAlignmentX(Component.CENTER_ALIGNMENT);
                empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
                tablePanel.add(empty);
            } else {
                for (User user : filtered) {
                    Profile p = profileMap.get(user.getEmail());

                    JPanel row = new JPanel(new GridLayout(1, 5, 5, 0));
                    row.setBackground(Color.WHITE);
                    row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                    row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
                    row.setPreferredSize(new Dimension(0, 38));

                    JLabel nameLabel = new JLabel(user.getName(), SwingConstants.CENTER);
                    nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    row.add(nameLabel);

                    String sid = (p != null && p.getStudentId() != null) ? p.getStudentId() : "-";
                    JLabel idLabel = new JLabel(sid, SwingConstants.CENTER);
                    idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    row.add(idLabel);

                    String major = (p != null && p.getMajor() != null) ? p.getMajor() : "-";
                    JLabel majorLabel = new JLabel(major, SwingConstants.CENTER);
                    majorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    row.add(majorLabel);

                    String grade = (p != null && p.getGrade() != null) ? p.getGrade() : "-";
                    JLabel gradeLabel = new JLabel(grade, SwingConstants.CENTER);
                    gradeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    row.add(gradeLabel);

                    JButton viewBtn = new JButton("Details");
                    viewBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    viewBtn.setBackground(new Color(79, 114, 139));
                    viewBtn.setForeground(Color.WHITE);
                    viewBtn.setFocusPainted(false);
                    viewBtn.setBorderPainted(false);
                    viewBtn.setPreferredSize(new Dimension(60, 26));

                    final User finalUser = user;
                    final Profile finalProfile = p;
                    viewBtn.addActionListener(e -> {
                        if (finalProfile != null) {
                            viewTaProfile(finalProfile, finalUser);
                        } else {
                            JOptionPane.showMessageDialog(this, "No profile data available");
                        }
                    });

                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                    btnPanel.setBackground(Color.WHITE);
                    btnPanel.add(viewBtn);
                    row.add(btnPanel);

                    tablePanel.add(row);
                }
            }

            tablePanel.revalidate();
            tablePanel.repaint();
        };

        searchBtn.addActionListener(e -> refreshTable.run());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            majorFilter.setSelectedIndex(0);
            gradeFilter.setSelectedIndex(0);
            refreshTable.run();
        });

        refreshTable.run();

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 底部统计
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(250, 250, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        JLabel countLabel = new JLabel("Total: " + taUsers.size() + " TAs");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        countLabel.setForeground(new Color(100, 100, 100));
        bottomPanel.add(countLabel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setContent(panel);
    }
    private void viewWorkload() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("TA Workload Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Load all data
        List<User> allUsers = FileUtil.loadUsers();
        List<Profile> allProfiles = FileUtil.loadProfiles();
        List<Application> allApps = FileUtil.loadApplications();
        List<Job> allJobs = FileUtil.loadJobs();

        // Calculate workload for each TA
        Map<String, Integer> workloadMap = new HashMap<>();
        Map<String, List<Job>> taJobsMap = new HashMap<>();

        // Initialize workload for all TA users
        for (User user : allUsers) {
            if (user.getRole().equals("TA")) {
                workloadMap.put(user.getEmail(), 0);
                taJobsMap.put(user.getEmail(), new ArrayList<>());
            }
        }

        // Calculate workload from accepted applications
        for (Application app : allApps) {
            if (app.getStatus().equals("ACCEPTED")) {
                for (Job job : allJobs) {
                    if (job.getJobId().equals(app.getJobId())) {
                        int currentHours = workloadMap.getOrDefault(app.getTaEmail(), 0);
                        workloadMap.put(app.getTaEmail(), currentHours + job.getWeeklyHours());

                        List<Job> jobs = taJobsMap.getOrDefault(app.getTaEmail(), new ArrayList<>());
                        jobs.add(job);
                        taJobsMap.put(app.getTaEmail(), jobs);
                        break;
                    }
                }
            }
        }

        // Create table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(Color.WHITE);

        // Header - 固定高度32px
        JPanel headerPanel = new JPanel(new GridLayout(1, 4, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        headerPanel.setPreferredSize(new Dimension(0, 32));

        JLabel nameHeader = new JLabel("TA Name", SwingConstants.CENTER);
        nameHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nameHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(nameHeader);

        JLabel emailHeader = new JLabel("Email", SwingConstants.CENTER);
        emailHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        emailHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(emailHeader);

        JLabel hoursHeader = new JLabel("Total Hours", SwingConstants.CENTER);
        hoursHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hoursHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(hoursHeader);

        JLabel statusHeader = new JLabel("Status", SwingConstants.CENTER);
        statusHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusHeader.setForeground(new Color(79, 114, 139));
        headerPanel.add(statusHeader);

        tablePanel.add(headerPanel);

        // Create list of TA workload entries and sort by hours (highest first)
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(workloadMap.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            String email = entry.getKey();
            int hours = entry.getValue();

            // Find TA user
            User taUser = null;
            for (User u : allUsers) {
                if (u.getEmail().equals(email)) {
                    taUser = u;
                    break;
                }
            }

            // Find TA profile
            Profile taProfile = null;
            for (Profile p : allProfiles) {
                if (p.getEmail().equals(email)) {
                    taProfile = p;
                    break;
                }
            }

            String name = taUser != null ? taUser.getName() : email;
            String studentId = taProfile != null ? taProfile.getStudentId() : "N/A";

            JPanel rowPanel = new JPanel(new GridLayout(1, 4, 5, 0));
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            rowPanel.setPreferredSize(new Dimension(0, 38));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

            JLabel nameLabel = new JLabel(name + " (" + studentId + ")", SwingConstants.CENTER);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rowPanel.add(nameLabel);

            JLabel emailLabel = new JLabel(email, SwingConstants.CENTER);
            emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            rowPanel.add(emailLabel);

            // Hours with color coding
            JLabel hoursLabel = new JLabel(hours + " hrs/week", SwingConstants.CENTER);
            hoursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            if (hours > 15) {
                hoursLabel.setForeground(new Color(244, 67, 54));
            } else if (hours > 10) {
                hoursLabel.setForeground(new Color(255, 152, 0));
            } else if (hours > 5) {
                hoursLabel.setForeground(new Color(76, 175, 80));
            } else {
                hoursLabel.setForeground(new Color(150, 150, 150));
            }
            rowPanel.add(hoursLabel);

            // Status
            String statusText;
            Color statusColor;
            if (hours > 15) {
                statusText = "Overloaded";
                statusColor = new Color(244, 67, 54);
            } else if (hours > 10) {
                statusText = "Heavy";
                statusColor = new Color(255, 152, 0);
            } else if (hours > 0) {
                statusText = "Normal";
                statusColor = new Color(76, 175, 80);
            } else {
                statusText = "Idle";
                statusColor = new Color(150, 150, 150);
            }

            JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);
            statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            statusLabel.setForeground(statusColor);
            rowPanel.add(statusLabel);

            // Make row clickable to show detailed job list
            final String finalEmail = email;
            final Map<String, List<Job>> finalTaJobsMap = taJobsMap;
            rowPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showTaJobDetails(finalEmail, finalTaJobsMap.get(finalEmail));
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    rowPanel.setBackground(new Color(245, 245, 245));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    rowPanel.setBackground(Color.WHITE);
                }
            });

            tablePanel.add(rowPanel);
        }

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add summary panel at bottom
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(new Color(250, 250, 250));
        summaryPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        summaryPanel.add(Box.createRigidArea(new Dimension(5, 8)));

        int totalHours = 0;
        int activeTAs = 0;
        int overloadedTAs = 0;
        for (int hours : workloadMap.values()) {
            totalHours += hours;
            if (hours > 0) activeTAs++;
            if (hours > 15) overloadedTAs++;
        }

        JLabel summaryLabel = new JLabel("Summary: " + activeTAs + " active TAs | Total workload: " + totalHours + " hrs/week | Overloaded: " + overloadedTAs + " TAs");
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        summaryLabel.setForeground(new Color(100, 100, 100));
        summaryPanel.add(summaryLabel);

        panel.add(summaryPanel, BorderLayout.SOUTH);

        setContent(panel);
    }

    /**
     * Show detailed job list for a specific TA
     */
    private void showTaJobDetails(String taEmail, List<Job> jobs) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title with back button
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("TA Workload Details: " + taEmail);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = UIHelper.createButton("Back to Workload", UIHelper.SECONDARY_COLOR);
        backBtn.addActionListener(e -> viewWorkload());
        titlePanel.add(backBtn, BorderLayout.EAST);

        panel.add(titlePanel, BorderLayout.NORTH);

        if (jobs == null || jobs.isEmpty()) {
            JLabel emptyLabel = new JLabel("This TA is not assigned to any positions.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel, BorderLayout.CENTER);
            setContent(panel);
            return;
        }

        // Calculate total hours
        int totalHours = 0;
        for (Job job : jobs) {
            totalHours += job.getWeeklyHours();
        }

        // Job list table
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createHeaderLabel("Module Code"));
        headerPanel.add(createHeaderLabel("Module Name"));
        headerPanel.add(createHeaderLabel("Weekly Hours"));
        headerPanel.add(createHeaderLabel("MO Email"));
        headerPanel.add(createHeaderLabel("Deadline"));
        tablePanel.add(headerPanel);

        for (Job job : jobs) {
            JPanel rowPanel = new JPanel(new GridLayout(1, 5, 5, 0));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            rowPanel.setPreferredSize(new Dimension(0, 40));

            rowPanel.add(createCellLabel(job.getModuleCode()));
            rowPanel.add(createCellLabel(job.getModuleName()));
            rowPanel.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
            rowPanel.add(createCellLabel(job.getMoEmail()));
            rowPanel.add(createCellLabel(job.getDeadline()));

            tablePanel.add(rowPanel);
        }

        // Total row
        JPanel totalRow = new JPanel(new GridLayout(1, 5, 5, 0));
        totalRow.setBackground(new Color(240, 240, 240));
        totalRow.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        totalRow.add(createCellLabel(""));
        totalRow.add(createCellLabel("TOTAL:"));
        totalRow.add(createCellLabel(totalHours + " hrs/week"));
        totalRow.add(createCellLabel(""));
        totalRow.add(createCellLabel(""));
        tablePanel.add(totalRow);

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        setContent(panel);
    }

    /**
     * Manage TA workload - Admin can reassign or remove TA from positions
     */
    private void manageWorkload() {
        // Load all data
        List<User> allUsers = FileUtil.loadUsers();
        List<Profile> allProfiles = FileUtil.loadProfiles();
        List<Application> allApps = FileUtil.loadApplications();
        List<Job> allJobs = FileUtil.loadJobs();

        // Collect all TAs with their workload
        List<TaWorkloadInfo> taWorkloadList = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getRole().equals("TA")) {
                TaWorkloadInfo info = new TaWorkloadInfo();
                info.taUser = user;

                // Find profile
                for (Profile p : allProfiles) {
                    if (p.getEmail().equals(user.getEmail())) {
                        info.profile = p;
                        break;
                    }
                }

                // Calculate workload and collect jobs
                int totalHours = 0;
                List<Job> taJobs = new ArrayList<>();
                for (Application app : allApps) {
                    if (app.getTaEmail().equals(user.getEmail()) && "ACCEPTED".equals(app.getStatus())) {
                        for (Job job : allJobs) {
                            if (job.getJobId().equals(app.getJobId())) {
                                totalHours += job.getWeeklyHours();
                                taJobs.add(job);
                                break;
                            }
                        }
                    }
                }
                info.totalHours = totalHours;
                info.assignedJobs = taJobs;
                taWorkloadList.add(info);
            }
        }

        // Sort by workload (highest first)
        taWorkloadList.sort((a, b) -> b.totalHours - a.totalHours);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage TA Workload");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        if (taWorkloadList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No TAs registered yet.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            panel.add(emptyLabel, BorderLayout.CENTER);
            setContent(panel);
            return;
        }

        // Create main panel with scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Instructions
        JLabel instructionLabel = new JLabel("Click on a TA to view and manage their assigned positions");
        instructionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(100, 100, 100));
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(instructionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setMaximumSize(new Dimension(800, 45));
        headerPanel.add(createHeaderLabel("TA Name"));
        headerPanel.add(createHeaderLabel("Student ID"));
        headerPanel.add(createHeaderLabel("Email"));
        headerPanel.add(createHeaderLabel("Current Hours"));
        headerPanel.add(createHeaderLabel("Status"));
        mainPanel.add(headerPanel);

        // TA list
        for (TaWorkloadInfo info : taWorkloadList) {
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            rowPanel.setMaximumSize(new Dimension(800, 55));
            rowPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel infoPanel = new JPanel(new GridLayout(1, 5, 10, 0));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            String name = info.taUser.getName();
            String studentId = info.profile != null ? info.profile.getStudentId() : "N/A";
            String email = info.taUser.getEmail();
            String hours = info.totalHours + " hrs/week";

            infoPanel.add(createCellLabel(name));
            infoPanel.add(createCellLabel(studentId));
            infoPanel.add(createCellLabel(email));

            JLabel hoursLabel = createCellLabel(hours);
            if (info.totalHours > 15) {
                hoursLabel.setForeground(new Color(244, 67, 54));
            } else if (info.totalHours > 10) {
                hoursLabel.setForeground(new Color(255, 152, 0));
            } else {
                hoursLabel.setForeground(new Color(76, 175, 80));
            }
            infoPanel.add(hoursLabel);

            String statusText;
            Color statusColor;
            if (info.totalHours > 15) {
                statusText = "Overloaded";
                statusColor = new Color(244, 67, 54);
            } else if (info.totalHours > 10) {
                statusText = "Heavy";
                statusColor = new Color(255, 152, 0);
            } else if (info.totalHours > 0) {
                statusText = "Normal";
                statusColor = new Color(76, 175, 80);
            } else {
                statusText = "Idle";
                statusColor = new Color(150, 150, 150);
            }
            JLabel statusLabel = createCellLabel(statusText);
            statusLabel.setForeground(statusColor);
            infoPanel.add(statusLabel);

            rowPanel.add(infoPanel, BorderLayout.CENTER);

            // Make row clickable
            final TaWorkloadInfo finalInfo = info;
            rowPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showManageTaWorkloadDialog(finalInfo, allJobs, allApps);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    rowPanel.setBackground(new Color(245, 245, 245));
                    infoPanel.setBackground(new Color(245, 245, 245));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    rowPanel.setBackground(Color.WHITE);
                    infoPanel.setBackground(Color.WHITE);
                }
            });

            mainPanel.add(rowPanel);
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        setContent(panel);
    }

    /**
     * Inner class to hold TA workload information
     */
    private class TaWorkloadInfo {
        User taUser;
        Profile profile;
        int totalHours;
        List<Job> assignedJobs;
    }

    /**
     * Show dialog to manage a specific TA's workload
     */
    private void showManageTaWorkloadDialog(TaWorkloadInfo info, List<Job> allJobs, List<Application> allApps) {
        JDialog dialog = new JDialog(this, "Manage Workload: " + info.taUser.getName(), true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // TA Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(new JLabel("TA: " + info.taUser.getName()));
        infoPanel.add(new JLabel("Email: " + info.taUser.getEmail()));
        if (info.profile != null) {
            infoPanel.add(new JLabel("Student ID: " + info.profile.getStudentId()));
            infoPanel.add(new JLabel("Major: " + info.profile.getMajor()));
        }
        infoPanel.add(new JLabel("Total Hours: " + info.totalHours + " hrs/week"));

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Assigned Jobs Section
        JLabel jobsLabel = new JLabel("Assigned Positions:");
        jobsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        jobsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(jobsLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        if (info.assignedJobs.isEmpty()) {
            JLabel emptyLabel = new JLabel("No positions assigned.");
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            emptyLabel.setForeground(new Color(150, 150, 150));
            emptyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            mainPanel.add(emptyLabel);
        } else {
            // Jobs table
            JPanel jobsTable = new JPanel();
            jobsTable.setLayout(new BoxLayout(jobsTable, BoxLayout.Y_AXIS));
            jobsTable.setBackground(Color.WHITE);

            // Header
            JPanel header = new JPanel(new GridLayout(1, 5, 5, 0));
            header.setBackground(new Color(240, 240, 240));
            header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            header.add(createHeaderLabel("Module"));
            header.add(createHeaderLabel("Weekly Hours"));
            header.add(createHeaderLabel("MO"));
            header.add(createHeaderLabel("Deadline"));
            header.add(createHeaderLabel("Action"));
            jobsTable.add(header);

            for (Job job : info.assignedJobs) {
                JPanel row = new JPanel(new GridLayout(1, 5, 5, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                row.setPreferredSize(new Dimension(0, 45));

                row.add(createCellLabel(job.getModuleCode() + " - " + job.getModuleName()));
                row.add(createCellLabel(job.getWeeklyHours() + " hrs"));
                row.add(createCellLabel(job.getMoEmail()));
                row.add(createCellLabel(job.getDeadline()));

                // Remove button
                JButton removeBtn = new JButton("Remove");
                removeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                removeBtn.setBackground(new Color(244, 67, 54));
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.setBorderPainted(false);
                removeBtn.setOpaque(true);
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                final Job finalJob = job;
                removeBtn.addActionListener(e -> {
                    int confirm = JOptionPane.showConfirmDialog(dialog,
                            "Remove " + info.taUser.getName() + " from " + finalJob.getModuleCode() + "?\n\n" +
                                    "This will free up " + finalJob.getWeeklyHours() + " hours/week from this TA.",
                            "Confirm Removal",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Update application status to REJECTED for this TA
                        List<Application> apps = FileUtil.loadApplications();
                        for (Application app : apps) {
                            if (app.getTaEmail().equals(info.taUser.getEmail()) &&
                                    app.getJobId().equals(finalJob.getJobId()) &&
                                    "ACCEPTED".equals(app.getStatus())) {
                                app.setStatus("REJECTED");
                                break;
                            }
                        }
                        FileUtil.saveApplications(apps);

                        LoggerUtil.logUpdate("Workload", "Removed TA " + info.taUser.getEmail() + " from job " + finalJob.getJobId());
                        JOptionPane.showMessageDialog(dialog, "TA removed from position successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        manageWorkload(); // Refresh
                    }
                });

                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                actionPanel.setBackground(Color.WHITE);
                actionPanel.add(removeBtn);
                row.add(actionPanel);

                jobsTable.add(row);
            }

            JScrollPane scrollPane = new JScrollPane(jobsTable);
            scrollPane.setPreferredSize(new Dimension(540, 250));
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            mainPanel.add(scrollPane);
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Close button
        JButton closeBtn = UIHelper.createButton("Close", UIHelper.SECONDARY_COLOR);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        mainPanel.add(closeBtn);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void viewAllJobs() {
        List<Job> allJobs = FileUtil.loadJobs();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title with filter options
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("All Positions");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        topPanel.add(titleLabel, BorderLayout.WEST);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter by status:"));

        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All", "Open", "Closed"});
        statusFilter.setPreferredSize(new Dimension(100, 30));
        filterPanel.add(statusFilter);

        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        filterPanel.add(refreshBtn);
        topPanel.add(filterPanel, BorderLayout.EAST);

        panel.add(topPanel, BorderLayout.NORTH);

        // Create table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createHeaderLabel("Job ID"));
        headerPanel.add(createHeaderLabel("Module Code"));
        headerPanel.add(createHeaderLabel("Module Name"));
        headerPanel.add(createHeaderLabel("MO Email"));
        headerPanel.add(createHeaderLabel("Weekly Hours"));
        headerPanel.add(createHeaderLabel("Deadline"));
        headerPanel.add(createHeaderLabel("Status"));
        tablePanel.add(headerPanel);

        // Function to refresh table based on filter
        java.awt.event.ActionListener refreshAction = e -> {
            String filter = (String) statusFilter.getSelectedItem();
            refreshAllJobsTable(tablePanel, allJobs, filter);
        };

        refreshBtn.addActionListener(refreshAction);

        // Initial load
        refreshAllJobsTable(tablePanel, allJobs, "All");

        JScrollPane scrollPane = new JScrollPane(tablePanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        setContent(panel);
    }

    private void refreshAllJobsTable(JPanel tablePanel, List<Job> allJobs, String filter) {
        tablePanel.removeAll();

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(1, 7, 5, 0));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(createHeaderLabel("Job ID"));
        headerPanel.add(createHeaderLabel("Module Code"));
        headerPanel.add(createHeaderLabel("Module Name"));
        headerPanel.add(createHeaderLabel("MO Email"));
        headerPanel.add(createHeaderLabel("Weekly Hours"));
        headerPanel.add(createHeaderLabel("Deadline"));
        headerPanel.add(createHeaderLabel("Status"));
        tablePanel.add(headerPanel);

        // Filter jobs
        List<Job> filteredJobs = new ArrayList<>();
        for (Job job : allJobs) {
            if (filter.equals("All")) {
                filteredJobs.add(job);
            } else if (filter.equals("Open") && job.getStatus().equals("OPEN")) {
                filteredJobs.add(job);
            } else if (filter.equals("Closed") && job.getStatus().equals("CLOSED")) {
                filteredJobs.add(job);
            }
        }

        if (filteredJobs.isEmpty()) {
            JLabel emptyLabel = new JLabel("No positions found.", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            tablePanel.add(emptyLabel);
        } else {
            for (Job job : filteredJobs) {
                JPanel rowPanel = new JPanel(new GridLayout(1, 7, 5, 0));
                rowPanel.setBackground(Color.WHITE);
                rowPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                rowPanel.setPreferredSize(new Dimension(0, 40));

                rowPanel.add(createCellLabel(job.getJobId()));
                rowPanel.add(createCellLabel(job.getModuleCode()));
                rowPanel.add(createCellLabel(job.getModuleName()));
                rowPanel.add(createCellLabel(job.getMoEmail()));
                rowPanel.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
                rowPanel.add(createCellLabel(job.getDeadline()));

                String statusText = job.getStatus().equals("OPEN") ? "Open" : "Closed";
                Color statusColor = job.getStatus().equals("OPEN") ? new Color(76, 175, 80) : new Color(150, 150, 150);
                JLabel statusLabel = createCellLabel(statusText);
                statusLabel.setForeground(statusColor);
                rowPanel.add(statusLabel);

                tablePanel.add(rowPanel);
            }
        }

        tablePanel.revalidate();
        tablePanel.repaint();
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

        JPanel buttonPanel = new JPanel();
        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> viewLogs());
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        setContent(panel);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            LoggerUtil.logLogout(currentUser.getEmail(), currentUser.getRole());
            dispose();
            new LoginFrame();
        }
    }
    /**
     * View a TA's full profile (for MO)
     */
    private void viewTaProfile(Profile taProfile, User taUser) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title with back button
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("TA Profile: " + (taUser != null ? taUser.getName() : taProfile.getEmail()));
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JButton backBtn = UIHelper.createButton("Back to Applicants", UIHelper.SECONDARY_COLOR);
        backBtn.addActionListener(e -> viewApplicants());
        titlePanel.add(backBtn, BorderLayout.EAST);

        panel.add(titlePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Basic user info
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(Color.WHITE);
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("Account Information"));
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.setMaximumSize(new Dimension(500, 120));

        addInfoRow(userInfoPanel, "Name:", taUser != null ? taUser.getName() : "-");
        addInfoRow(userInfoPanel, "Email:", taProfile.getEmail());
        addInfoRow(userInfoPanel, "Role:", "TA");

        panel.add(userInfoPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Personal information
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        profilePanel.setMaximumSize(new Dimension(500, 300));

        addInfoRow(profilePanel, "Student ID:", taProfile.getStudentId());
        addInfoRow(profilePanel, "Major:", taProfile.getMajor());
        addInfoRow(profilePanel, "Grade:", taProfile.getGrade());
        addInfoRow(profilePanel, "Phone:", taProfile.getPhone());
        if (taProfile.getDescription() != null && !taProfile.getDescription().isEmpty()) {
            addInfoRow(profilePanel, "Description:", taProfile.getDescription());
        }

        panel.add(profilePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // CV section with View and Download buttons
        JPanel cvPanel = new JPanel();
        cvPanel.setLayout(new BoxLayout(cvPanel, BoxLayout.Y_AXIS));
        cvPanel.setBackground(Color.WHITE);
        cvPanel.setBorder(BorderFactory.createTitledBorder("CV / Resume"));
        cvPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cvPanel.setMaximumSize(new Dimension(500, 120));

        if (taProfile.getCvPath() != null && !taProfile.getCvPath().isEmpty()) {
            addInfoRow(cvPanel, "CV File:", taProfile.getCvPath());

            JPanel cvButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            cvButtonPanel.setBackground(Color.WHITE);

            // View CV button
            JButton viewCvBtn = UIHelper.createButton("View CV", UIHelper.PRIMARY_COLOR);
            viewCvBtn.addActionListener(e -> viewCvFile(taProfile.getCvPath()));

            // Download CV button
            JButton downloadCvBtn = UIHelper.createButton("Download CV", UIHelper.SUCCESS_COLOR);
            downloadCvBtn.addActionListener(e -> downloadCvFile(taProfile.getCvPath(), taProfile.getEmail()));

            cvButtonPanel.add(viewCvBtn);
            cvButtonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
            cvButtonPanel.add(downloadCvBtn);

            cvPanel.add(cvButtonPanel);
        } else {
            addInfoRow(cvPanel, "CV:", "Not uploaded yet");
        }

        panel.add(cvPanel);

        setContent(panel);
    }
    /**
     * View CV file (open PDF in system default viewer)
     */
    private void viewCvFile(String cvPath) {
        File cvFile = new File(cvPath);
        if (!cvFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "CV file not found at: " + cvPath + "\n\nPlease upload your CV again.",
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Open PDF with system default application
            Desktop.getDesktop().open(cvFile);
            LoggerUtil.logInfo("TA " + currentUser.getEmail() + " viewed CV: " + cvPath);
        } catch (IOException e) {
            LoggerUtil.logError("View CV", "Failed to open CV: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Failed to open CV. Please make sure you have a PDF viewer installed.\n\n" +
                            "Error: " + e.getMessage(),
                    "Cannot Open File", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Download CV file (save to user-selected location)
     */
    private void downloadCvFile(String sourcePath, String taEmail) {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "CV file not found at: " + sourcePath + "\n\nThe TA may need to upload their CV again.",
                    "File Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open file chooser for save location
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CV As");
        fileChooser.setSelectedFile(new File(taEmail + "_CV.pdf"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File destFile = fileChooser.getSelectedFile();
            // Ensure .pdf extension
            if (!destFile.getName().toLowerCase().endsWith(".pdf")) {
                destFile = new File(destFile.getAbsolutePath() + ".pdf");
            }

            try {
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this,
                        "CV downloaded successfully to:\n" + destFile.getAbsolutePath(),
                        "Download Complete", JOptionPane.INFORMATION_MESSAGE);
                LoggerUtil.logInfo("MO " + currentUser.getEmail() + " downloaded CV for TA: " + taEmail);
            } catch (IOException e) {
                LoggerUtil.logError("Download CV", "Failed to download CV: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Failed to download CV: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * 按钮渲染器 - 用于在表格中显示按钮
     */
    class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton editBtn;
        private JButton deleteBtn;
        private JButton toggleBtn;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            setOpaque(true);

            editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            editBtn.setBackground(new Color(255, 152, 0));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setOpaque(true);
            editBtn.setPreferredSize(new Dimension(60, 35));

            deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            deleteBtn.setBackground(new Color(244, 67, 54));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setOpaque(true);
            deleteBtn.setPreferredSize(new Dimension(60, 35));

            toggleBtn = new JButton();
            toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            toggleBtn.setForeground(Color.WHITE);
            toggleBtn.setFocusPainted(false);
            toggleBtn.setBorderPainted(false);
            toggleBtn.setOpaque(true);
            toggleBtn.setPreferredSize(new Dimension(60, 35));

            add(editBtn);
            add(deleteBtn);
            add(toggleBtn);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Job) {
                Job job = (Job) value;
                if ("OPEN".equals(job.getStatus())) {
                    toggleBtn.setText("Close");
                    toggleBtn.setBackground(new Color(244, 67, 54));
                } else {
                    toggleBtn.setText("Reopen");
                    toggleBtn.setBackground(new Color(76, 175, 80));
                }
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
            }
            return this;  // 直接返回 this，因为 ButtonRenderer 本身就是 JPanel
        }
    }

    /**
     * 按钮编辑器 - 处理按钮点击事件
     */
    class ButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private JPanel panel;
        private JButton editBtn;
        private JButton deleteBtn;
        private JButton toggleBtn;
        private Job currentJob;
        private DashboardFrame frame;
        private List<Application> allApps;

        public ButtonEditor(JCheckBox checkBox, DashboardFrame frame, List<Application> allApps) {
            this.frame = frame;
            this.allApps = allApps;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
            panel.setOpaque(true);

            editBtn = new JButton("Edit");
            editBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            editBtn.setBackground(new Color(255, 152, 0));
            editBtn.setForeground(Color.WHITE);
            editBtn.setFocusPainted(false);
            editBtn.setBorderPainted(false);
            editBtn.setOpaque(true);
            editBtn.setPreferredSize(new Dimension(60, 35));

            deleteBtn = new JButton("Delete");
            deleteBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            deleteBtn.setBackground(new Color(244, 67, 54));
            deleteBtn.setForeground(Color.WHITE);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setOpaque(true);
            deleteBtn.setPreferredSize(new Dimension(60, 35));

            toggleBtn = new JButton();
            toggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            toggleBtn.setForeground(Color.WHITE);
            toggleBtn.setFocusPainted(false);
            toggleBtn.setBorderPainted(false);
            toggleBtn.setOpaque(true);
            toggleBtn.setPreferredSize(new Dimension(60, 35));

            editBtn.addActionListener(e -> {
                if (currentJob != null) {
                    frame.editPostedPosition(currentJob);
                    fireEditingStopped();
                }
            });

            deleteBtn.addActionListener(e -> {
                if (currentJob != null) {
                    int applicantCount = 0;
                    for (Application app : allApps) {
                        if (app.getJobId().equals(currentJob.getJobId())) {
                            applicantCount++;
                        }
                    }

                    String message = applicantCount > 0 ?
                            "⚠ Warning: This position has " + applicantCount + " applicant(s).\n\nDeleting it will remove the position and ALL associated applications.\n\nThis action cannot be undone.\n\nAre you sure you want to delete?" :
                            "Are you sure you want to delete this position?\n\nThis action cannot be undone.";

                    int confirm = JOptionPane.showConfirmDialog(frame, message, "Confirm Delete",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        List<Job> jobs = FileUtil.loadJobs();
                        jobs.removeIf(j -> j.getJobId().equals(currentJob.getJobId()));
                        FileUtil.saveJobs(jobs);
                        if (applicantCount > 0) {
                            List<Application> updatedApps = FileUtil.loadApplications();
                            updatedApps.removeIf(app -> app.getJobId().equals(currentJob.getJobId()));
                            FileUtil.saveApplications(updatedApps);
                        }
                        LoggerUtil.logDelete("Job", currentJob.getJobId());
                        frame.viewMyJobs();
                        fireEditingStopped();
                    }
                }
            });

            toggleBtn.addActionListener(e -> {
                if (currentJob != null) {
                    // Count accepted applicants
                    int acceptedCount = (int) allApps.stream()
                            .filter(app -> app.getJobId().equals(currentJob.getJobId()) && "ACCEPTED".equals(app.getStatus()))
                            .count();

                    String newStatus = "OPEN".equals(currentJob.getStatus()) ? "CLOSED" : "OPEN";
                    if (newStatus.equals("OPEN") && acceptedCount >= currentJob.getApplicantLimit()) {
                        JOptionPane.showMessageDialog(frame,
                                "Cannot reopen: Position has reached applicant limit (" + acceptedCount + "/" + currentJob.getApplicantLimit() + ").",
                                "Position Full", JOptionPane.WARNING_MESSAGE);
                        fireEditingStopped();
                        return;
                    }
                    int confirm = JOptionPane.showConfirmDialog(frame,
                            (newStatus.equals("CLOSED") ? "Close" : "Reopen") + " position?",
                            "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        List<Job> jobs = FileUtil.loadJobs();
                        for (Job j : jobs) {
                            if (j.getJobId().equals(currentJob.getJobId())) {
                                j.setStatus(newStatus);
                                break;
                            }
                        }
                        FileUtil.saveJobs(jobs);
                        frame.viewMyJobs();
                        fireEditingStopped();
                    }
                }
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
            panel.add(toggleBtn);
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value,
                                                              boolean isSelected, int row, int column) {
            currentJob = (Job) value;
            if (currentJob != null) {
                if ("OPEN".equals(currentJob.getStatus())) {
                    toggleBtn.setText("Close");
                    toggleBtn.setBackground(new Color(244, 67, 54));
                } else {
                    toggleBtn.setText("Reopen");
                    toggleBtn.setBackground(new Color(76, 175, 80));
                }
            }
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentJob;
        }
    }
}
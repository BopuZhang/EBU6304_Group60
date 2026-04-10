package system.ta;

import system.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Frame for TA to view available jobs and submit applications.
 * Improved distinction between "Apply" and "Applied" buttons.
 */
public class TAJobListFrame extends JFrame {
    private final User currentUser;
    private List<Job> jobs;
    private List<Application> applications;
    private JPanel tableContainer;

    public TAJobListFrame(User user) {
        this.currentUser = user;
        this.jobs = FileUtil.loadJobs();
        this.applications = FileUtil.loadApplications();

        setTitle("Available Positions");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel title = UIHelper.createTitle("Available Positions");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(title, BorderLayout.NORTH);

        tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        refreshTable();

        JScrollPane scrollPane = UIHelper.createScrollPane(tableContainer);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        card.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.SECONDARY_COLOR);
        refreshBtn.addActionListener(e -> refreshTable());
        bottomPanel.add(refreshBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void refreshTable() {
        tableContainer.removeAll();

        // Filter open jobs
        List<Job> openJobs = new ArrayList<>();
        for (Job job : jobs) {
            if ("OPEN".equals(job.getStatus())) {
                openJobs.add(job);
            }
        }
        openJobs.sort(Comparator.comparing(Job::getDeadline));

        // Header
        JPanel header = new JPanel(new GridLayout(1, 7, 5, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        header.add(createHeaderLabel("Module Code"));
        header.add(createHeaderLabel("Module Name"));
        header.add(createHeaderLabel("Hours/Week"));
        header.add(createHeaderLabel("Deadline"));
        header.add(createHeaderLabel("Applicants"));
        header.add(createHeaderLabel("Status"));
        header.add(createHeaderLabel("Action"));
        tableContainer.add(header);

        if (openJobs.isEmpty()) {
            JLabel empty = new JLabel("No available positions at this time.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableContainer.add(empty);
        } else {
            for (Job job : openJobs) {
                int acceptedCount = countAcceptedApplicants(job.getJobId());
                boolean applied = hasApplied(job.getJobId());
                boolean full = acceptedCount >= job.getApplicantLimit();

                JPanel row = new JPanel(new GridLayout(1, 7, 5, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

                row.add(createCellLabel(job.getModuleCode()));
                row.add(createCellLabel(job.getModuleName()));
                row.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
                row.add(createCellLabel(job.getDeadline()));

                JLabel appLabel = createCellLabel(acceptedCount + " / " + job.getApplicantLimit());
                appLabel.setForeground(full ? new Color(244, 67, 54) : UIHelper.SUCCESS_COLOR);
                row.add(appLabel);

                String statusText;
                Color statusColor;
                if (full) {
                    statusText = "Full";
                    statusColor = new Color(244, 67, 54);
                } else if (applied) {
                    statusText = "Applied";
                    statusColor = UIHelper.SUCCESS_COLOR;
                } else {
                    statusText = "Available";
                    statusColor = UIHelper.PRIMARY_COLOR;
                }
                JLabel statusLabel = createCellLabel(statusText);
                statusLabel.setForeground(statusColor);
                row.add(statusLabel);

                // Action button with fixed size
                JButton actionBtn = createActionButton(job, applied, full);
                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                btnPanel.setBackground(Color.WHITE);
                btnPanel.add(actionBtn);
                row.add(btnPanel);

                tableContainer.add(row);
            }
        }

        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private JButton createActionButton(Job job, boolean applied, boolean full) {
        JButton btn = new JButton();
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(90, 30));
        btn.setMaximumSize(new Dimension(90, 30));
        btn.setMinimumSize(new Dimension(90, 30));
        // Fix border to prevent size change on click
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (applied) {
            btn.setText("✓ Applied");
            btn.setBackground(new Color(200, 200, 200));
            btn.setEnabled(false);
        } else if (full) {
            btn.setText("Full");
            btn.setBackground(new Color(180, 180, 180));
            btn.setEnabled(false);
        } else {
            btn.setText("Apply");
            btn.setBackground(UIHelper.PRIMARY_COLOR);
            btn.setEnabled(true);
            // Hover effect
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btn.setBackground(UIHelper.PRIMARY_COLOR.darker());
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btn.setBackground(UIHelper.PRIMARY_COLOR);
                }
            });
            btn.addActionListener(e -> submitApplication(job));
        }

        return btn;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setForeground(UIHelper.PRIMARY_COLOR);
        return label;
    }

    private JLabel createCellLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        return label;
    }

    private int countAcceptedApplicants(String jobId) {
        int count = 0;
        for (Application app : applications) {
            if (app.getJobId().equals(jobId) && "ACCEPTED".equals(app.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private boolean hasApplied(String jobId) {
        for (Application app : applications) {
            if (app.getTaEmail().equals(currentUser.getEmail()) && app.getJobId().equals(jobId)) {
                return true;
            }
        }
        return false;
    }

    private void submitApplication(Job job) {
        int confirm = UIHelper.showConfirmDialog(this,
                "Apply for " + job.getModuleCode() + " - " + job.getModuleName() + "?",
                "Confirm Application", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        String appId = "APP" + System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Application newApp = new Application(appId, time, currentUser.getEmail(), job.getJobId(), "PENDING");

        List<Application> allApps = FileUtil.loadApplications();
        allApps.add(newApp);
        FileUtil.saveApplications(allApps);
        applications = allApps;

        LoggerUtil.logInfo("TA " + currentUser.getEmail() + " applied for job " + job.getJobId());
        UIHelper.showInfoDialog(this, "Application submitted successfully!", "Success");
        refreshTable();
    }
}
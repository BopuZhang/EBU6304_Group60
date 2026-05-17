package system.admin;

import system.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for Admin to view TA workload summary.
 */
public class AdminViewWorkloadFrame extends JFrame {
    private final List<User> users;
    private final List<Profile> allProfiles;
    private final List<Application> allApps;
    private final List<Job> allJobs;

    public AdminViewWorkloadFrame(List<User> users) {
        this.users = users;
        this.allProfiles = FileUtil.loadProfiles();
        this.allApps = FileUtil.loadApplications();
        this.allJobs = FileUtil.loadJobs();

        setTitle("TA Workload Management");
        setSize(850, 500);
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

        JLabel title = UIHelper.createTitle("TA Workload Management");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(title, BorderLayout.NORTH);

        // Calculate workload map
        Map<String, Integer> workloadMap = new HashMap<>();
        Map<String, List<Job>> taJobsMap = new HashMap<>();

        for (User u : users) {
            if ("TA".equals(u.getRole())) {
                workloadMap.put(u.getEmail(), 0);
                taJobsMap.put(u.getEmail(), new ArrayList<>());
            }
        }

        for (Application app : allApps) {
            if ("ACCEPTED".equals(app.getStatus())) {
                for (Job job : allJobs) {
                    if (job.getJobId().equals(app.getJobId())) {
                        int current = workloadMap.getOrDefault(app.getTaEmail(), 0);
                        workloadMap.put(app.getTaEmail(), current + job.getWeeklyHours());
                        taJobsMap.get(app.getTaEmail()).add(job);
                        break;
                    }
                }
            }
        }

        // Table container
        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new GridLayout(1, 4, 5, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        header.add(createHeaderLabel("TA Name"));
        header.add(createHeaderLabel("Email"));
        header.add(createHeaderLabel("Total Hours"));
        header.add(createHeaderLabel("Status"));
        tableContainer.add(header);

        // Sort by hours descending
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(workloadMap.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int totalHours = 0, activeTAs = 0, overloadedTAs = 0;

        for (Map.Entry<String, Integer> entry : sorted) {
            String email = entry.getKey();
            int hours = entry.getValue();
            totalHours += hours;
            if (hours > 0) activeTAs++;
            if (hours > 15) overloadedTAs++;

            User taUser = findUser(email);
            Profile taProfile = findProfile(email);
            String name = taUser != null ? taUser.getName() : email;
            String displayName = taProfile != null ?
                    name + " (" + taProfile.getStudentId() + ")" : name;

            JPanel row = new JPanel(new GridLayout(1, 4, 5, 0));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            row.add(createCellLabel(displayName));
            row.add(createCellLabel(email));

            JLabel hoursLabel = createCellLabel(hours + " hrs/week");
            if (hours > 15) hoursLabel.setForeground(UIHelper.DANGER_COLOR);
            else if (hours > 10) hoursLabel.setForeground(UIHelper.ACCENT_COLOR);
            else if (hours > 5) hoursLabel.setForeground(UIHelper.SUCCESS_COLOR);
            else hoursLabel.setForeground(UIHelper.DISABLED_COLOR);
            row.add(hoursLabel);

            String statusText;
            Color statusColor;
            if (hours > 15) { statusText = "Overloaded"; statusColor = UIHelper.DANGER_COLOR; }
            else if (hours > 10) { statusText = "Heavy"; statusColor = UIHelper.ACCENT_COLOR; }
            else if (hours > 0) { statusText = "Normal"; statusColor = UIHelper.SUCCESS_COLOR; }
            else { statusText = "Idle"; statusColor = UIHelper.DISABLED_COLOR; }

            JLabel statusLabel = createCellLabel(statusText);
            statusLabel.setForeground(statusColor);
            row.add(statusLabel);

            // Make row clickable to show details
            final List<Job> jobs = taJobsMap.get(email);
            final String finalEmail = email;
            row.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showJobDetails(finalEmail, jobs);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    row.setBackground(new Color(245, 245, 245));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    row.setBackground(Color.WHITE);
                }
            });
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));

            tableContainer.add(row);
        }

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(new Color(250, 250, 250));
        summaryPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        String summary = String.format("Summary: %d active TAs | Total workload: %d hrs/week | Overloaded: %d TAs",
                activeTAs, totalHours, overloadedTAs);
        JLabel summaryLabel = new JLabel(summary);
        summaryLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        summaryLabel.setForeground(new Color(100, 100, 100));
        summaryPanel.add(summaryLabel);
        card.add(summaryPanel, BorderLayout.SOUTH);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private User findUser(String email) {
        for (User u : users) {
            if (u.getEmail().equals(email)) return u;
        }
        return null;
    }

    private Profile findProfile(String email) {
        for (Profile p : allProfiles) {
            if (p.getEmail().equals(email)) return p;
        }
        return null;
    }

    private void showJobDetails(String taEmail, List<Job> jobs) {
        JDialog dialog = new JDialog(this, "TA Workload Details: " + taEmail, true);
        dialog.setSize(700, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());

        JLabel title = new JLabel("Assigned Positions for " + taEmail);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        title.setForeground(UIHelper.PRIMARY_COLOR);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        card.add(title, BorderLayout.NORTH);

        JPanel tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new GridLayout(1, 5, 5, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        header.add(createHeaderLabel("Module Code"));
        header.add(createHeaderLabel("Module Name"));
        header.add(createHeaderLabel("Weekly Hours"));
        header.add(createHeaderLabel("MO Email"));
        header.add(createHeaderLabel("Deadline"));
        tableContainer.add(header);

        int total = 0;
        if (jobs == null || jobs.isEmpty()) {
            JLabel empty = new JLabel("No positions assigned.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            empty.setForeground(Color.GRAY);
            tableContainer.add(empty);
        } else {
            for (Job job : jobs) {
                total += job.getWeeklyHours();
                JPanel row = new JPanel(new GridLayout(1, 5, 5, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

                row.add(createCellLabel(job.getModuleCode()));
                row.add(createCellLabel(job.getModuleName()));
                row.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
                row.add(createCellLabel(job.getMoEmail()));
                row.add(createCellLabel(job.getDeadline()));
                tableContainer.add(row);
            }
        }

        // Total row
        JPanel totalRow = new JPanel(new GridLayout(1, 5, 5, 0));
        totalRow.setBackground(new Color(240, 240, 240));
        totalRow.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        totalRow.add(createCellLabel(""));
        totalRow.add(createCellLabel("TOTAL:"));
        totalRow.add(createCellLabel(total + " hrs/week"));
        totalRow.add(createCellLabel(""));
        totalRow.add(createCellLabel(""));
        tableContainer.add(totalRow);

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = UIHelper.createButton("Close", UIHelper.SECONDARY_COLOR);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(closeBtn);
        card.add(btnPanel, BorderLayout.SOUTH);

        dialog.add(card, BorderLayout.CENTER);
        dialog.setVisible(true);
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
}
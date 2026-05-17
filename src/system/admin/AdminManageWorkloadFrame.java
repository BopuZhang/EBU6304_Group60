package system.admin;

import system.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for Admin to manage TA workload (remove TA from positions).
 */
public class AdminManageWorkloadFrame extends JFrame {
    private final List<User> users;
    private final List<Profile> allProfiles;
    private final List<Application> allApps;
    private final List<Job> allJobs;
    private JPanel mainPanel;

    public AdminManageWorkloadFrame(List<User> users) {
        this.users = users;
        this.allProfiles = FileUtil.loadProfiles();
        this.allApps = FileUtil.loadApplications();
        this.allJobs = FileUtil.loadJobs();

        setTitle("Manage TA Workload");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIHelper.BACKGROUND_COLOR);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel title = UIHelper.createTitle("Manage TA Workload");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(title, BorderLayout.NORTH);

        JLabel instruction = new JLabel("Click on a TA to view and manage assigned positions");
        instruction.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        instruction.setForeground(Color.GRAY);
        instruction.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        card.add(instruction, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(scrollPane, BorderLayout.CENTER);

        outer.add(card, BorderLayout.CENTER);
        add(outer);

        refreshView();
    }

    private void refreshView() {
        mainPanel.removeAll();

        // Header
        JPanel header = new JPanel(new GridLayout(1, 5, 10, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        header.add(createHeaderLabel("TA Name"));
        header.add(createHeaderLabel("Student ID"));
        header.add(createHeaderLabel("Email"));
        header.add(createHeaderLabel("Current Hours"));
        header.add(createHeaderLabel("Status"));
        mainPanel.add(header);

        // Collect TA workload info
        List<TaWorkloadInfo> taList = new ArrayList<>();
        for (User u : users) {
            if ("TA".equals(u.getRole())) {
                TaWorkloadInfo info = new TaWorkloadInfo();
                info.taUser = u;
                info.profile = findProfile(u.getEmail());
                info.totalHours = 0;
                info.assignedJobs = new ArrayList<>();

                for (Application app : allApps) {
                    if (app.getTaEmail().equals(u.getEmail()) && "ACCEPTED".equals(app.getStatus())) {
                        for (Job job : allJobs) {
                            if (job.getJobId().equals(app.getJobId())) {
                                info.totalHours += job.getWeeklyHours();
                                info.assignedJobs.add(job);
                                break;
                            }
                        }
                    }
                }
                taList.add(info);
            }
        }

        taList.sort((a, b) -> b.totalHours - a.totalHours);

        for (TaWorkloadInfo info : taList) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
            if (info.totalHours > 15) hoursLabel.setForeground(UIHelper.DANGER_COLOR);
            else if (info.totalHours > 10) hoursLabel.setForeground(UIHelper.ACCENT_COLOR);
            else if (info.totalHours > 0) hoursLabel.setForeground(UIHelper.SUCCESS_COLOR);
            else hoursLabel.setForeground(UIHelper.DISABLED_COLOR);
            infoPanel.add(hoursLabel);

            String statusText;
            Color statusColor;
            if (info.totalHours > 15) { statusText = "Overloaded"; statusColor = UIHelper.DANGER_COLOR; }
            else if (info.totalHours > 10) { statusText = "Heavy"; statusColor = UIHelper.ACCENT_COLOR; }
            else if (info.totalHours > 0) { statusText = "Normal"; statusColor = UIHelper.SUCCESS_COLOR; }
            else { statusText = "Idle"; statusColor = UIHelper.DISABLED_COLOR; }

            JLabel statusLabel = createCellLabel(statusText);
            statusLabel.setForeground(statusColor);
            infoPanel.add(statusLabel);

            row.add(infoPanel, BorderLayout.CENTER);

            final TaWorkloadInfo finalInfo = info;
            row.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showManageDialog(finalInfo);
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    row.setBackground(new Color(245, 245, 245));
                    infoPanel.setBackground(new Color(245, 245, 245));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    row.setBackground(Color.WHITE);
                    infoPanel.setBackground(Color.WHITE);
                }
            });

            mainPanel.add(row);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showManageDialog(TaWorkloadInfo info) {
        JDialog dialog = new JDialog(this, "Manage Workload: " + info.taUser.getName(), true);
        dialog.setSize(650, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

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

        card.add(infoPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel jobsLabel = new JLabel("Assigned Positions:");
        jobsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        jobsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(jobsLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        if (info.assignedJobs.isEmpty()) {
            JLabel empty = new JLabel("No positions assigned.");
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.add(empty);
        } else {
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
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

                row.add(createCellLabel(job.getModuleCode() + " - " + job.getModuleName()));
                row.add(createCellLabel(job.getWeeklyHours() + " hrs"));
                row.add(createCellLabel(job.getMoEmail()));
                row.add(createCellLabel(job.getDeadline()));

                JButton removeBtn = new JButton("Remove");
                removeBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                removeBtn.setBackground(UIHelper.DANGER_COLOR);
                removeBtn.setForeground(Color.WHITE);
                removeBtn.setFocusPainted(false);
                removeBtn.setBorderPainted(false);
                removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                removeBtn.addActionListener(e -> {
                    int confirm = UIHelper.showConfirmDialog(dialog,
                            "Remove " + info.taUser.getName() + " from " + job.getModuleCode() + "?",
                            "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        List<Application> apps = FileUtil.loadApplications();
                        for (Application app : apps) {
                            if (app.getTaEmail().equals(info.taUser.getEmail()) &&
                                    app.getJobId().equals(job.getJobId()) &&
                                    "ACCEPTED".equals(app.getStatus())) {
                                app.setStatus("REJECTED");
                                break;
                            }
                        }
                        FileUtil.saveApplications(apps);
                        LoggerUtil.logUpdate("Workload", "Removed TA " + info.taUser.getEmail() + " from job " + job.getJobId());
                        UIHelper.showInfoDialog(dialog, "TA removed from position.", "Success");
                        dialog.dispose();
                        refreshView();
                    }
                });

                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                actionPanel.setBackground(Color.WHITE);
                actionPanel.add(removeBtn);
                row.add(actionPanel);

                jobsTable.add(row);
            }

            JScrollPane scroll = new JScrollPane(jobsTable);
            scroll.setPreferredSize(new Dimension(580, 200));
            scroll.setBorder(BorderFactory.createEmptyBorder());
            card.add(scroll);
        }

        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton closeBtn = UIHelper.createButton("Close", UIHelper.SECONDARY_COLOR);
        closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeBtn.addActionListener(e -> dialog.dispose());
        card.add(closeBtn);

        dialog.add(card, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private Profile findProfile(String email) {
        for (Profile p : allProfiles) {
            if (p.getEmail().equals(email)) return p;
        }
        return null;
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

    private class TaWorkloadInfo {
        User taUser;
        Profile profile;
        int totalHours;
        List<Job> assignedJobs;
    }
}
package system.mo;

import system.*;
import system.ta.TAProfileViewFrame;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for MO to view applicants for their positions, with accept/reject actions.
 */
public class MOViewApplicantsFrame extends JFrame {
    private final User currentUser;
    private List<Job> myJobs;
    private List<Application> allApps;
    private List<Profile> allProfiles;
    private List<User> allUsers;
    private JComboBox<Object> jobCombo;
    private JPanel contentPanel;

    public MOViewApplicantsFrame(User user) {
        this.currentUser = user;
        this.allApps = FileUtil.loadApplications();
        this.allProfiles = FileUtil.loadProfiles();
        this.allUsers = FileUtil.loadUsers();
        this.myJobs = loadMyJobs();

        setTitle("View Applicants");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private List<Job> loadMyJobs() {
        List<Job> all = FileUtil.loadJobs();
        List<Job> mine = new ArrayList<>();
        for (Job job : all) {
            if (job.getMoEmail().equals(currentUser.getEmail())) {
                mine.add(job);
            }
        }
        return mine;
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Top panel with title and job selector
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel title = UIHelper.createTitle("View Applicants");
        topPanel.add(title, BorderLayout.NORTH);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(Color.WHITE);
        selectorPanel.add(new JLabel("Select Position:"));

        jobCombo = new JComboBox<>();
        jobCombo.addItem("All Positions");
        for (Job job : myJobs) {
            jobCombo.addItem(job);
        }
        jobCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Job) {
                    Job job = (Job) value;
                    value = job.getModuleCode() + " - " + job.getModuleName() + " (" + job.getStatus() + ")";
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        jobCombo.setPreferredSize(new Dimension(350, 35));
        jobCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        selectorPanel.add(jobCombo);

        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> refreshContent());
        selectorPanel.add(refreshBtn);

        topPanel.add(selectorPanel, BorderLayout.SOUTH);
        card.add(topPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        card.add(contentPanel, BorderLayout.CENTER);

        refreshContent();

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void refreshContent() {
        contentPanel.removeAll();
        Object selected = jobCombo.getSelectedItem();
        List<Application> appsToShow = new ArrayList<>();

        if (selected instanceof String && "All Positions".equals(selected)) {
            for (Job job : myJobs) {
                for (Application app : allApps) {
                    if (app.getJobId().equals(job.getJobId())) {
                        appsToShow.add(app);
                    }
                }
            }
        } else if (selected instanceof Job) {
            Job job = (Job) selected;
            for (Application app : allApps) {
                if (app.getJobId().equals(job.getJobId())) {
                    appsToShow.add(app);
                }
            }
        }

        if (appsToShow.isEmpty()) {
            JLabel empty = new JLabel("No applicants for this selection.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            empty.setForeground(Color.GRAY);
            contentPanel.add(empty, BorderLayout.CENTER);
        } else {
            JPanel tableContainer = createApplicantTable(appsToShow, selected);
            JScrollPane scrollPane = new JScrollPane(tableContainer);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            contentPanel.add(scrollPane, BorderLayout.CENTER);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createApplicantTable(List<Application> apps, Object selected) {
        boolean isAll = selected instanceof String && "All Positions".equals(selected);
        int cols = isAll ? 8 : 7;
        String[] headers = isAll ?
                new String[]{"Apply Date", "Name", "Student ID", "Major", "Position", "Status", "Action", "Profile"} :
                new String[]{"Apply Date", "Name", "Student ID", "Major", "Status", "Action", "Profile"};

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

        // Header
        JPanel header = new JPanel(new GridLayout(1, cols, 5, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        for (String h : headers) {
            JLabel lbl = new JLabel(h, SwingConstants.CENTER);
            lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            lbl.setForeground(UIHelper.PRIMARY_COLOR);
            header.add(lbl);
        }
        container.add(header);

        for (Application app : apps) {
            Job job = findJob(app.getJobId());
            Profile profile = findProfile(app.getTaEmail());
            User taUser = findUser(app.getTaEmail());

            JPanel row = new JPanel(new GridLayout(1, cols, 5, 0));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));

            row.add(createCellLabel(app.getApplyTime()));
            row.add(createCellLabel(taUser != null ? taUser.getName() : app.getTaEmail()));
            row.add(createCellLabel(profile != null ? profile.getStudentId() : "N/A"));
            row.add(createCellLabel(profile != null ? profile.getMajor() : "N/A"));

            if (isAll && job != null) {
                row.add(createCellLabel(job.getModuleCode() + " - " + job.getModuleName()));
            }

            JLabel statusLabel = createCellLabel(app.getStatus());
            switch (app.getStatus()) {
                case "ACCEPTED":
                    statusLabel.setForeground(UIHelper.SUCCESS_COLOR);
                    break;
                case "REJECTED":
                    statusLabel.setForeground(new Color(244, 67, 54));
                    break;
                default:
                    statusLabel.setForeground(UIHelper.ACCENT_COLOR);
            }
            row.add(statusLabel);

            // Action buttons
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            actionPanel.setBackground(Color.WHITE);
            JButton acceptBtn = new JButton("Accept");
            styleMiniButton(acceptBtn, UIHelper.SUCCESS_COLOR);
            JButton rejectBtn = new JButton("Reject");
            styleMiniButton(rejectBtn, new Color(244, 67, 54));

            if (!"PENDING".equals(app.getStatus())) {
                acceptBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                acceptBtn.setBackground(Color.GRAY);
                rejectBtn.setBackground(Color.GRAY);
            } else {
                acceptBtn.addActionListener(e -> updateStatus(app, "ACCEPTED", job));
                rejectBtn.addActionListener(e -> updateStatus(app, "REJECTED", job));
            }

            actionPanel.add(acceptBtn);
            actionPanel.add(rejectBtn);
            row.add(actionPanel);

            // Profile button
            JButton profileBtn = new JButton("View Profile");
            styleMiniButton(profileBtn, UIHelper.PRIMARY_COLOR);
            profileBtn.addActionListener(e -> viewProfile(profile, taUser));
            JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            profilePanel.setBackground(Color.WHITE);
            profilePanel.add(profileBtn);
            row.add(profilePanel);

            container.add(row);
        }
        return container;
    }

    private JLabel createCellLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        return label;
    }

    private void styleMiniButton(JButton btn, Color bg) {
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(60, 25));
    }

    private Job findJob(String jobId) {
        for (Job job : myJobs) {
            if (job.getJobId().equals(jobId)) return job;
        }
        return null;
    }

    private Profile findProfile(String email) {
        for (Profile p : allProfiles) {
            if (p.getEmail().equalsIgnoreCase(email)) return p;
        }
        return null;
    }

    private User findUser(String email) {
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    private void updateStatus(Application app, String newStatus, Job job) {
        List<Application> apps = FileUtil.loadApplications();
        for (Application a : apps) {
            if (a.getApplicationId().equals(app.getApplicationId())) {
                a.setStatus(newStatus);
                break;
            }
        }
        FileUtil.saveApplications(apps);

        if ("ACCEPTED".equals(newStatus) && job != null) {
            int accepted = countAccepted(apps, job.getJobId());
            if (accepted >= job.getApplicantLimit()) {
                List<Job> jobs = FileUtil.loadJobs();
                for (Job j : jobs) {
                    if (j.getJobId().equals(job.getJobId())) {
                        j.setStatus("CLOSED");
                        break;
                    }
                }
                FileUtil.saveJobs(jobs);
                UIHelper.showInfoDialog(this, "Position has reached limit and is now closed.", "Position Full");
            }
        }

        allApps = FileUtil.loadApplications();
        refreshContent();
        UIHelper.showInfoDialog(this, "Application status updated.", "Success");
    }

    private int countAccepted(List<Application> apps, String jobId) {
        int count = 0;
        for (Application a : apps) {
            if (a.getJobId().equals(jobId) && "ACCEPTED".equals(a.getStatus())) count++;
        }
        return count;
    }

    private void viewProfile(Profile profile, User user) {
        if (profile == null) {
            UIHelper.showInfoDialog(this, "This TA has not created a profile yet.", "No Profile");
            return;
        }
        new TAProfileViewFrame(user);
    }
}
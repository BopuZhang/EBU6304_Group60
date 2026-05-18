package system.mo;

import system.*;
import system.ta.TAProfileViewFrame;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Frame for Module Organizers to view and manage applicants for their job positions.
 * <p>
 * This frame displays a list of applicants for each job posted by the MO,
 * allowing them to accept or reject applications.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class MOViewApplicantsFrame extends JFrame {

    /** Background color for alternating rows */
    private static final Color ROW_ALT = new Color(250, 250, 250);

    /** Color for reject button */
    private static final Color REJECT_COLOR = UIHelper.DANGER_COLOR;

    /** The currently logged-in user (Module Organizer) */
    private final User currentUser;
    private List<Job> myJobs;
    private List<Application> allApps;
    private List<Profile> allProfiles;
    private List<User> allUsers;
    private JComboBox<Object> jobCombo;
    private JPanel contentPanel;

    public MOViewApplicantsFrame(User user) {
        this.currentUser = user;
        loadDataFromDisk();

        setTitle("View Applicants");
        setSize(1400, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        renderApplicantList();
        setVisible(true);
    }

    private void loadDataFromDisk() {
        allApps = FileUtil.loadApplications();
        allProfiles = FileUtil.loadProfiles();
        allUsers = FileUtil.loadUsers();
        myJobs = loadMyJobs();
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

    private void rebuildJobComboPreservingSelection() {
        String selectedJobId = null;
        boolean allSelected = true;
        Object sel = jobCombo.getSelectedItem();
        if (sel instanceof Job) {
            allSelected = false;
            selectedJobId = ((Job) sel).getJobId();
        }

        jobCombo.removeAllItems();
        jobCombo.addItem("All Positions");
        for (Job job : myJobs) {
            jobCombo.addItem(job);
        }

        if (allSelected) {
            jobCombo.setSelectedIndex(0);
        } else if (selectedJobId != null) {
            for (int i = 0; i < jobCombo.getItemCount(); i++) {
                Object o = jobCombo.getItemAt(i);
                if (o instanceof Job && ((Job) o).getJobId().equals(selectedJobId)) {
                    jobCombo.setSelectedIndex(i);
                    return;
                }
            }
            jobCombo.setSelectedIndex(0);
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel title = UIHelper.createTitle("View Applicants");
        topPanel.add(title, BorderLayout.NORTH);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
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
        jobCombo.setPreferredSize(new Dimension(380, 35));
        jobCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        jobCombo.addActionListener(e -> renderApplicantList());
        selectorPanel.add(jobCombo);

        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> {
            loadDataFromDisk();
            rebuildJobComboPreservingSelection();
            renderApplicantList();
        });
        selectorPanel.add(refreshBtn);

        topPanel.add(selectorPanel, BorderLayout.SOUTH);
        card.add(topPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        card.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void renderApplicantList() {
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

        appsToShow.sort(Comparator
                .comparing((Application a) -> !"PENDING".equals(a.getStatus()))
                .thenComparing((a, b) -> b.getApplyTime().compareTo(a.getApplyTime())));

        if (myJobs.isEmpty()) {
            JLabel empty = new JLabel("You have not posted any positions yet.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            empty.setForeground(Color.GRAY);
            contentPanel.add(empty, BorderLayout.CENTER);
        } else if (appsToShow.isEmpty()) {
            JLabel empty = new JLabel("No applicants for this selection.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            empty.setForeground(Color.GRAY);
            contentPanel.add(empty, BorderLayout.CENTER);
        } else {
            JLabel countHint = new JLabel(appsToShow.size() + " applicant(s)", SwingConstants.LEFT);
            countHint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            countHint.setForeground(new Color(100, 100, 110));
            countHint.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

            JPanel tableContainer = createApplicantTable(appsToShow, selected);
            JScrollPane scrollPane = UIHelper.createScrollPane(tableContainer);
            scrollPane.getViewport().setBackground(Color.WHITE);

            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(Color.WHITE);
            wrap.add(countHint, BorderLayout.NORTH);
            wrap.add(scrollPane, BorderLayout.CENTER);
            contentPanel.add(wrap, BorderLayout.CENTER);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createApplicantTable(List<Application> apps, Object selected) {
        boolean isAll = selected instanceof String && "All Positions".equals(selected);
        int cols = isAll ? 11 : 10;
        String[] headers = isAll
                ? new String[] { "Apply Date", "Name", "Student ID", "Major", "Grade", "Position", "Match", "Status", "Decision",
                        "Profile", "CV" }
                : new String[] { "Apply Date", "Name", "Student ID", "Major", "Grade", "Match", "Status", "Decision", "Profile",
                        "CV" };

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);

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

        int rowIndex = 0;
        for (Application app : apps) {
            Job job = findJob(app.getJobId());
            Profile profile = findProfile(app.getTaEmail());
            User taUser = findUser(app.getTaEmail());
            boolean rowAlt = rowIndex % 2 == 1;
            Color rowBg = rowAlt ? ROW_ALT : Color.WHITE;

            JPanel row = new JPanel(new GridLayout(1, cols, 5, 0));
            row.setBackground(rowBg);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

            row.add(createCellLabel(app.getApplyTime(), rowBg));
            row.add(createCellLabel(taUser != null ? taUser.getName() : app.getTaEmail(), rowBg));
            row.add(createCellLabel(profile != null ? profile.getStudentId() : "N/A", rowBg));
            row.add(createCellLabel(profile != null ? profile.getMajor() : "N/A", rowBg));
            row.add(createCellLabel(profile != null ? profile.getGrade() : "N/A", rowBg));

            if (isAll && job != null) {
                row.add(createCellLabel(job.getModuleCode() + " - " + job.getModuleName(), rowBg));
            }

            List<String> taSkills = (profile != null) ? profile.getSkills() : null;
            List<String> jobSkills = (job != null) ? job.getSkills() : null;
            int matchPercent = UIHelper.calculateSkillMatch(taSkills, jobSkills);
            JLabel matchLabel = UIHelper.createMatchLabel(matchPercent);
            matchLabel.setHorizontalAlignment(SwingConstants.CENTER);
            matchLabel.setOpaque(true);
            matchLabel.setBackground(rowBg);
            row.add(matchLabel);

            JLabel statusLabel = createCellLabel(app.getStatus(), rowBg);
            switch (app.getStatus()) {
                case "ACCEPTED":
                    statusLabel.setForeground(UIHelper.SUCCESS_COLOR);
                    break;
                case "REJECTED":
                    statusLabel.setForeground(REJECT_COLOR);
                    break;
                default:
                    statusLabel.setForeground(UIHelper.ACCENT_COLOR);
            }
            row.add(statusLabel);

            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            actionPanel.setBackground(rowBg);
            JButton acceptBtn = new JButton("Accept");
            styleMiniButton(acceptBtn, UIHelper.SUCCESS_COLOR);
            JButton rejectBtn = new JButton("Reject");
            styleMiniButton(rejectBtn, REJECT_COLOR);

            boolean jobClosed = job != null && "CLOSED".equals(job.getStatus());
            boolean canDecide = "PENDING".equals(app.getStatus()) && !jobClosed;

            if (!canDecide) {
                acceptBtn.setEnabled(false);
                rejectBtn.setEnabled(false);
                acceptBtn.setBackground(UIHelper.DISABLED_COLOR);
                rejectBtn.setBackground(UIHelper.DISABLED_COLOR);
            } else {
                acceptBtn.addActionListener(e -> updateStatus(app, "ACCEPTED", job));
                rejectBtn.addActionListener(e -> confirmReject(app, job));
            }

            actionPanel.add(acceptBtn);
            actionPanel.add(rejectBtn);
            row.add(actionPanel);

            JButton profileBtn = new JButton("View");
            styleMiniButton(profileBtn, UIHelper.PRIMARY_COLOR);
            profileBtn.addActionListener(e -> viewProfile(profile, taUser));
            JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            profilePanel.setBackground(rowBg);
            profilePanel.add(profileBtn);
            row.add(profilePanel);

            JPanel cvPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            cvPanel.setBackground(rowBg);
            if (profile != null && profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
                JButton cvBtn = new JButton("Save CV");
                styleMiniButton(cvBtn, UIHelper.SECONDARY_COLOR);
                cvBtn.setPreferredSize(new Dimension(72, 25));
                cvBtn.addActionListener(e -> saveCvToDisk(profile));
                cvPanel.add(cvBtn);
            } else {
                cvPanel.add(createCellLabel("—", rowBg));
            }
            row.add(cvPanel);

            container.add(row);
            rowIndex++;
        }
        return container;
    }

    private JLabel createCellLabel(String text, Color background) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        label.setOpaque(true);
        label.setBackground(background);
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
        btn.setPreferredSize(new Dimension(66, 25));
    }

    private Job findJob(String jobId) {
        for (Job j : myJobs) {
            if (j.getJobId().equals(jobId)) {
                return j;
            }
        }
        List<Job> all = FileUtil.loadJobs();
        for (Job j : all) {
            if (j.getJobId().equals(jobId)) {
                return j;
            }
        }
        return null;
    }

    private Profile findProfile(String email) {
        for (Profile p : allProfiles) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }

    private User findUser(String email) {
        for (User u : allUsers) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    private void confirmReject(Application app, Job job) {
        int r = UIHelper.showConfirmDialog(this,
                "Reject this applicant? They will see the status as Rejected.",
                "Confirm Reject",
                JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            updateStatus(app, "REJECTED", job);
        }
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

        if (job != null) {
            String notificationTitle = "Application Status Update";
            String notificationContent = "Your application for " + job.getModuleCode() + " - " + job.getModuleName() +
                    " has been " + newStatus + ".";
            FileUtil.sendNotification(app.getTaEmail(), notificationTitle, notificationContent, "STATUS_UPDATE");
        }

        boolean closedByLimit = false;
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
                closedByLimit = true;
            }
        }

        loadDataFromDisk();
        rebuildJobComboPreservingSelection();
        renderApplicantList();

        if (closedByLimit) {
            UIHelper.showInfoDialog(this,
                    "Application accepted. This position has reached its TA limit and applications are now closed.",
                    "Success");
        } else {
            UIHelper.showInfoDialog(this, "Application status updated.", "Success");
        }
    }

    private int countAccepted(List<Application> apps, String jobId) {
        int count = 0;
        for (Application a : apps) {
            if (a.getJobId().equals(jobId) && "ACCEPTED".equals(a.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private void viewProfile(Profile profile, User user) {
        if (user == null) {
            UIHelper.showInfoDialog(this, "Unable to load this applicant's account.", "Error");
            return;
        }
        if (profile == null) {
            UIHelper.showInfoDialog(this, "This TA has not created a profile yet.", "No Profile");
            return;
        }
        String title = user.getName() + " — TA Profile";
        new TAProfileViewFrame(user, title);
    }

    private void saveCvToDisk(Profile profile) {
        if (profile == null || profile.getCvPath() == null || profile.getCvPath().isEmpty()) {
            UIHelper.showWarningDialog(this, "No CV file for this applicant.", "No CV");
            return;
        }
        File src = new File(profile.getCvPath());
        if (!src.exists()) {
            UIHelper.showErrorDialog(this, "CV file not found on disk.", "Error");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(src.getName()));
        int option = chooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            File dest = chooser.getSelectedFile();
            try {
                Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                UIHelper.showInfoDialog(this, "CV saved to:\n" + dest.getAbsolutePath(), "Success");
            } catch (IOException ex) {
                UIHelper.showErrorDialog(this, "Could not save CV: " + ex.getMessage(), "Error");
            }
        }
    }
}

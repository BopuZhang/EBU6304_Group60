package system.mo;

import system.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Frame for Module Organizers to edit an existing job position.
 * <p>
 * This frame allows MOs to modify job details including module information,
 * workload hours, deadline, and required skills.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class MOEditJobFrame extends JFrame {

    /** The currently logged-in user (Module Organizer) */
    private final User currentUser;

    /** The job being edited */
    private final Job job;

    private JTextField skillInputField;
    private JPanel skillTagsPanel;
    private List<String> skills = new ArrayList<>();

    public MOEditJobFrame(User user, Job job) {
        this.currentUser = user;
        this.job = job;

        if (job.getSkills() != null) {
            skills = new ArrayList<>(job.getSkills());
        }

        setTitle("Edit Position: " + job.getModuleCode());
        setSize(650, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = UIHelper.createTitle("Edit Position: " + job.getModuleCode());
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Job ID:"), gbc);
        JLabel jobIdLabel = new JLabel(job.getJobId());
        jobIdLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        jobIdLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(jobIdLabel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Module Code:"), gbc);
        JLabel moduleCodeLabel = new JLabel(job.getModuleCode());
        moduleCodeLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        moduleCodeLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(moduleCodeLabel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Module Name:"), gbc);
        JLabel moduleNameLabel = new JLabel(job.getModuleName());
        moduleNameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        moduleNameLabel.setForeground(Color.GRAY);
        gbc.gridx = 1;
        formPanel.add(moduleNameLabel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        JTextArea descArea = new JTextArea(job.getDescription(), 3, 25);
        descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 70));
        gbc.gridx = 1;
        formPanel.add(descScroll, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Weekly Hours (1-20):"), gbc);
        JSpinner hoursSpinner = new JSpinner(new SpinnerNumberModel(job.getWeeklyHours(), 1, 20, 1));
        hoursSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        hoursSpinner.setPreferredSize(new Dimension(80, 35));
        gbc.gridx = 1;
        formPanel.add(hoursSpinner, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Applicant Limit:"), gbc);
        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        limitPanel.setBackground(Color.WHITE);
        JSpinner limitSpinner = new JSpinner(new SpinnerNumberModel(job.getApplicantLimit(), 1, 50, 1));
        limitSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        limitSpinner.setPreferredSize(new Dimension(80, 35));
        limitPanel.add(limitSpinner);
        limitPanel.add(new JLabel("(Number of TAs to hire)"));
        gbc.gridx = 1;
        formPanel.add(limitPanel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Deadline (YYYY-MM-DD):"), gbc);
        JTextField deadlineField = UIHelper.createTextField();
        deadlineField.setText(job.getDeadline());
        deadlineField.setPreferredSize(new Dimension(150, 35));
        gbc.gridx = 1;
        formPanel.add(deadlineField, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        formPanel.add(new JLabel("Requirements:"), gbc);
        JTextArea reqArea = new JTextArea(job.getRequirements(), 3, 25);
        reqArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        reqArea.setLineWrap(true);
        reqArea.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(reqArea);
        reqScroll.setPreferredSize(new Dimension(300, 70));
        gbc.gridx = 1;
        formPanel.add(reqScroll, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel skillLabel = new JLabel("Required Skills:");
        skillLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        formPanel.add(skillLabel, gbc);

        JPanel skillInputPanel = new JPanel(new BorderLayout(5, 0));
        skillInputPanel.setBackground(Color.WHITE);
        skillInputField = new JTextField();
        skillInputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        skillInputField.setPreferredSize(new Dimension(240, 35));
        skillInputField.setToolTipText("Type a skill and press Enter or click Add");
        JButton addSkillBtn = new JButton("Add");
        addSkillBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        addSkillBtn.setBackground(UIHelper.PRIMARY_COLOR);
        addSkillBtn.setForeground(Color.WHITE);
        addSkillBtn.setFocusPainted(false);
        addSkillBtn.setBorderPainted(false);
        addSkillBtn.setOpaque(true);
        addSkillBtn.setPreferredSize(new Dimension(55, 35));
        addSkillBtn.addActionListener(e -> addSkill());
        skillInputField.addActionListener(e -> addSkill());
        skillInputPanel.add(skillInputField, BorderLayout.CENTER);
        skillInputPanel.add(addSkillBtn, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.insets = new Insets(8, 8, 4, 8);
        formPanel.add(skillInputPanel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 1;
        skillTagsPanel = new JPanel();
        skillTagsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 4));
        skillTagsPanel.setBackground(Color.WHITE);
        gbc.insets = new Insets(2, 8, 8, 8);
        formPanel.add(skillTagsPanel, gbc);
        gbc.insets = new Insets(8, 8, 8, 8);
        row++;

        card.add(formPanel);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        List<Application> allApps = FileUtil.loadApplications();
        long applicantCount = allApps.stream().filter(a -> a.getJobId().equals(job.getJobId())).count();
        if (applicantCount > 0) {
            JPanel warning = new JPanel();
            warning.setBackground(new Color(255, 243, 224));
            warning.setBorder(BorderFactory.createLineBorder(UIHelper.ACCENT_COLOR));
            warning.setAlignmentX(Component.LEFT_ALIGNMENT);
            warning.add(new JLabel("Warning: This position has applicants. Modifying may affect applications."));
            card.add(warning);
            card.add(Box.createRigidArea(new Dimension(0, 20)));
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
                UIHelper.showWarningDialog(this, "Please fill all required fields.", "Warning");
                return;
            }
            if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
                UIHelper.showWarningDialog(this, "Deadline must be YYYY-MM-DD.", "Warning");
                return;
            }

            int accepted = (int) allApps.stream()
                    .filter(a -> a.getJobId().equals(job.getJobId()) && "ACCEPTED".equals(a.getStatus()))
                    .count();
            if (limit < accepted) {
                int confirm = UIHelper.showConfirmDialog(this,
                        "Reducing limit below accepted count (" + accepted + "). Continue?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
            }

            List<Job> jobs = FileUtil.loadJobs();
            for (int i = 0; i < jobs.size(); i++) {
                Job j = jobs.get(i);
                if (j.getJobId().equals(job.getJobId())) {
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
                            limit,
                            skills
                    );
                    jobs.set(i, updatedJob);
                    break;
                }
            }
            FileUtil.saveJobs(jobs);

            LoggerUtil.logUpdate("Job", job.getJobId());
            UIHelper.showInfoDialog(this, "Position updated.", "Success");
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        card.add(buttonPanel);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);

        refreshSkillTags();
    }

    private void addSkill() {
        String skill = skillInputField.getText().trim();
        if (skill.isEmpty()) return;
        for (String s : skills) {
            if (s.equalsIgnoreCase(skill)) {
                skillInputField.setText("");
                return;
            }
        }
        skills.add(skill);
        refreshSkillTags();
        skillInputField.setText("");
    }

    private void removeSkill(String skill) {
        skills.remove(skill);
        refreshSkillTags();
    }

    private void refreshSkillTags() {
        skillTagsPanel.removeAll();
        for (String skill : skills) {
            JPanel tagPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            tagPanel.setBackground(Color.WHITE);
            JLabel tag = UIHelper.createSkillTag(skill);
            JButton removeBtn = new JButton("×");
            removeBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            removeBtn.setForeground(UIHelper.DANGER_COLOR);
            removeBtn.setBackground(new Color(238, 236, 255));
            removeBtn.setBorderPainted(false);
            removeBtn.setFocusPainted(false);
            removeBtn.setOpaque(true);
            removeBtn.setBorder(BorderFactory.createEmptyBorder(3, 4, 3, 4));
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            String skillToRemove = skill;
            removeBtn.addActionListener(e -> removeSkill(skillToRemove));
            tagPanel.add(tag);
            tagPanel.add(removeBtn);
            skillTagsPanel.add(tagPanel);
        }
        skillTagsPanel.revalidate();
        skillTagsPanel.repaint();
    }
}

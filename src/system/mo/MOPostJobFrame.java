package system.mo;

import system.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MOPostJobFrame extends JFrame {
    private final User currentUser;

    private JTextField moduleCodeField;
    private JTextField moduleNameField;
    private JTextArea descArea;
    private JSpinner hoursSpinner;
    private JSpinner limitSpinner;
    private JTextField deadlineField;
    private JTextArea reqArea;
    private JTextField skillInputField;
    private JPanel skillTagsPanel;
    private List<String> skills = new ArrayList<>();

    private JLabel moduleCodeErrorLabel;
    private JLabel moduleNameErrorLabel;
    private JLabel descErrorLabel;
    private JLabel deadlineErrorLabel;

    private JButton submitBtn;

    private boolean moduleCodeValid = false;
    private boolean moduleNameValid = false;
    private boolean descValid = false;
    private boolean deadlineValid = false;

    public MOPostJobFrame(User user) {
        this.currentUser = user;

        setTitle("Post New Position");
        setSize(700, 820);
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
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = UIHelper.createTitle("Post New Position");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel formSection = createFormSection();
        formSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formSection);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitBtn = UIHelper.createButton("Post Position", UIHelper.SUCCESS_COLOR);
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);

        submitBtn.setEnabled(false);
        submitBtn.addActionListener(e -> submitJob());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);
        card.add(buttonPanel);

        JScrollPane scrollPane = UIHelper.createScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        setupValidation();
    }

    private JPanel createFormSection() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel moduleCodeLabel = new JLabel("Module Code:");
        moduleCodeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(moduleCodeLabel, gbc);

        moduleCodeField = UIHelper.createTextField();
        moduleCodeField.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        content.add(moduleCodeField, gbc);
        row++;

        moduleCodeErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(moduleCodeErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel moduleNameLabel = new JLabel("Module Name:");
        moduleNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(moduleNameLabel, gbc);

        moduleNameField = UIHelper.createTextField();
        moduleNameField.setPreferredSize(new Dimension(300, 35));
        gbc.gridx = 1;
        content.add(moduleNameField, gbc);
        row++;

        moduleNameErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(moduleNameErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(descLabel, gbc);

        descArea = new JTextArea(3, 25);
        descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(300, 70));
        gbc.gridx = 1;
        content.add(descScroll, gbc);
        row++;

        descErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(descErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel hoursLabel = new JLabel("Weekly Hours (1-20):");
        hoursLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(hoursLabel, gbc);

        hoursSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        hoursSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        hoursSpinner.setPreferredSize(new Dimension(80, 35));
        gbc.gridx = 1;
        content.add(hoursSpinner, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel limitLabel = new JLabel("Applicant Limit:");
        limitLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(limitLabel, gbc);

        JPanel limitPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        limitPanel.setBackground(Color.WHITE);
        limitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1));
        limitSpinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        limitSpinner.setPreferredSize(new Dimension(80, 35));
        limitPanel.add(limitSpinner);
        limitPanel.add(new JLabel("(Number of TAs to hire)"));
        gbc.gridx = 1;
        content.add(limitPanel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel deadlineLabel = new JLabel("Deadline (YYYY-MM-DD):");
        deadlineLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(deadlineLabel, gbc);

        deadlineField = UIHelper.createTextField();
        deadlineField.setPreferredSize(new Dimension(150, 35));
        deadlineField.setToolTipText("e.g., 2024-12-31");
        gbc.gridx = 1;
        content.add(deadlineField, gbc);
        row++;

        deadlineErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(deadlineErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel reqLabel = new JLabel("Requirements:");
        reqLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(reqLabel, gbc);

        reqArea = new JTextArea(3, 25);
        reqArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        reqArea.setLineWrap(true);
        reqArea.setWrapStyleWord(true);
        JScrollPane reqScroll = new JScrollPane(reqArea);
        reqScroll.setPreferredSize(new Dimension(300, 70));
        gbc.gridx = 1;
        content.add(reqScroll, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel skillLabel = new JLabel("Required Skills:");
        skillLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(skillLabel, gbc);

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
        gbc.insets = new Insets(8, 10, 4, 10);
        content.add(skillInputPanel, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 1;
        skillTagsPanel = new JPanel();
        skillTagsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 4));
        skillTagsPanel.setBackground(Color.WHITE);
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(skillTagsPanel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        outer.add(content);
        return outer;
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

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        label.setForeground(new Color(220, 53, 69));
        label.setPreferredSize(new Dimension(300, 16));
        return label;
    }

    private void setupValidation() {
        moduleCodeField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String text = moduleCodeField.getText().trim();
                if (text.isEmpty()) {
                    moduleCodeErrorLabel.setText("Module code is required");
                    moduleCodeValid = false;
                } else {
                    moduleCodeErrorLabel.setText(" ");
                    moduleCodeValid = true;
                }
                updateSubmitButton();
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });

        moduleNameField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String text = moduleNameField.getText().trim();
                if (text.isEmpty()) {
                    moduleNameErrorLabel.setText("Module name is required");
                    moduleNameValid = false;
                } else {
                    moduleNameErrorLabel.setText(" ");
                    moduleNameValid = true;
                }
                updateSubmitButton();
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });

        descArea.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String text = descArea.getText().trim();
                if (text.isEmpty()) {
                    descErrorLabel.setText("Description is required");
                    descValid = false;
                } else {
                    descErrorLabel.setText(" ");
                    descValid = true;
                }
                updateSubmitButton();
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });

        deadlineField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String text = deadlineField.getText().trim();
                if (text.isEmpty()) {
                    deadlineErrorLabel.setText("Deadline is required");
                    deadlineValid = false;
                } else if (!text.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    deadlineErrorLabel.setText("Format must be YYYY-MM-DD");
                    deadlineValid = false;
                } else {
                    deadlineErrorLabel.setText(" ");
                    deadlineValid = true;
                }
                updateSubmitButton();
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });
    }

    private void updateSubmitButton() {
        submitBtn.setEnabled(moduleCodeValid && moduleNameValid && descValid && deadlineValid);
    }

    private void submitJob() {
        String moduleCode = moduleCodeField.getText().trim();
        String moduleName = moduleNameField.getText().trim();
        String description = descArea.getText().trim();
        int hours = (int) hoursSpinner.getValue();
        int limit = (int) limitSpinner.getValue();
        String deadline = deadlineField.getText().trim();
        String requirements = reqArea.getText().trim();

        if (moduleCode.isEmpty() || moduleName.isEmpty() || description.isEmpty() || deadline.isEmpty()) {
            UIHelper.showWarningDialog(this, "Please fill in all required fields.", "Warning");
            return;
        }
        if (!deadline.matches("\\d{4}-\\d{2}-\\d{2}")) {
            UIHelper.showWarningDialog(this, "Deadline must be in format YYYY-MM-DD.", "Warning");
            return;
        }

        String jobId = "JOB" + System.currentTimeMillis();
        Job newJob = new Job(jobId, currentUser.getEmail(), moduleCode, moduleName,
                description, hours, deadline, requirements, "OPEN", limit, skills);

        List<Job> jobs = FileUtil.loadJobs();
        jobs.add(newJob);
        FileUtil.saveJobs(jobs);

        LoggerUtil.logCreate("Job", jobId + " - " + moduleCode);
        UIHelper.showInfoDialog(this, "Position posted successfully!\n\nJob ID: " + jobId, "Success");
        dispose();
    }
}

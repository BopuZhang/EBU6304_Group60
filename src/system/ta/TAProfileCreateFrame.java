package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class TAProfileCreateFrame extends JFrame {
    private final User currentUser;

    private JTextField studentIdField;
    private JComboBox<String> majorCombo;
    private JComboBox<String> gradeCombo;
    private JTextField phoneField;
    private JTextArea descArea;
    private JTextField skillInputField;
    private JPanel skillTagsPanel;
    private List<String> skills = new ArrayList<>();

    private JLabel studentIdErrorLabel;
    private JLabel phoneErrorLabel;

    private JButton saveBtn;

    private boolean studentIdValid = false;
    private boolean phoneValid = false;

    public TAProfileCreateFrame(User user) {
        this.currentUser = user;

        Profile existing = FileUtil.getProfileByEmail(currentUser.getEmail());
        if (existing != null) {
            UIHelper.showInfoDialog(this,
                    "You already have a profile. Use 'Edit Personal Profile' to update it.",
                    "Profile Exists");
            dispose();
            new TAProfileEditFrame(currentUser);
            return;
        }

        setTitle("Create Personal Profile");
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = UIHelper.createTitle("Create Personal Profile");
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

        saveBtn = UIHelper.createButton("Save Profile", UIHelper.SUCCESS_COLOR);
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);

        saveBtn.setEnabled(false);
        saveBtn.addActionListener(e -> saveProfile());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
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
        JLabel studentIdLabel = new JLabel("Student ID (9 digits):");
        studentIdLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(studentIdLabel, gbc);

        studentIdField = UIHelper.createTextField();
        studentIdField.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        content.add(studentIdField, gbc);
        row++;

        studentIdErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(studentIdErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel majorLabel = new JLabel("Major:");
        majorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(majorLabel, gbc);

        majorCombo = new JComboBox<>(new String[]{
                "Computer Science", "Software Engineering", "Information Technology",
                "Data Science", "Artificial Intelligence", "Other"
        });
        majorCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        majorCombo.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        content.add(majorCombo, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel gradeLabel = new JLabel("Grade/Year:");
        gradeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(gradeLabel, gbc);

        gradeCombo = new JComboBox<>(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate"
        });
        gradeCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gradeCombo.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        content.add(gradeCombo, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(phoneLabel, gbc);

        phoneField = UIHelper.createTextField();
        phoneField.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        content.add(phoneField, gbc);
        row++;

        phoneErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(phoneErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel descLabel = new JLabel("Personal Description:");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(descLabel, gbc);

        descArea = new JTextArea(3, 20);
        descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 70));
        gbc.gridx = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        content.add(descScroll, gbc);
        row++;

        gbc.gridy = row; gbc.gridx = 0;
        JLabel skillLabel = new JLabel("Skills:");
        skillLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(skillLabel, gbc);

        JPanel skillInputPanel = new JPanel(new BorderLayout(5, 0));
        skillInputPanel.setBackground(Color.WHITE);
        skillInputField = new JTextField();
        skillInputField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        skillInputField.setPreferredSize(new Dimension(200, 35));
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
        label.setPreferredSize(new Dimension(250, 16));
        return label;
    }

    private void setupValidation() {
        studentIdField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String id = studentIdField.getText().trim();
                if (id.isEmpty()) {
                    studentIdErrorLabel.setText("Student ID is required");
                    studentIdValid = false;
                } else if (!id.matches("\\d{9}")) {
                    studentIdErrorLabel.setText("Must be exactly 9 digits");
                    studentIdValid = false;
                } else {
                    studentIdErrorLabel.setText(" ");
                    studentIdValid = true;
                }
                updateSaveButton();
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });

        phoneField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String phone = phoneField.getText().trim();
                if (phone.isEmpty()) {
                    phoneErrorLabel.setText("Phone number is required");
                    phoneValid = false;
                } else {
                    phoneErrorLabel.setText(" ");
                    phoneValid = true;
                }
                updateSaveButton();
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });
    }

    private void updateSaveButton() {
        saveBtn.setEnabled(studentIdValid && phoneValid);
    }

    private void saveProfile() {
        String studentId = studentIdField.getText().trim();
        String major = (String) majorCombo.getSelectedItem();
        String grade = (String) gradeCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String description = descArea.getText().trim();

        if (studentId.isEmpty() || phone.isEmpty()) {
            UIHelper.showWarningDialog(this, "Please fill in all required fields.", "Warning");
            return;
        }
        if (!studentId.matches("\\d{9}")) {
            UIHelper.showWarningDialog(this, "Student ID must be exactly 9 digits.", "Warning");
            return;
        }

        Profile newProfile = new Profile(currentUser.getEmail(), studentId, major, grade, phone, "", description, skills);
        List<Profile> profiles = FileUtil.loadProfiles();
        profiles.add(newProfile);
        FileUtil.saveProfiles(profiles);

        LoggerUtil.logCreate("Profile", currentUser.getEmail());
        UIHelper.showInfoDialog(this, "Profile created successfully!", "Success");
        dispose();
        new TAProfileViewFrame(currentUser);
    }
}

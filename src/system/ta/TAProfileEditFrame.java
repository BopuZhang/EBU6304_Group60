package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Frame for TA to edit their existing personal profile.
 * Features real-time validation and pre-filled data.
 */
public class TAProfileEditFrame extends JFrame {
    private final User currentUser;
    private final Profile profile;

    private JLabel studentIdLabel;
    private JComboBox<String> majorCombo;
    private JComboBox<String> gradeCombo;
    private JTextField phoneField;
    private JTextArea descArea;

    private JLabel phoneErrorLabel;
    private JButton saveBtn;

    private boolean phoneValid = true; // Pre-filled with existing phone

    public TAProfileEditFrame(User user) {
        this.currentUser = user;
        this.profile = FileUtil.getProfileByEmail(user.getEmail());

        // Check if profile exists
        if (profile == null) {
            UIHelper.showInfoDialog(this,
                    "No profile found. Please create one first.",
                    "Profile Not Found");
            dispose();
            new TAProfileCreateFrame(currentUser);
            return;
        }

        setTitle("Edit Personal Profile");
        setSize(650, 650);
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

        // Title
        JLabel title = UIHelper.createTitle("Edit Personal Profile");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Form section
        JPanel formSection = createFormSection();
        formSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formSection);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveBtn = UIHelper.createButton("Save Changes", UIHelper.SUCCESS_COLOR);
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);

        saveBtn.addActionListener(e -> saveProfile());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        card.add(buttonPanel);

        // Scroll pane
        JScrollPane scrollPane = UIHelper.createScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);

        // Setup validation after UI is built
        setupValidation();
    }

    /**
     * Creates the form section with pre-filled data and error labels.
     */
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

        // Student ID (read-only)
        gbc.gridy = row; gbc.gridx = 0;
        JLabel studentIdFieldLabel = new JLabel("Student ID:");
        studentIdFieldLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(studentIdFieldLabel, gbc);

        studentIdLabel = new JLabel(profile.getStudentId());
        studentIdLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        studentIdLabel.setForeground(new Color(100, 100, 100));
        gbc.gridx = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        content.add(studentIdLabel, gbc);
        row++;

        // Major
        gbc.gridy = row; gbc.gridx = 0;
        JLabel majorLabel = new JLabel("Major:");
        majorLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(majorLabel, gbc);

        majorCombo = new JComboBox<>(new String[]{
                "Computer Science", "Software Engineering", "Information Technology",
                "Data Science", "Artificial Intelligence", "Other"
        });
        majorCombo.setSelectedItem(profile.getMajor());
        majorCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        majorCombo.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        content.add(majorCombo, gbc);
        row++;

        // Grade / Year
        gbc.gridy = row; gbc.gridx = 0;
        JLabel gradeLabel = new JLabel("Grade/Year:");
        gradeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(gradeLabel, gbc);

        gradeCombo = new JComboBox<>(new String[]{
                "1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate"
        });
        gradeCombo.setSelectedItem(profile.getGrade());
        gradeCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gradeCombo.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        content.add(gradeCombo, gbc);
        row++;

        // Phone
        gbc.gridy = row; gbc.gridx = 0;
        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(phoneLabel, gbc);

        phoneField = UIHelper.createTextField();
        phoneField.setText(profile.getPhone());
        phoneField.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        content.add(phoneField, gbc);
        row++;

        // Phone error label
        phoneErrorLabel = createErrorLabel();
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(phoneErrorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        // Description
        gbc.gridy = row; gbc.gridx = 0;
        JLabel descLabel = new JLabel("Personal Description:");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        content.add(descLabel, gbc);

        descArea = new JTextArea(profile.getDescription() != null ? profile.getDescription() : "", 3, 20);
        descArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(250, 70));
        gbc.gridx = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        content.add(descScroll, gbc);

        outer.add(content);
        return outer;
    }

    /**
     * Creates a label for displaying error messages.
     */
    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        label.setForeground(new Color(220, 53, 69));
        label.setPreferredSize(new Dimension(250, 16));
        return label;
    }

    /**
     * Sets up real-time validation for phone field.
     */
    private void setupValidation() {
        phoneField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String phone = phoneField.getText().trim();
                if (phone.isEmpty()) {
                    phoneErrorLabel.setText("Phone number cannot be empty");
                    phoneValid = false;
                } else {
                    phoneErrorLabel.setText(" ");
                    phoneValid = true;
                }
                saveBtn.setEnabled(phoneValid);
            }
            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        });

        // Initial validation (should pass because field is pre-filled)
        // Trigger validation by simulating a document change or direct call
        // Since DocumentListener doesn't fire on setText, we call manually
        SwingUtilities.invokeLater(() -> {
            String phone = phoneField.getText().trim();
            phoneValid = !phone.isEmpty();
            saveBtn.setEnabled(phoneValid);
            if (!phoneValid) {
                phoneErrorLabel.setText("Phone number cannot be empty");
            }
        });
    }

    /**
     * Saves the updated profile.
     */
    private void saveProfile() {
        String major = (String) majorCombo.getSelectedItem();
        String grade = (String) gradeCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String description = descArea.getText().trim();

        if (phone.isEmpty()) {
            UIHelper.showWarningDialog(this, "Phone number cannot be empty.", "Warning");
            return;
        }

        // Create updated profile (student ID and email unchanged)
        Profile updated = new Profile(
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
                profiles.set(i, updated);
                break;
            }
        }
        FileUtil.saveProfiles(profiles);

        LoggerUtil.logUpdate("Profile", currentUser.getEmail());
        UIHelper.showInfoDialog(this, "Profile updated successfully!", "Success");
        dispose();
        new TAProfileViewFrame(currentUser);
    }
}
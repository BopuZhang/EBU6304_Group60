package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Frame for TA to upload CV (PDF format, max 5MB).
 * Features file validation and real-time feedback.
 */
public class TACVUploadFrame extends JFrame {
    private final User currentUser;
    private Profile profile;

    private JTextField filePathField;
    private JLabel fileInfoLabel;
    private JLabel errorLabel;
    private JButton uploadBtn;

    private File selectedFile;
    private boolean fileValid = false;

    public TACVUploadFrame(User user) {
        this.currentUser = user;
        this.profile = FileUtil.getProfileByEmail(user.getEmail());

        // Check if profile exists
        if (profile == null) {
            UIHelper.showInfoDialog(this,
                    "Please create your personal profile first before uploading CV.",
                    "Profile Required");
            dispose();
            new TAProfileCreateFrame(currentUser);
            return;
        }

        setTitle("Upload CV");
        setSize(550, 400);
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
        JLabel title = UIHelper.createTitle("Upload CV");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Info section
        JPanel infoSection = createInfoSection();
        infoSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(infoSection);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // File selection section
        JPanel fileSection = createFileSection();
        fileSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(fileSection);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        uploadBtn = UIHelper.createButton("Upload CV", UIHelper.SUCCESS_COLOR);
        uploadBtn.setEnabled(false);
        uploadBtn.addActionListener(e -> uploadCV());

        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(uploadBtn);
        buttonPanel.add(cancelBtn);
        card.add(buttonPanel);

        // Scroll pane
        JScrollPane scrollPane = UIHelper.createScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Creates the info section showing current CV status and requirements.
     */
    private JPanel createInfoSection() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);

        // Content panel with light background
        JPanel content = new JPanel();
        content.setBackground(new Color(250, 250, 252));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel requirementLabel = new JLabel("Requirements: PDF format, max 5MB");
        requirementLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        requirementLabel.setForeground(new Color(80, 80, 80));
        requirementLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(requirementLabel);

        content.add(Box.createRigidArea(new Dimension(0, 10)));

        String cvStatus;
        if (profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
            cvStatus = "Current CV: " + new File(profile.getCvPath()).getName();
        } else {
            cvStatus = "No CV uploaded yet.";
        }
        JLabel currentLabel = new JLabel(cvStatus);
        currentLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        currentLabel.setForeground(UIHelper.PRIMARY_COLOR);
        currentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(currentLabel);

        outer.add(content);
        return outer;
    }

    /**
     * Creates the file selection section with browse button and validation feedback.
     */
    private JPanel createFileSection() {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // File path field
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 1;
        filePathField = new JTextField();
        filePathField.setEditable(false);
        filePathField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        filePathField.setPreferredSize(new Dimension(300, 35));
        content.add(filePathField, gbc);

        // Browse button
        gbc.gridx = 1; gbc.weightx = 0;
        JButton browseBtn = UIHelper.createButton("Browse", UIHelper.SECONDARY_COLOR);
        browseBtn.addActionListener(this::browseFile);
        content.add(browseBtn, gbc);
        row++;

        // File info label
        gbc.gridy = row; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 0, 10);
        fileInfoLabel = new JLabel("No file selected");
        fileInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        fileInfoLabel.setForeground(new Color(100, 100, 100));
        content.add(fileInfoLabel, gbc);
        row++;

        // Error label
        gbc.gridy = row;
        gbc.insets = new Insets(2, 10, 5, 10);
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        errorLabel.setForeground(new Color(220, 53, 69));
        content.add(errorLabel, gbc);

        outer.add(content);
        return outer;
    }

    /**
     * Opens a file chooser for selecting a PDF file.
     */
    private void browseFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            validateAndSetFile(file);
        }
    }

    /**
     * Validates the selected file and updates UI accordingly.
     */
    private void validateAndSetFile(File file) {
        // Check file extension
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".pdf")) {
            filePathField.setText("");
            fileInfoLabel.setText("Invalid file type");
            errorLabel.setText("Only PDF files are allowed.");
            selectedFile = null;
            fileValid = false;
            uploadBtn.setEnabled(false);
            return;
        }

        // Check file size (max 5MB)
        long sizeMB = file.length() / (1024 * 1024);
        if (file.length() > 5 * 1024 * 1024) {
            filePathField.setText("");
            fileInfoLabel.setText("File size: " + sizeMB + " MB");
            errorLabel.setText("File size exceeds 5MB limit.");
            selectedFile = null;
            fileValid = false;
            uploadBtn.setEnabled(false);
            return;
        }

        // Valid file
        selectedFile = file;
        filePathField.setText(file.getAbsolutePath());
        fileInfoLabel.setText("File size: " + sizeMB + " MB");
        errorLabel.setText(" ");
        fileValid = true;
        uploadBtn.setEnabled(true);
    }

    /**
     * Uploads the selected CV file and updates the profile.
     */
    private void uploadCV() {
        if (!fileValid || selectedFile == null) {
            UIHelper.showWarningDialog(this, "Please select a valid PDF file.", "Warning");
            return;
        }

        // Create CVs directory if not exists
        File cvDir = new File("cvs");
        if (!cvDir.exists()) {
            cvDir.mkdirs();
        }

        // Generate unique filename
        String originalName = selectedFile.getName();
        String newFileName = currentUser.getEmail().replace("@", "_").replace(".", "_")
                + "_" + System.currentTimeMillis() + ".pdf";
        File destFile = new File(cvDir, newFileName);

        try {
            // Copy file
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Update profile
            List<Profile> profiles = FileUtil.loadProfiles();
            for (Profile p : profiles) {
                if (p.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
                    Profile updated = new Profile(
                            p.getEmail(),
                            p.getStudentId(),
                            p.getMajor(),
                            p.getGrade(),
                            p.getPhone(),
                            destFile.getPath(),
                            p.getDescription()
                    );
                    profiles.set(profiles.indexOf(p), updated);
                    break;
                }
            }
            FileUtil.saveProfiles(profiles);

            LoggerUtil.logUpdate("CV Upload", currentUser.getEmail() + " uploaded CV: " + destFile.getPath());
            UIHelper.showInfoDialog(this, "CV uploaded successfully!", "Success");
            dispose();
            new TAProfileViewFrame(currentUser);

        } catch (Exception ex) {
            LoggerUtil.logError("CV Upload", ex.getMessage());
            UIHelper.showErrorDialog(this, "Upload failed: " + ex.getMessage(), "Error");
        }
    }
}
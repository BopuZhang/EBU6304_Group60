package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.io.File;

/**
 * Frame for TA to view their personal profile.
 * With clearly visible grouping borders and aligned section headers.
 */
public class TAProfileViewFrame extends JFrame {
    private final User currentUser;

    public TAProfileViewFrame(User user) {
        this(user, "My Profile");
    }

    /**
     * @param windowTitle dialog title (e.g. when a Module Organiser views a TA profile)
     */
    public TAProfileViewFrame(User user, String windowTitle) {
        this.currentUser = user;
        setTitle(windowTitle != null && !windowTitle.isEmpty() ? windowTitle : "My Profile");
        setSize(650, 550);
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

        JLabel title = UIHelper.createTitle("My Profile");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        // Account Information Section
        JPanel accountSection = createSectionPanel("Account Information");
        accountSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accountSection);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        // Personal Information Section
        JPanel personalSection = createSectionPanel("Personal Information");
        personalSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(personalSection);
        card.add(Box.createRigidArea(new Dimension(0, 25)));

        Profile profile = FileUtil.getProfileByEmail(currentUser.getEmail());

        populateAccountSection(accountSection);
        populatePersonalSection(personalSection, profile);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (profile != null && profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
            JButton viewCvBtn = UIHelper.createButton("View CV", UIHelper.SECONDARY_COLOR);
            viewCvBtn.addActionListener(e -> viewCV(profile.getCvPath()));
            buttonPanel.add(viewCvBtn);
        }

        JButton closeBtn = UIHelper.createButton("Close", UIHelper.PRIMARY_COLOR);
        closeBtn.addActionListener(e -> dispose());
        buttonPanel.add(closeBtn);

        card.add(buttonPanel);

        JScrollPane scrollPane = UIHelper.createScrollPane(card);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    /**
     * Creates a grouped section panel with a highly visible border and aligned title.
     */
    private JPanel createSectionPanel(String title) {
        JPanel outer = new JPanel();
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBackground(Color.WHITE);
        outer.setOpaque(true);

        // Section title with left alignment matching content padding
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 21, 0, 0)); // Align with content
        outer.add(titleLabel);
        outer.add(Box.createRigidArea(new Dimension(0, 8)));

        // Content panel with white background and DARK GRAY rounded border
        JPanel content = new JPanel();
        content.setBackground(Color.WHITE);                     // Pure white
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        // Use a clearly visible border: Color.GRAY, radius 12, thickness 2
        content.setBorder(new RoundedBorder(Color.GRAY, 12, 2));
        content.setOpaque(true);

        outer.add(content);
        return outer;
    }

    private void populateAccountSection(JPanel section) {
        JPanel content = (JPanel) section.getComponent(2);
        content.setLayout(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        addInfoRow(content, "Name", currentUser.getName());
        addInfoRow(content, "Email", currentUser.getEmail());
        addInfoRow(content, "Role", currentUser.getRole());
    }

    private void populatePersonalSection(JPanel section, Profile profile) {
        JPanel content = (JPanel) section.getComponent(2);
        content.setLayout(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        if (profile == null) {
            JLabel noProfile = new JLabel("No profile created yet.");
            noProfile.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            noProfile.setForeground(Color.GRAY);
            content.add(noProfile);
        } else {
            addInfoRow(content, "Student ID", profile.getStudentId());
            addInfoRow(content, "Major", profile.getMajor());
            addInfoRow(content, "Grade", profile.getGrade());
            addInfoRow(content, "Phone", profile.getPhone());
            if (profile.getDescription() != null && !profile.getDescription().isEmpty()) {
                addInfoRow(content, "Description", profile.getDescription());
            }
            String cvStatus = (profile.getCvPath() != null && !profile.getCvPath().isEmpty())
                    ? "Uploaded" : "Not uploaded";
            addInfoRow(content, "CV", cvStatus);
        }
    }

    private void addInfoRow(JPanel panel, String labelText, String valueText) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 15);
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridy = panel.getComponentCount() / 2;

        JLabel label = new JLabel(labelText + ":");
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(new Color(70, 70, 80));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.weightx = 1;

        JLabel value = new JLabel(valueText);
        value.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        value.setForeground(new Color(40, 40, 50));
        panel.add(value, gbc);
    }

    private void viewCV(String path) {
        try {
            File cvFile = new File(path);
            if (!cvFile.exists()) {
                UIHelper.showErrorDialog(this, "CV file not found.", "Error");
                return;
            }
            Desktop.getDesktop().open(cvFile);
        } catch (Exception ex) {
            UIHelper.showErrorDialog(this, "Cannot open CV: " + ex.getMessage(), "Error");
        }
    }

    /**
     * Simple rounded border with visible color and adjustable thickness.
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            int offset = thickness / 2;
            g2.drawRoundRect(x + offset, y + offset,
                    width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int margin = thickness + 1;
            return new Insets(margin, margin, margin, margin);
        }
    }
}

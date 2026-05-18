package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Frame for Teaching Assistants to edit their profile information.
 * <p>
 * This frame allows TAs to modify their academic details, contact information,
 * skills, and self-description.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class TAProfileEditFrame extends JFrame {

    /** The currently logged-in user */
    private final User currentUser;

    /** The profile being edited */
    private final Profile profile;

    private static final Color WHITE_BG = Color.WHITE;
    private static final Color SOFT_BG = new Color(245, 247, 250);
    private static final Color ACCENT_BLUE = new Color(79, 70, 229);
    private static final Color LIGHT_ACCENT = new Color(238, 242, 255);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MEDIUM = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color DANGER_RED = new Color(239, 68, 68);

    private JLabel studentIdLabel;
    private JComboBox<String> majorCombo;
    private JComboBox<String> gradeCombo;
    private JTextField phoneField;
    private JTextArea descArea;
    private JTextField skillInputField;
    private JPanel skillTagsPanel;
    private List<String> skills = new ArrayList<>();

    private JLabel phoneErrorLabel;
    private JButton saveBtn;

    private boolean phoneValid = true;

    public TAProfileEditFrame(User user) {
        this.currentUser = user;
        this.profile = FileUtil.getProfileByEmail(user.getEmail());

        if (profile == null) {
            UIHelper.showInfoDialog(this,
                    "No profile found. Please create one first.",
                    "Profile Not Found");
            dispose();
            new TAProfileCreateFrame(currentUser);
            return;
        }

        if (profile.getSkills() != null) {
            skills = new ArrayList<>(profile.getSkills());
        }

        setTitle("Edit Personal Profile");
        setSize(680, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(WHITE_BG);
        card.setBorder(new RoundedBorder(BORDER_COLOR, 16, 1));
        card.setOpaque(true);

        JPanel headerPanel = createHeaderPanel();
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(headerPanel);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(241, 245, 249));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel formSection = createFormSection();
        formSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(formSection);

        card.add(Box.createRigidArea(new Dimension(0, 16)));
        JSeparator bottomSep = new JSeparator();
        bottomSep.setForeground(new Color(241, 245, 249));
        bottomSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        bottomSep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bottomSep);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(WHITE_BG);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        saveBtn = UIHelper.createButton("Save Changes", SUCCESS_GREEN);
        JButton cancelBtn = createOutlineButton("Cancel", TEXT_LIGHT);

        saveBtn.addActionListener(e -> saveProfile());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(saveBtn);
        card.add(buttonPanel);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);

        setupValidation();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(WHITE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(234, 138, 56), getWidth(), getHeight(),
                        new Color(210, 115, 40));
                g2.setPaint(gp);
                g2.fill(new Ellipse2D.Double(1, 1, getWidth() - 2, getHeight() - 2));
                g2.setColor(WHITE_BG);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "\u270F";
                int tw = fm.stringWidth(icon);
                g2.drawString(icon, (getWidth() - tw) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(48, 48));
        avatarPanel.setMaximumSize(new Dimension(48, 48));
        avatarPanel.setOpaque(false);
        header.add(avatarPanel, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(WHITE_BG);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JLabel title = new JLabel("Edit Personal Profile");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        JLabel subtitle = new JLabel("Update your profile information for " + currentUser.getName());
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createFormSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(WHITE_BG);
        section.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(WHITE_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;

        int row = 0;

        Font labelFont = new Font(Font.SANS_SERIF, Font.BOLD, 12);
        Font fieldFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        int fieldHeight = 34;
        Dimension fieldDim = new Dimension(280, fieldHeight);

        // --- Student ID (read-only) ---
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(6, 8, 4, 16);
        JLabel sidLabel = new JLabel("Student ID");
        sidLabel.setFont(labelFont);
        sidLabel.setForeground(TEXT_LIGHT);
        content.add(sidLabel, gbc);

        studentIdLabel = new JLabel(profile.getStudentId());
        studentIdLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        studentIdLabel.setForeground(TEXT_MEDIUM);
        gbc.gridx = 1;
        gbc.insets = new Insets(6, 8, 4, 8);
        gbc.weightx = 1;
        content.add(studentIdLabel, gbc);
        gbc.weightx = 0;
        row++;

        // --- Major ---
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 8, 4, 16);
        JLabel majorLabel = new JLabel("Major");
        majorLabel.setFont(labelFont);
        majorLabel.setForeground(TEXT_LIGHT);
        content.add(majorLabel, gbc);

        majorCombo = new JComboBox<>(new String[] {
                "Computer Science", "Software Engineering", "Information Technology",
                "Data Science", "Artificial Intelligence", "Other"
        });
        majorCombo.setSelectedItem(profile.getMajor());
        majorCombo.setFont(fieldFont);
        majorCombo.setPreferredSize(fieldDim);
        majorCombo.setBackground(WHITE_BG);
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 8, 4, 8);
        content.add(majorCombo, gbc);
        row++;

        // --- Grade ---
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 8, 4, 16);
        JLabel gradeLabel = new JLabel("Grade / Year");
        gradeLabel.setFont(labelFont);
        gradeLabel.setForeground(TEXT_LIGHT);
        content.add(gradeLabel, gbc);

        gradeCombo = new JComboBox<>(new String[] {
                "1st Year", "2nd Year", "3rd Year", "4th Year", "Graduate"
        });
        gradeCombo.setSelectedItem(profile.getGrade());
        gradeCombo.setFont(fieldFont);
        gradeCombo.setPreferredSize(fieldDim);
        gradeCombo.setBackground(WHITE_BG);
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 8, 4, 8);
        content.add(gradeCombo, gbc);
        row++;

        // --- Phone ---
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 8, 2, 16);
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(TEXT_LIGHT);
        content.add(phoneLabel, gbc);

        phoneField = new JTextField(profile.getPhone());
        phoneField.setFont(fieldFont);
        phoneField.setPreferredSize(fieldDim);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 8, 2, 8);
        content.add(phoneField, gbc);
        row++;

        // Phone error label
        phoneErrorLabel = new JLabel(" ");
        phoneErrorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        phoneErrorLabel.setForeground(DANGER_RED);
        gbc.gridy = row;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 8, 4, 8);
        content.add(phoneErrorLabel, gbc);
        row++;

        // --- Description ---
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 8, 4, 16);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(labelFont);
        descLabel.setForeground(TEXT_LIGHT);
        content.add(descLabel, gbc);

        descArea = new JTextArea(profile.getDescription() != null ? profile.getDescription() : "", 2, 20);
        descArea.setFont(fieldFont);
        descArea.setForeground(TEXT_DARK);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(280, 55));
        descScroll.setBorder(BorderFactory.createEmptyBorder());
        gbc.gridx = 1;
        gbc.insets = new Insets(10, 8, 4, 8);
        gbc.anchor = GridBagConstraints.WEST;
        content.add(descScroll, gbc);
        gbc.anchor = GridBagConstraints.WEST;
        row++;

        // --- Skills ---
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.insets = new Insets(12, 8, 4, 16);
        JLabel skillLabel = new JLabel("Skills");
        skillLabel.setFont(labelFont);
        skillLabel.setForeground(TEXT_LIGHT);
        content.add(skillLabel, gbc);

        JPanel skillInputPanel = new JPanel(new BorderLayout(6, 0));
        skillInputPanel.setBackground(WHITE_BG);
        skillInputField = new JTextField();
        skillInputField.setFont(fieldFont);
        skillInputField.setPreferredSize(new Dimension(215, fieldHeight));
        skillInputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        skillInputField.setToolTipText("Type a skill and press Enter or click Add");

        JButton addSkillBtn = UIHelper.createButton("Add", ACCENT_BLUE);
        addSkillBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        addSkillBtn.setPreferredSize(new Dimension(55, fieldHeight));
        addSkillBtn.addActionListener(e -> addSkill());
        skillInputField.addActionListener(e -> addSkill());
        skillInputPanel.add(skillInputField, BorderLayout.CENTER);
        skillInputPanel.add(addSkillBtn, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.insets = new Insets(12, 8, 2, 8);
        content.add(skillInputPanel, gbc);
        row++;

        // --- Skill tags ---
        gbc.gridy = row;
        gbc.gridx = 1;
        skillTagsPanel = new JPanel();
        skillTagsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 6, 4));
        skillTagsPanel.setBackground(WHITE_BG);

        JScrollPane skillScroll = new JScrollPane(skillTagsPanel);
        skillScroll.setPreferredSize(new Dimension(280, 78));
        skillScroll.setMaximumSize(new Dimension(280, 140));
        skillScroll.setMinimumSize(new Dimension(280, 78));
        skillScroll.setBorder(BorderFactory.createEmptyBorder());
        skillScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        skillScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        skillScroll.getVerticalScrollBar().setUnitIncrement(12);
        skillScroll.setViewportBorder(BorderFactory.createEmptyBorder());

        gbc.insets = new Insets(0, 8, 8, 8);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        content.add(skillScroll, gbc);
        row++;

        section.add(content);
        refreshSkillTags();

        return section;
    }

    private void addSkill() {
        String skill = skillInputField.getText().trim();
        if (skill.isEmpty())
            return;
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
            JPanel tagPill = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(LIGHT_ACCENT);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                    g2.setColor(new Color(199, 210, 254));
                    g2.setStroke(new BasicStroke(1f));
                    g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 2, getHeight() - 2, 10, 10));
                    g2.dispose();
                }
            };
            tagPill.setOpaque(false);
            tagPill.setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
            tagPill.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 4));

            JLabel tag = new JLabel(skill);
            tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            tag.setForeground(ACCENT_BLUE);
            tagPill.add(tag);

            JButton removeBtn = new JButton("\u00D7") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (getModel().isRollover()) {
                        g2.setColor(new Color(252, 165, 165));
                        g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                        g2.setColor(Color.WHITE);
                    } else {
                        g2.setColor(ACCENT_BLUE);
                    }
                    g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                    FontMetrics fm = g2.getFontMetrics();
                    String t = "\u00D7";
                    int tw = fm.stringWidth(t);
                    g2.drawString(t, (getWidth() - tw) / 2,
                            (getHeight() + fm.getAscent()) / 2 - 2);
                    g2.dispose();
                }
            };
            removeBtn.setPreferredSize(new Dimension(18, 18));
            removeBtn.setContentAreaFilled(false);
            removeBtn.setBorderPainted(false);
            removeBtn.setFocusPainted(false);
            removeBtn.setBorder(BorderFactory.createEmptyBorder());
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            String skillToRemove = skill;
            removeBtn.addActionListener(e -> removeSkill(skillToRemove));
            tagPill.add(removeBtn);

            skillTagsPanel.add(tagPill);
        }
        skillTagsPanel.setSize(280, Short.MAX_VALUE);
        Dimension pref = skillTagsPanel.getPreferredSize();
        skillTagsPanel.setPreferredSize(new Dimension(280, pref.height));
        skillTagsPanel.revalidate();
        skillTagsPanel.repaint();
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        label.setForeground(DANGER_RED);
        return label;
    }

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

            @Override
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate();
            }
        });

        SwingUtilities.invokeLater(() -> {
            String phone = phoneField.getText().trim();
            phoneValid = !phone.isEmpty();
            saveBtn.setEnabled(phoneValid);
            if (!phoneValid) {
                phoneErrorLabel.setText("Phone number cannot be empty");
            }
        });
    }

    private JButton createOutlineButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);

                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                int textWidth = fm.stringWidth(t);
                int textHeight = fm.getAscent();
                g2.setColor(color);
                g2.drawString(t, (getWidth() - textWidth) / 2,
                        (getHeight() + textHeight) / 2 - 2);
                g2.dispose();
            }
        };
        button.setBackground(WHITE_BG);
        button.setForeground(color);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(248, 250, 252));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(WHITE_BG);
            }
        });
        return button;
    }

    private void saveProfile() {
        String major = (String) majorCombo.getSelectedItem();
        String grade = (String) gradeCombo.getSelectedItem();
        String phone = phoneField.getText().trim();
        String description = descArea.getText().trim();

        if (phone.isEmpty()) {
            UIHelper.showWarningDialog(this, "Phone number cannot be empty.", "Warning");
            return;
        }

        Profile updated = new Profile(
                profile.getEmail(),
                profile.getStudentId(),
                major,
                grade,
                phone,
                profile.getCvPath(),
                description,
                skills);

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

    private static class WrapLayout extends FlowLayout {
        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            return layoutSize(target, false);
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getWidth();
                if (targetWidth == 0)
                    targetWidth = Integer.MAX_VALUE;

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - insets.left - insets.right;
                int x = 0, y = 0, rowHeight = 0, maxRowWidth = 0;

                for (int i = 0; i < target.getComponentCount(); i++) {
                    Component c = target.getComponent(i);
                    if (!c.isVisible())
                        continue;
                    Dimension d = preferred ? c.getPreferredSize() : c.getMinimumSize();

                    if (x == 0 || x + d.width <= maxWidth) {
                        if (x > 0)
                            x += hgap;
                        x += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    } else {
                        y += rowHeight + vgap;
                        maxRowWidth = Math.max(maxRowWidth, x);
                        x = d.width;
                        rowHeight = d.height;
                    }
                }
                y += rowHeight;
                maxRowWidth = Math.max(maxRowWidth, x);

                return new Dimension(maxRowWidth + insets.left + insets.right,
                        y + insets.top + insets.bottom);
            }
        }
    }
}

package system.ta;

import system.*;
import system.ui.WrapLayout;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.List;

public class TAProfileViewFrame extends JFrame {
    private final User currentUser;

    private static final Color SOFT_BG = new Color(245, 247, 250);
    private static final Color ACCENT_BLUE = new Color(79, 70, 229);
    private static final Color LIGHT_ACCENT = new Color(238, 242, 255);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MEDIUM = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_AMBER = new Color(251, 191, 36);
    private static final Color WHITE_BG = Color.WHITE;

    public TAProfileViewFrame(User user) {
        this(user, "My Profile");
    }

    public TAProfileViewFrame(User user, String windowTitle) {
        this.currentUser = user;
        setTitle(windowTitle != null && !windowTitle.isEmpty() ? windowTitle : "My Profile");
        setSize(560, 620);
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

        Profile profile = FileUtil.getProfileByEmail(currentUser.getEmail());

        JPanel headerPanel = createHeaderPanel(profile);
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(headerPanel);

        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(241, 245, 249));
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(separator);
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel accountSection = createInfoSection(
                "\uD83D\uDCCB", "Account Information",
                new String[][] {
                        { "Name", currentUser.getName() },
                        { "Email", currentUser.getEmail() },
                        { "Role", currentUser.getRole() }
                });
        accountSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accountSection);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel personalSection = createPersonalSection(profile);
        personalSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(personalSection);

        card.add(Box.createRigidArea(new Dimension(0, 20)));
        JSeparator bottomSep = new JSeparator();
        bottomSep.setForeground(new Color(241, 245, 249));
        bottomSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        bottomSep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bottomSep);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(WHITE_BG);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (profile != null && profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
            JButton viewCvBtn = createOutlineButton("View CV", ACCENT_BLUE);
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

    private JPanel createHeaderPanel(Profile profile) {
        JPanel header = new JPanel(new BorderLayout(20, 0));
        header.setBackground(WHITE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));

        JPanel avatarPanel = createAvatarPanel();
        header.add(avatarPanel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(WHITE_BG);

        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        nameLabel.setForeground(TEXT_DARK);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 4)));

        JLabel emailLabel = new JLabel(currentUser.getEmail());
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        emailLabel.setForeground(TEXT_MEDIUM);
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        if (profile != null) {
            JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            badgePanel.setBackground(WHITE_BG);
            badgePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

            badgePanel.add(createBadge(profile.getMajor(), new Color(219, 234, 254), new Color(30, 64, 175)));
            badgePanel.add(createBadge(profile.getGrade(), new Color(220, 252, 231), new Color(22, 101, 52)));

            boolean hasCV = profile.getCvPath() != null && !profile.getCvPath().isEmpty();
            badgePanel.add(createStatusBadge("CV " + (hasCV ? "Uploaded" : "Pending"),
                    hasCV ? new Color(220, 252, 231) : new Color(254, 243, 199),
                    hasCV ? new Color(22, 101, 52) : new Color(146, 109, 2)));

            infoPanel.add(badgePanel);
        }

        header.add(infoPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createAvatarPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0,
                        new Color(99, 102, 241), getWidth(), getHeight(),
                        new Color(79, 70, 229));
                g2.setPaint(gradient);
                g2.fill(new Ellipse2D.Double(1, 1, getWidth() - 2, getHeight() - 2));

                String initials = getInitials(currentUser.getName());
                g2.setColor(WHITE_BG);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 26));
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(initials);
                int textHeight = fm.getAscent();
                g2.drawString(initials,
                        (getWidth() - textWidth) / 2,
                        (getHeight() + textHeight) / 2 - 2);

                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(72, 72));
        panel.setMaximumSize(new Dimension(72, 72));
        panel.setMinimumSize(new Dimension(72, 72));
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        return panel;
    }

    private String getInitials(String name) {
        if (name == null || name.trim().isEmpty())
            return "?";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }

    private JPanel createBadge(String text, Color bg, Color fg) {
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        label.setForeground(fg);
        badge.add(label);
        return badge;
    }

    private JPanel createStatusBadge(String text, Color bg, Color fg) {
        JPanel badge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 12, 3, 12));

        JPanel dot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(fg);
                g2.fill(new Ellipse2D.Double(1, 1, 6, 6));
                g2.dispose();
            }
        };
        dot.setPreferredSize(new Dimension(8, 8));
        dot.setOpaque(false);
        badge.add(dot);

        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        label.setForeground(fg);
        badge.add(label);
        return badge;
    }

    private JPanel createInfoSection(String icon, String title, String[][] rows) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(WHITE_BG);
        section.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setBackground(WHITE_BG);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        titleRow.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        titleLabel.setForeground(TEXT_DARK);
        titleRow.add(titleLabel);

        section.add(titleRow);
        section.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(SOFT_BG);
        content.setBorder(new RoundedBorder(new Color(226, 232, 240), 12, 1));
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel innerGrid = new JPanel(new GridBagLayout());
        innerGrid.setBackground(SOFT_BG);
        innerGrid.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        for (String[] row : rows) {
            addInfoRow(innerGrid, row[0], row[1]);
        }

        content.add(innerGrid);
        section.add(content);

        return section;
    }

    private JPanel createPersonalSection(Profile profile) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(WHITE_BG);
        section.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setBackground(WHITE_BG);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLabel = new JLabel("\uD83D\uDC64");
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        titleRow.add(iconLabel);

        JLabel titleLabel = new JLabel("Personal Information");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        titleLabel.setForeground(TEXT_DARK);
        titleRow.add(titleLabel);

        section.add(titleRow);
        section.add(Box.createRigidArea(new Dimension(0, 12)));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(SOFT_BG);
        content.setBorder(new RoundedBorder(new Color(226, 232, 240), 12, 1));
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel innerGrid = new JPanel(new GridBagLayout());
        innerGrid.setBackground(SOFT_BG);
        innerGrid.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        if (profile == null) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(SOFT_BG);
            JLabel emptyLabel = new JLabel("No profile created yet. Go to dashboard to create one.");
            emptyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            emptyLabel.setForeground(TEXT_LIGHT);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyPanel.add(emptyLabel, BorderLayout.CENTER);
            innerGrid.add(emptyPanel);
        } else {
            addInfoRow(innerGrid, "Student ID", profile.getStudentId());
            addInfoRow(innerGrid, "Major", profile.getMajor());
            addInfoRow(innerGrid, "Grade", profile.getGrade());
            addInfoRow(innerGrid, "Phone", profile.getPhone());

            if (profile.getDescription() != null && !profile.getDescription().isEmpty()) {
                addInfoRow(innerGrid, "Description", profile.getDescription());
            }

            List<String> profileSkills = profile.getSkills();
            if (profileSkills != null && !profileSkills.isEmpty()) {
                GridBagConstraints gbcSkill = new GridBagConstraints();
                gbcSkill.gridy = innerGrid.getComponentCount() / 2;
                gbcSkill.gridx = 0;
                gbcSkill.gridwidth = 2;
                gbcSkill.anchor = GridBagConstraints.WEST;
                gbcSkill.insets = new Insets(10, 5, 4, 5);
                gbcSkill.fill = GridBagConstraints.HORIZONTAL;
                gbcSkill.weightx = 1;

                JPanel skillRow = new JPanel(new BorderLayout(10, 0));
                skillRow.setBackground(SOFT_BG);

                JLabel skillLabel = new JLabel("Skills:");
                skillLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
                skillLabel.setForeground(TEXT_MEDIUM);
                skillRow.add(skillLabel, BorderLayout.WEST);

                JPanel skillsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 6, 4));
                skillsPanel.setBackground(SOFT_BG);
                for (String skill : profileSkills) {
                    skillsPanel.add(createModernSkillTag(skill));
                }
                skillRow.add(skillsPanel, BorderLayout.CENTER);
                innerGrid.add(skillRow, gbcSkill);
            }
        }

        content.add(innerGrid);
        section.add(content);

        return section;
    }

    private void addInfoRow(JPanel panel, String labelText, String valueText) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 16);
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridy = panel.getComponentCount() / 2;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setForeground(TEXT_LIGHT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.weightx = 1;

        JLabel value = new JLabel("<html><body style='width:200px'>" + escapeHtml(valueText) + "</body></html>");
        value.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        value.setForeground(TEXT_DARK);
        panel.add(value, gbc);
    }

    private String escapeHtml(String text) {
        if (text == null)
            return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private JLabel createModernSkillTag(String text) {
        JLabel tag = new JLabel(text) {
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
                super.paintComponent(g);
            }
        };
        tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tag.setForeground(ACCENT_BLUE);
        tag.setOpaque(false);
        tag.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        return tag;
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
                button.setBackground(LIGHT_ACCENT);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(WHITE_BG);
            }
        });

        return button;
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

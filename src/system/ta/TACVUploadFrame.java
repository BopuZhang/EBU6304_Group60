package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class TACVUploadFrame extends JFrame {
    private final User currentUser;
    private Profile profile;

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
    private static final Color DROPZONE_BG = new Color(249, 250, 252);
    private static final Color DROPZONE_BORDER = new Color(203, 213, 225);

    private JPanel dropZonePanel;
    private JLabel dropZoneIcon;
    private JLabel dropZoneText;
    private JLabel dropZoneHint;
    private JLabel fileInfoLabel;
    private JLabel errorLabel;
    private JButton uploadBtn;

    private File selectedFile;
    private boolean fileValid = false;

    public TACVUploadFrame(User user) {
        this.currentUser = user;
        this.profile = FileUtil.getProfileByEmail(user.getEmail());

        if (profile == null) {
            UIHelper.showInfoDialog(this,
                    "Please create your personal profile first before uploading CV.",
                    "Profile Required");
            dispose();
            new TAProfileCreateFrame(currentUser);
            return;
        }

        setTitle("Upload CV");
        setSize(640, 540);
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
        card.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel cvStatusSection = createCvStatusSection();
        cvStatusSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(cvStatusSection);
        card.add(Box.createRigidArea(new Dimension(0, 16)));

        JPanel dropZoneSection = createDropZoneSection();
        dropZoneSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(dropZoneSection);

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

        uploadBtn = UIHelper.createButton("Upload CV", UIHelper.SUCCESS_COLOR);
        uploadBtn.setEnabled(false);
        uploadBtn.addActionListener(e -> uploadCV());

        JButton cancelBtn = createOutlineButton("Cancel", TEXT_LIGHT);
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(uploadBtn);
        card.add(buttonPanel);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE_BG);
        header.setBorder(BorderFactory.createEmptyBorder(24, 32, 24, 32));

        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(72, 175, 130),
                        getWidth(), getHeight(), new Color(52, 140, 100));
                g2.setPaint(gp);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.setColor(WHITE_BG);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
                FontMetrics fm = g2.getFontMetrics();
                String icon = "\u2191";
                int tw = fm.stringWidth(icon);
                g2.drawString(icon, (getWidth() - tw) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        iconCircle.setPreferredSize(new Dimension(48, 48));
        iconCircle.setMaximumSize(new Dimension(48, 48));
        iconCircle.setOpaque(false);
        header.add(iconCircle, BorderLayout.WEST);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(WHITE_BG);
        textPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        JLabel title = new JLabel("Upload Your CV");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(title);
        textPanel.add(Box.createRigidArea(new Dimension(0, 2)));
        JLabel subtitle = new JLabel("PDF format only, maximum 5MB file size");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createCvStatusSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(WHITE_BG);
        section.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        String cvFileName = null;
        if (profile.getCvPath() != null && !profile.getCvPath().isEmpty()) {
            cvFileName = new File(profile.getCvPath()).getName();
        }

        JPanel statusBar = new JPanel(new BorderLayout(12, 0));
        statusBar.setBackground(SOFT_BG);
        statusBar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        leftPanel.setBackground(SOFT_BG);

        JLabel statusIcon = new JLabel(cvFileName != null ? "\u2705" : "\u2139\uFE0F");
        statusIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        leftPanel.add(statusIcon);

        JLabel statusText = new JLabel(cvFileName != null
                ? "Current CV: " + cvFileName
                : "No CV uploaded yet. Upload a new CV to replace.");
        statusText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statusText.setForeground(TEXT_MEDIUM);
        leftPanel.add(statusText);

        statusBar.add(leftPanel, BorderLayout.CENTER);

        if (cvFileName != null) {
            JLabel badge = createSmallBadge("Will be replaced", new Color(254, 243, 199), new Color(146, 109, 2));
            statusBar.add(badge, BorderLayout.EAST);
        }

        section.add(statusBar);
        return section;
    }

    private JPanel createDropZoneSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(WHITE_BG);
        section.setBorder(BorderFactory.createEmptyBorder(0, 32, 0, 32));

        dropZonePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DROPZONE_BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                float[] dash = { 8f, 4f };
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, dash, 0f));
                g2.setColor(fileValid ? SUCCESS_GREEN : DROPZONE_BORDER);
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 14, 14));
                g2.dispose();
            }
        };
        dropZonePanel.setLayout(new BoxLayout(dropZonePanel, BoxLayout.Y_AXIS));
        dropZonePanel.setOpaque(false);
        dropZonePanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        dropZonePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        dropZoneIcon = new JLabel("\uD83D\uDCC4");
        dropZoneIcon.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 32));
        dropZoneIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropZonePanel.add(dropZoneIcon);
        dropZonePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        dropZoneText = new JLabel("Click to browse or drag your PDF file here");
        dropZoneText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        dropZoneText.setForeground(TEXT_MEDIUM);
        dropZoneText.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropZonePanel.add(dropZoneText);
        dropZonePanel.add(Box.createRigidArea(new Dimension(0, 4)));

        dropZoneHint = new JLabel("PDF only \u2022 Max 5MB");
        dropZoneHint.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        dropZoneHint.setForeground(TEXT_LIGHT);
        dropZoneHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropZonePanel.add(dropZoneHint);
        dropZonePanel.add(Box.createRigidArea(new Dimension(0, 12)));

        fileInfoLabel = new JLabel(" ");
        fileInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        fileInfoLabel.setForeground(TEXT_MEDIUM);
        fileInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropZonePanel.add(fileInfoLabel);

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        errorLabel.setForeground(DANGER_RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        dropZonePanel.add(errorLabel);

        dropZonePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                browseFile(null);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                dropZonePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        });

        section.add(dropZonePanel);
        return section;
    }

    private void browseFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf"));

        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            validateAndSetFile(file);
        }
    }

    private void validateAndSetFile(File file) {
        String name = file.getName().toLowerCase();
        if (!name.endsWith(".pdf")) {
            resetDropZone();
            errorLabel.setText("Only PDF files are allowed");
            fileInfoLabel.setText(" ");
            dropZoneIcon.setText("\u274C");
            dropZoneText.setText("Invalid file type");
            dropZoneText.setForeground(DANGER_RED);
            selectedFile = null;
            fileValid = false;
            uploadBtn.setEnabled(false);
            dropZonePanel.repaint();
            return;
        }

        long sizeMB = file.length() / (1024 * 1024);
        if (file.length() > 5 * 1024 * 1024) {
            resetDropZone();
            errorLabel.setText("File size exceeds 5MB limit (" + sizeMB + "MB)");
            fileInfoLabel.setText(" ");
            dropZoneIcon.setText("\u274C");
            dropZoneText.setText("File too large");
            dropZoneText.setForeground(DANGER_RED);
            selectedFile = null;
            fileValid = false;
            uploadBtn.setEnabled(false);
            dropZonePanel.repaint();
            return;
        }

        selectedFile = file;
        fileValid = true;
        uploadBtn.setEnabled(true);

        dropZoneIcon.setText("\u2705");
        dropZoneText.setText(file.getName());
        dropZoneText.setForeground(SUCCESS_GREEN);
        dropZoneHint.setText(sizeMB + " MB \u2022 PDF \u2022 Ready to upload");
        dropZoneHint.setForeground(SUCCESS_GREEN);
        fileInfoLabel.setText(" ");
        errorLabel.setText(" ");
        dropZonePanel.repaint();
    }

    private void resetDropZone() {
        dropZoneIcon.setText("\uD83D\uDCC4");
        dropZoneText.setText("Click to browse or drag your PDF file here");
        dropZoneText.setForeground(TEXT_MEDIUM);
        dropZoneHint.setText("PDF only \u2022 Max 5MB");
        dropZoneHint.setForeground(TEXT_LIGHT);
        fileInfoLabel.setText(" ");
        errorLabel.setText(" ");
        dropZonePanel.repaint();
    }

    private JLabel createSmallBadge(String text, Color bg, Color fg) {
        JPanel badgePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badgePanel.setOpaque(false);
        badgePanel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        label.setForeground(fg);
        badgePanel.add(label);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(badgePanel);
        JLabel result = new JLabel();
        result.setLayout(new BorderLayout());
        result.add(wrapper, BorderLayout.CENTER);
        return result;
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

    private void uploadCV() {
        if (!fileValid || selectedFile == null) {
            UIHelper.showWarningDialog(this, "Please select a valid PDF file.", "Warning");
            return;
        }

        File cvDir = new File("cvs");
        if (!cvDir.exists()) {
            cvDir.mkdirs();
        }

        String newFileName = currentUser.getEmail().replace("@", "_").replace(".", "_")
                + "_" + System.currentTimeMillis() + ".pdf";
        File destFile = new File(cvDir, newFileName);

        try {
            Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

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
                            p.getDescription());
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
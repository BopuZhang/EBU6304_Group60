package system.mo;

import system.*;
import system.ui.WrapLayout;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Frame for Module Organizers to post new TA job positions.
 * <p>
 * This frame provides a form for creating a new job position including
 * module details, workload hours, deadline, and required skills.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class MOPostJobFrame extends JFrame {

    /** The currently logged-in user (Module Organizer) */
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
    private JScrollPane skillTagsScrollPane;
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

    private static final Color WHITE_BG = Color.WHITE;
    private static final Color SOFT_BG = new Color(245, 247, 250);
    private static final Color ACCENT_BLUE = new Color(79, 70, 229);
    private static final Color LIGHT_ACCENT = new Color(238, 242, 255);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MEDIUM = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    public MOPostJobFrame(User user) {
        this.currentUser = user;

        setTitle("Post New Position");
        setSize(780, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        card.add(createHeaderPanel(), BorderLayout.NORTH);
        card.add(createMainForm(), BorderLayout.CENTER);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);

        setupValidation();
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(WHITE_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(20, 28, 18, 28)
        ));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leftGroup.setBackground(WHITE_BG);

        JPanel iconDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(16, 185, 129), getWidth(), getHeight(),
                        new Color(5, 150, 105));
                g2.setPaint(gp);
                g2.fillOval(1, 1, getWidth() - 2, getHeight() - 2);
                g2.dispose();
            }
        };
        iconDot.setPreferredSize(new Dimension(38, 38));
        iconDot.setOpaque(false);
        leftGroup.add(iconDot);

        JPanel textGroup = new JPanel();
        textGroup.setLayout(new BoxLayout(textGroup, BoxLayout.Y_AXIS));
        textGroup.setBackground(WHITE_BG);

        JLabel title = new JLabel("Post New Position");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(title);
        textGroup.add(Box.createRigidArea(new Dimension(0, 2)));

        JLabel subtitle = new JLabel("Create a new TA recruitment position with required skills");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(subtitle);

        leftGroup.add(textGroup);
        header.add(leftGroup, BorderLayout.WEST);

        return header;
    }

    private JScrollPane createMainForm() {
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setBackground(WHITE_BG);
        formContainer.setBorder(new EmptyBorder(24, 28, 24, 28));

        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(WHITE_BG);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 0, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        row = addFieldRow(content, gbc, row, "Module Code*", "e.g. COMP2201", moduleCodeField = createTextField(260));
        moduleCodeErrorLabel = addErrorRow(content, gbc, row++);

        row = addFieldRow(content, gbc, row, "Module Name*", "e.g. Software Engineering", moduleNameField = createTextField(300));
        moduleNameErrorLabel = addErrorRow(content, gbc, row++);

        row = addTextAreaRow(content, gbc, row, "Job Description*", 3, descArea = createTextArea());
        descErrorLabel = addErrorRow(content, gbc, row++);

        row = addSpinnerRow(content, gbc, row, "Weekly Hours", 5, 1, 20, hoursSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1)), "hours/week");
        row++;

        row = addSpinnerRow(content, gbc, row, "Applicant Limit", 5, 1, 50, limitSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 50, 1)), "TAs to hire");
        row++;

        row = addFieldRow(content, gbc, row, "Application Deadline*", "e.g. 2025-12-31", deadlineField = createTextField(160));
        deadlineErrorLabel = addErrorRow(content, gbc, row++);

        row = addTextAreaRow(content, gbc, row, "Additional Requirements", 3, reqArea = createTextArea());
        row++;

        row = addSkillSection(content, gbc, row);
        row++;

        formContainer.add(content);
        formContainer.add(Box.createRigidArea(new Dimension(0, 28)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(WHITE_BG);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        submitBtn = createStyledButton("Post Position", SUCCESS_GREEN);
        JButton cancelBtn = createOutlineButton("Cancel");

        submitBtn.setEnabled(false);
        submitBtn.addActionListener(e -> submitJob());
        cancelBtn.addActionListener(e -> dispose());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(submitBtn);
        formContainer.add(buttonPanel);

        JScrollPane scrollPane = new JScrollPane(formContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private int addFieldRow(JPanel content, GridBagConstraints gbc, int row, String labelText, String hint, JTextField field) {
        gbc.gridy = row; gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        content.add(label, gbc);

        gbc.gridx = 1;
        field.setPreferredSize(new Dimension(300, 36));
        content.add(field, gbc);
        if (hint != null && !hint.isEmpty()) {
            field.setToolTipText(hint);
        }
        return row;
    }

    private JLabel addErrorRow(JPanel content, GridBagConstraints gbc, int row) {
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        errorLabel.setForeground(DANGER_RED);
        errorLabel.setPreferredSize(new Dimension(300, 16));
        gbc.gridy = row; gbc.gridx = 1;
        gbc.insets = new Insets(2, 10, 8, 10);
        content.add(errorLabel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        return errorLabel;
    }

    private int addTextAreaRow(JPanel content, GridBagConstraints gbc, int row, String labelText, int rows, JTextArea area) {
        gbc.gridy = row; gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        content.add(label, gbc);

        gbc.gridx = 1;
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(300, 66 + (rows - 3) * 16));
        content.add(scroll, gbc);
        row++;
        return row;
    }

    private int addSpinnerRow(JPanel content, GridBagConstraints gbc, int row, String labelText, int initial, int min, int max, JSpinner spinner, String suffix) {
        gbc.gridy = row; gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        label.setForeground(TEXT_DARK);
        content.add(label, gbc);

        gbc.gridx = 1;
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        wrapper.setBackground(WHITE_BG);
        spinner.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(80, 36));
        wrapper.add(spinner);
        JLabel suffixLbl = new JLabel(suffix);
        suffixLbl.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        suffixLbl.setForeground(TEXT_LIGHT);
        wrapper.add(suffixLbl);
        content.add(wrapper, gbc);
        return row;
    }

    private int addSkillSection(JPanel content, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0;
        JLabel skillLabel = new JLabel("Required Skills:");
        skillLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        skillLabel.setForeground(TEXT_DARK);
        content.add(skillLabel, gbc);

        gbc.gridx = 1;
        JPanel skillInputPanel = new JPanel(new BorderLayout(8, 0));
        skillInputPanel.setBackground(WHITE_BG);
        skillInputField = createTextField(240);
        skillInputField.setToolTipText("Type a skill and press Enter or click Add");
        JButton addSkillBtn = createStyledButton("Add", ACCENT_BLUE);
        addSkillBtn.setPreferredSize(new Dimension(60, 36));
        addSkillBtn.addActionListener(e -> addSkill());
        skillInputField.addActionListener(e -> addSkill());
        skillInputPanel.add(skillInputField, BorderLayout.CENTER);
        skillInputPanel.add(addSkillBtn, BorderLayout.EAST);
        gbc.insets = new Insets(8, 10, 4, 10);
        content.add(skillInputPanel, gbc);
        gbc.insets = new Insets(8, 10, 0, 10);
        row++;

        gbc.gridy = row; gbc.gridx = 1;
        skillTagsPanel = new JPanel();
        skillTagsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 6, 6));
        skillTagsPanel.setBackground(WHITE_BG);
        skillTagsPanel.setOpaque(true);

        skillTagsScrollPane = new JScrollPane(skillTagsPanel);
        skillTagsScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(8, 8, 8, 8)
        ));
        skillTagsScrollPane.setBackground(WHITE_BG);
        skillTagsScrollPane.setPreferredSize(new Dimension(300, 78));
        skillTagsScrollPane.setMinimumSize(new Dimension(300, 50));
        skillTagsScrollPane.setMaximumSize(new Dimension(300, 120));
        skillTagsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        skillTagsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(skillTagsScrollPane, gbc);
        row++;

        return row;
    }

    private JTextField createTextField(int width) {
        JTextField field = new JTextField();
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        field.setBackground(WHITE_BG);
        return field;
    }

    private JTextArea createTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        area.setBackground(WHITE_BG);
        return area;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(110, 36));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createOutlineButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(SOFT_BG);
                } else {
                    g2.setColor(WHITE_BG);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(BORDER_COLOR);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                g2.setColor(TEXT_MEDIUM);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(90, 36));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.repaint();
            }
        });
        return btn;
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
            JPanel pill = new JPanel(new BorderLayout(4, 0));
            pill.setBackground(new Color(238, 242, 255));
            pill.setBorder(new EmptyBorder(4, 8, 4, 4));

            JLabel nameLabel = new JLabel(skill);
            nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            nameLabel.setForeground(ACCENT_BLUE);
            pill.add(nameLabel, BorderLayout.CENTER);

            JLabel closeLabel = new JLabel("×");
            closeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
            closeLabel.setForeground(TEXT_LIGHT);
            closeLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            String skillToRemove = skill;
            closeLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    removeSkill(skillToRemove);
                }
                @Override
                public void mouseEntered(MouseEvent e) {
                    closeLabel.setForeground(DANGER_RED);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    closeLabel.setForeground(TEXT_LIGHT);
                }
            });
            pill.add(closeLabel, BorderLayout.EAST);

            skillTagsPanel.add(pill);
        }
        skillTagsPanel.revalidate();
        skillTagsPanel.repaint();
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
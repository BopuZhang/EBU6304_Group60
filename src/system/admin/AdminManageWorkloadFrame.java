package system.admin;

import system.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

/**
 * Frame for administrators to manage TA workload.
 * <p>
 * This frame displays a table of TAs with their workload hours and overload status.
 * Administrators can view TA details and manage their assignments.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class AdminManageWorkloadFrame extends JFrame {

    /** List of all users in the system */
    private final List<User> users;

    /** List of all TA profiles */
    private final List<Profile> allProfiles;

    /** List of all applications */
    private final List<Application> allApps;

    /** List of all jobs */
    private final List<Job> allJobs;

    /** Main content panel */
    private JPanel mainPanel;

    /**
     * Constructs the frame to manage TA workload.
     *
     * @param users the list of all users
     */
    public AdminManageWorkloadFrame(List<User> users) {
        this.users = users;
        this.allProfiles = FileUtil.loadProfiles();
        this.allApps = FileUtil.loadApplications();
        this.allJobs = FileUtil.loadJobs();

        setTitle("Manage TA Workload");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(UIHelper.BACKGROUND_COLOR);
        outer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        JLabel title = UIHelper.createTitle("Manage TA Workload");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        card.add(title, BorderLayout.NORTH);

        JLabel instruction = new JLabel("Click on a TA to view and manage assigned positions");
        instruction.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        instruction.setForeground(Color.GRAY);
        instruction.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        card.add(instruction, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        card.add(scrollPane, BorderLayout.CENTER);

        outer.add(card, BorderLayout.CENTER);
        add(outer);

        refreshView();
    }

    private void refreshView() {
        mainPanel.removeAll();

        // Header
        JPanel header = new JPanel(new GridLayout(1, 5, 10, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        header.add(createHeaderLabel("TA Name"));
        header.add(createHeaderLabel("Student ID"));
        header.add(createHeaderLabel("Email"));
        header.add(createHeaderLabel("Current Hours"));
        header.add(createHeaderLabel("Status"));
        mainPanel.add(header);

        // Collect TA workload info
        List<TaWorkloadInfo> taList = new ArrayList<>();
        for (User u : users) {
            if ("TA".equals(u.getRole())) {
                TaWorkloadInfo info = new TaWorkloadInfo();
                info.taUser = u;
                info.profile = findProfile(u.getEmail());
                info.totalHours = 0;
                info.assignedJobs = new ArrayList<>();

                for (Application app : allApps) {
                    if (app.getTaEmail().equals(u.getEmail()) && "ACCEPTED".equals(app.getStatus())) {
                        for (Job job : allJobs) {
                            if (job.getJobId().equals(app.getJobId())) {
                                info.totalHours += job.getWeeklyHours();
                                info.assignedJobs.add(job);
                                break;
                            }
                        }
                    }
                }
                taList.add(info);
            }
        }

        taList.sort((a, b) -> b.totalHours - a.totalHours);

        for (TaWorkloadInfo info : taList) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            row.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel infoPanel = new JPanel(new GridLayout(1, 5, 10, 0));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

            String name = info.taUser.getName();
            String studentId = info.profile != null ? info.profile.getStudentId() : "N/A";
            String email = info.taUser.getEmail();
            String hours = info.totalHours + " hrs/week";

            infoPanel.add(createCellLabel(name));
            infoPanel.add(createCellLabel(studentId));
            infoPanel.add(createCellLabel(email));

            JLabel hoursLabel = createCellLabel(hours);
            if (info.totalHours > 15)
                hoursLabel.setForeground(UIHelper.DANGER_COLOR);
            else if (info.totalHours > 10)
                hoursLabel.setForeground(UIHelper.ACCENT_COLOR);
            else if (info.totalHours > 0)
                hoursLabel.setForeground(UIHelper.SUCCESS_COLOR);
            else
                hoursLabel.setForeground(UIHelper.DISABLED_COLOR);
            infoPanel.add(hoursLabel);

            String statusText;
            Color statusColor;
            if (info.totalHours > 15) {
                statusText = "Overloaded";
                statusColor = UIHelper.DANGER_COLOR;
            } else if (info.totalHours > 10) {
                statusText = "Heavy";
                statusColor = UIHelper.ACCENT_COLOR;
            } else if (info.totalHours > 0) {
                statusText = "Normal";
                statusColor = UIHelper.SUCCESS_COLOR;
            } else {
                statusText = "Idle";
                statusColor = UIHelper.DISABLED_COLOR;
            }

            JLabel statusLabel = createCellLabel(statusText);
            statusLabel.setForeground(statusColor);
            infoPanel.add(statusLabel);

            row.add(infoPanel, BorderLayout.CENTER);

            final TaWorkloadInfo finalInfo = info;
            row.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showManageDialog(finalInfo);
                }

                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    row.setBackground(new Color(245, 245, 245));
                    infoPanel.setBackground(new Color(245, 245, 245));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    row.setBackground(Color.WHITE);
                    infoPanel.setBackground(Color.WHITE);
                }
            });

            mainPanel.add(row);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void showManageDialog(TaWorkloadInfo info) {
        JDialog dialog = new JDialog(this, "Workload Details – " + info.taUser.getName(), true);
        dialog.setSize(820, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(UIHelper.BACKGROUND_COLOR);

        Color WHITE_BG = Color.WHITE;
        Color SOFT_BG = new Color(245, 247, 250);
        Color ACCENT_BLUE = new Color(79, 70, 229);
        Color LIGHT_ACCENT = new Color(238, 242, 255);
        Color TEXT_DARK = new Color(30, 41, 59);
        Color TEXT_MEDIUM = new Color(71, 85, 105);
        Color TEXT_LIGHT = new Color(148, 163, 184);
        Color DANGER_RED = new Color(239, 68, 68);
        Color SUCCESS_GREEN = new Color(34, 197, 94);
        Color WARNING_AMBER = new Color(251, 191, 36);
        Color BORDER_COLOR = new Color(226, 232, 240);
        Color HEADER_BG = new Color(248, 250, 252);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(WHITE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));

        card.add(createDialogHeader(info, ACCENT_BLUE, SUCCESS_GREEN, WARNING_AMBER, DANGER_RED, TEXT_DARK, TEXT_LIGHT,
                WHITE_BG), BorderLayout.NORTH);
        card.add(createDialogBody(info, WHITE_BG, SOFT_BG, TEXT_DARK, TEXT_MEDIUM, TEXT_LIGHT, DANGER_RED,
                SUCCESS_GREEN, WARNING_AMBER, BORDER_COLOR, HEADER_BG, ACCENT_BLUE, dialog), BorderLayout.CENTER);

        mainPanel.add(card, BorderLayout.CENTER);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createDialogHeader(TaWorkloadInfo info, Color accentBlue, Color successGreen,
            Color warningAmber, Color dangerRed, Color textDark, Color textLight, Color whiteBg) {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(whiteBg);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(20, 24, 18, 24)));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leftGroup.setBackground(whiteBg);

        JPanel iconDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color iconColor;
                if (info.totalHours > 15)
                    iconColor = dangerRed;
                else if (info.totalHours > 10)
                    iconColor = warningAmber;
                else if (info.totalHours > 0)
                    iconColor = successGreen;
                else
                    iconColor = textLight;
                GradientPaint gp = new GradientPaint(0, 0, iconColor, getWidth(), getHeight(), iconColor.darker());
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
        textGroup.setBackground(whiteBg);

        JLabel title = new JLabel(info.taUser.getName());
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setForeground(textDark);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(title);
        textGroup.add(Box.createRigidArea(new Dimension(0, 2)));

        String statusStr;
        Color statusColor;
        if (info.totalHours > 15) {
            statusStr = "Overloaded";
            statusColor = dangerRed;
        } else if (info.totalHours > 10) {
            statusStr = "Heavy";
            statusColor = warningAmber;
        } else if (info.totalHours > 0) {
            statusStr = "Normal";
            statusColor = successGreen;
        } else {
            statusStr = "Idle";
            statusColor = textLight;
        }
        JLabel subtitle = new JLabel(statusStr + " · " + info.totalHours + " hrs/week total");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(statusColor);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(subtitle);

        leftGroup.add(textGroup);
        header.add(leftGroup, BorderLayout.WEST);

        return header;
    }

    private JScrollPane createDialogBody(TaWorkloadInfo info, Color whiteBg, Color softBg,
            Color textDark, Color textMedium, Color textLight, Color dangerRed,
            Color successGreen, Color warningAmber, Color borderColor,
            Color headerBg, Color accentBlue, JDialog dialog) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(whiteBg);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(softBg);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        infoCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoCard.add(createInfoLine("Email", info.taUser.getEmail(), textMedium, textDark));
        if (info.profile != null) {
            infoCard.add(Box.createRigidArea(new Dimension(0, 6)));
            infoCard.add(createInfoLine("Student ID", info.profile.getStudentId(), textMedium, textDark));
            infoCard.add(Box.createRigidArea(new Dimension(0, 6)));
            infoCard.add(createInfoLine("Major", info.profile.getMajor(), textMedium, textDark));
            infoCard.add(Box.createRigidArea(new Dimension(0, 6)));
            infoCard.add(createInfoLine("Grade", info.profile.getGrade(), textMedium, textDark));
        }
        infoCard.add(Box.createRigidArea(new Dimension(0, 6)));
        infoCard.add(createInfoLine("Total Workload", info.totalHours + " hrs/week",
                textMedium, info.totalHours > 15 ? dangerRed : info.totalHours > 10 ? warningAmber : successGreen));

        body.add(infoCard);
        body.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel sectionHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sectionHeader.setBackground(whiteBg);
        sectionHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel jobsLabel = new JLabel("Assigned Positions");
        jobsLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        jobsLabel.setForeground(textDark);
        sectionHeader.add(jobsLabel);
        body.add(sectionHeader);
        body.add(Box.createRigidArea(new Dimension(0, 10)));

        if (info.assignedJobs.isEmpty()) {
            JPanel emptyCard = new JPanel(new BorderLayout());
            emptyCard.setBackground(softBg);
            emptyCard.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
            emptyCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel empty = new JLabel("No positions assigned.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            empty.setForeground(textLight);
            emptyCard.add(empty, BorderLayout.CENTER);
            body.add(emptyCard);
        } else {
            String[] columns = { "Module", "Hours", "MO Email", "Deadline", "" };
            Object[][] data = new Object[info.assignedJobs.size()][5];
            for (int i = 0; i < info.assignedJobs.size(); i++) {
                Job job = info.assignedJobs.get(i);
                data[i][0] = job.getModuleCode() + " – " + job.getModuleName();
                data[i][1] = job.getWeeklyHours() + " h/w";
                data[i][2] = job.getMoEmail();
                data[i][3] = job.getDeadline();
                data[i][4] = job;
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };

            JTable table = new JTable(tableModel);
            table.setRowHeight(42);
            table.setShowGrid(false);
            table.setIntercellSpacing(new Dimension(0, 0));
            table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
            table.setForeground(textDark);
            table.setSelectionBackground(new Color(238, 242, 255));
            table.setSelectionForeground(accentBlue);
            table.setBackground(whiteBg);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            table.getTableHeader().setBackground(headerBg);
            table.getTableHeader().setForeground(textMedium);
            table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            table.getTableHeader().setPreferredSize(new Dimension(0, 36));
            table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));

            table.getColumnModel().getColumn(0).setPreferredWidth(220);
            table.getColumnModel().getColumn(1).setPreferredWidth(70);
            table.getColumnModel().getColumn(2).setPreferredWidth(180);
            table.getColumnModel().getColumn(3).setPreferredWidth(100);
            table.getColumnModel().getColumn(4).setPreferredWidth(90);

            table.getColumn("").setCellRenderer(new ActionRenderer());
            table.getColumn("").setCellEditor(new ActionEditor(info, dialog));

            JScrollPane tableScroll = new JScrollPane(table);
            tableScroll.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(borderColor, 1),
                    BorderFactory.createEmptyBorder(0, 0, 0, 0)));
            tableScroll.setBackground(whiteBg);
            tableScroll.getViewport().setBackground(whiteBg);
            tableScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            tableScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tableScroll.getVerticalScrollBar().setUnitIncrement(16);
            tableScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(tableScroll);
        }

        body.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(whiteBg);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton closeBtn = createDialogButton("Close", textMedium, whiteBg, borderColor);
        closeBtn.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeBtn);
        body.add(buttonPanel);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        bodyScroll.getViewport().setBackground(whiteBg);
        bodyScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bodyScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        bodyScroll.getVerticalScrollBar().setUnitIncrement(16);

        return bodyScroll;
    }

    private JPanel createInfoLine(String label, String value, Color labelColor, Color valueColor) {
        JPanel line = new JPanel(new BorderLayout(10, 0));
        line.setBackground(new Color(0, 0, 0, 0));
        JLabel lbl = new JLabel(label + ":");
        lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        lbl.setForeground(labelColor);
        lbl.setPreferredSize(new Dimension(110, 20));
        line.add(lbl, BorderLayout.WEST);
        JLabel val = new JLabel(value);
        val.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        val.setForeground(valueColor);
        line.add(val, BorderLayout.CENTER);
        return line;
    }

    private JButton createDialogButton(String text, Color fg, Color bg, Color border) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(245, 247, 250));
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 6, 6);
                g2.setColor(fg);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(90, 34));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.repaint();
            }
        });
        return btn;
    }

    private class ActionRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private final JButton removeBtn;

        public ActionRenderer() {
            super(new FlowLayout(FlowLayout.CENTER, 0, 0));
            setBackground(Color.WHITE);
            removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            removeBtn.setForeground(new Color(239, 68, 68));
            removeBtn.setBackground(Color.WHITE);
            removeBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(239, 68, 68), 1),
                    BorderFactory.createEmptyBorder(4, 12, 4, 12)));
            removeBtn.setContentAreaFilled(false);
            removeBtn.setFocusPainted(false);
            add(removeBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(238, 242, 255));
            } else {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
            }
            return this;
        }
    }

    private class ActionEditor extends DefaultCellEditor {
        private final JButton removeBtn;
        private Job currentJob;
        private TaWorkloadInfo taInfo;
        private JDialog parentDialog;

        public ActionEditor(TaWorkloadInfo info, JDialog dialog) {
            super(new javax.swing.JCheckBox());
            this.taInfo = info;
            this.parentDialog = dialog;
            removeBtn = new JButton("Remove");
            removeBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            removeBtn.setForeground(new Color(239, 68, 68));
            removeBtn.setBackground(Color.WHITE);
            removeBtn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(239, 68, 68), 1),
                    BorderFactory.createEmptyBorder(4, 12, 4, 12)));
            removeBtn.setContentAreaFilled(false);
            removeBtn.setFocusPainted(false);
            removeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            removeBtn.addActionListener(e -> {
                fireEditingStopped();
                int confirm = UIHelper.showConfirmDialog(parentDialog,
                        "Remove " + taInfo.taUser.getName() + " from " + currentJob.getModuleCode() + "?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    List<Application> apps = FileUtil.loadApplications();
                    for (Application app : apps) {
                        if (app.getTaEmail().equals(taInfo.taUser.getEmail()) &&
                                app.getJobId().equals(currentJob.getJobId()) &&
                                "ACCEPTED".equals(app.getStatus())) {
                            app.setStatus("REJECTED");
                            break;
                        }
                    }
                    FileUtil.saveApplications(apps);
                    LoggerUtil.logUpdate("Workload",
                            "Removed TA " + taInfo.taUser.getEmail() + " from job " + currentJob.getJobId());
                    UIHelper.showInfoDialog(parentDialog, "TA removed from position.", "Success");
                    parentDialog.dispose();
                    refreshView();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentJob = (Job) value;
            removeBtn.setBackground(Color.WHITE);
            return removeBtn;
        }
    }

    private Profile findProfile(String email) {
        for (Profile p : allProfiles) {
            if (p.getEmail().equals(email))
                return p;
        }
        return null;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        label.setForeground(UIHelper.PRIMARY_COLOR);
        return label;
    }

    private JLabel createCellLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        return label;
    }

    private class TaWorkloadInfo {
        User taUser;
        Profile profile;
        int totalHours;
        List<Job> assignedJobs;
    }
}
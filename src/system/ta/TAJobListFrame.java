package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Frame for Teaching Assistants to browse available job positions.
 * <p>
 * This frame displays a table of all open TA positions with skill matching
 * indicators. TAs can click on a job to view details and apply.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class TAJobListFrame extends JFrame {

    /** The currently logged-in user */
    private final User currentUser;

    /** List of all jobs */
    private List<Job> jobs;

    /** List of all applications */
    private List<Application> applications;

    /** The TA's profile */
    private Profile taProfile;

    /** Table displaying the jobs */
    private JTable jobTable;

    /** Table model for the jobs table */
    private DefaultTableModel tableModel;

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
    private static final Color HEADER_BG = new Color(248, 250, 252);

    public TAJobListFrame(User user) {
        this.currentUser = user;
        this.jobs = FileUtil.loadJobs();
        this.applications = FileUtil.loadApplications();
        this.taProfile = FileUtil.getProfileByEmail(user.getEmail());

        setTitle("Available Positions");
        setSize(1180, 580);
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
        card.add(createTableScrollPane(), BorderLayout.CENTER);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(WHITE_BG);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(20, 28, 18, 28)));

        JPanel leftGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        leftGroup.setBackground(WHITE_BG);

        JPanel iconDot = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0,
                        new Color(99, 102, 241), getWidth(), getHeight(),
                        new Color(79, 70, 229));
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

        JLabel title = new JLabel("Available Positions");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(title);
        textGroup.add(Box.createRigidArea(new Dimension(0, 2)));

        JLabel subtitle = new JLabel("Browse and apply for open TA positions");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(subtitle);

        leftGroup.add(textGroup);
        header.add(leftGroup, BorderLayout.WEST);

        JButton refreshBtn = UIHelper.createButton("\u21BB  Refresh", UIHelper.SECONDARY_COLOR);
        refreshBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.addActionListener(e -> refreshTable());
        header.add(refreshBtn, BorderLayout.EAST);

        return header;
    }

    private JScrollPane createTableScrollPane() {
        buildJobTable();
        JScrollPane scrollPane = new JScrollPane(jobTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE_BG);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private void buildJobTable() {
        jobs = FileUtil.loadJobs();
        applications = FileUtil.loadApplications();
        taProfile = FileUtil.getProfileByEmail(currentUser.getEmail());

        List<Job> openJobs = new ArrayList<>();
        for (Job job : jobs) {
            if ("OPEN".equals(job.getStatus())) {
                openJobs.add(job);
            }
        }
        openJobs.sort(Comparator.comparing(Job::getDeadline));

        String[] columns = { "Module Code", "Module Name", "Hours", "Deadline", "Skills", "Match", "Applicants",
                "Status", "Action" };
        Object[][] data = new Object[openJobs.size()][9];

        for (int i = 0; i < openJobs.size(); i++) {
            Job job = openJobs.get(i);
            int accepted = countAcceptedApplicants(job.getJobId());
            boolean applied = hasApplied(job.getJobId());
            boolean full = accepted >= job.getApplicantLimit();

            data[i][0] = job.getModuleCode();
            data[i][1] = job.getModuleName();
            data[i][2] = job.getWeeklyHours();
            data[i][3] = job.getDeadline();
            data[i][4] = job;
            data[i][5] = job;
            data[i][6] = Integer.toString(accepted) + " / " + Integer.toString(job.getApplicantLimit());
            data[i][7] = full ? "Full" : (applied ? "Applied" : "Available");
            data[i][8] = job;
        }

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4 || column == 5 || column == 8)
                    return Job.class;
                return String.class;
            }
        };

        jobTable = new JTable(tableModel);
        configureTable();
    }

    private void configureTable() {
        jobTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        jobTable.setRowHeight(54);
        jobTable.setShowGrid(false);
        jobTable.setIntercellSpacing(new Dimension(0, 0));
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jobTable.setBackground(WHITE_BG);

        jobTable.setDefaultRenderer(String.class, new TextRenderer());
        jobTable.setDefaultRenderer(Job.class, new JobRenderer());

        TableColumn actionCol = jobTable.getColumnModel().getColumn(8);
        actionCol.setCellRenderer(new ActionRenderer());
        actionCol.setCellEditor(new ActionEditor());

        int[] widths = { 95, 145, 55, 100, 215, 65, 80, 80, 100 };
        for (int i = 0; i < widths.length; i++) {
            jobTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            jobTable.getColumnModel().getColumn(i).setMinWidth(widths[i]);
        }

        JTableHeader header = jobTable.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        header.setForeground(TEXT_MEDIUM);
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 38));

        TableCellRenderer defaultHeaderRenderer = header.getDefaultRenderer();
        if (defaultHeaderRenderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) defaultHeaderRenderer).setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    private void refreshTable() {
        jobs = FileUtil.loadJobs();
        applications = FileUtil.loadApplications();
        taProfile = FileUtil.getProfileByEmail(currentUser.getEmail());

        List<Job> openJobs = new ArrayList<>();
        for (Job job : jobs) {
            if ("OPEN".equals(job.getStatus())) {
                openJobs.add(job);
            }
        }
        openJobs.sort(Comparator.comparing(Job::getDeadline));

        tableModel.setRowCount(0);

        for (Job job : openJobs) {
            int accepted = countAcceptedApplicants(job.getJobId());
            boolean applied = hasApplied(job.getJobId());
            boolean full = accepted >= job.getApplicantLimit();

            tableModel.addRow(new Object[] {
                    job.getModuleCode(),
                    job.getModuleName(),
                    job.getWeeklyHours(),
                    job.getDeadline(),
                    job,
                    job,
                    accepted + " / " + job.getApplicantLimit(),
                    full ? "Full" : (applied ? "Applied" : "Available"),
                    job
            });
        }
    }

    private int countAcceptedApplicants(String jobId) {
        int count = 0;
        for (Application app : applications) {
            if (app.getJobId().equals(jobId) && "ACCEPTED".equalsIgnoreCase(app.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private boolean hasApplied(String jobId) {
        for (Application app : applications) {
            if (app.getTaEmail().equals(currentUser.getEmail()) && app.getJobId().equals(jobId)) {
                return true;
            }
        }
        return false;
    }

    private void submitApplication(Job job) {
        int confirm = UIHelper.showConfirmDialog(this,
                "Apply for " + job.getModuleCode() + " - " + job.getModuleName() + "?",
                "Confirm Application", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        List<Application> allApps = FileUtil.loadApplications();
        String appId = "APP" + System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Application newApp = new Application(appId, time, currentUser.getEmail(), job.getJobId(), "PENDING");
        allApps.add(newApp);
        FileUtil.saveApplications(allApps);
        applications = allApps;

        try {
            FileUtil.sendNotification(job.getMoEmail(),
                    "New Application Received",
                    currentUser.getName() + " has applied for: " + job.getModuleCode() + " - " + job.getModuleName(),
                    "APPLICATION");
        } catch (Exception ignored) {
        }

        LoggerUtil.logInfo("TA " + currentUser.getEmail() + " applied for job " + job.getJobId());
        UIHelper.showInfoDialog(this, "Application submitted successfully!", "Success");
        refreshTable();
    }

    // ===================== TEXT RENDERER =====================

    private class TextRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            setForeground(TEXT_DARK);

            if (!selected) {
                setBackground(row % 2 == 0 ? WHITE_BG : SOFT_BG);
            }

            String colName = table.getColumnName(col);
            switch (colName) {
                case "Module Code":
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    break;
                case "Hours":
                case "Deadline":
                    setForeground(TEXT_MEDIUM);
                    break;
                case "Applicants":
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    String parts = (String) value;
                    if (parts != null && parts.contains(" / ")) {
                        String[] sp = parts.split(" / ");
                        try {
                            setForeground(
                                    Integer.parseInt(sp[0]) >= Integer.parseInt(sp[1]) ? DANGER_RED : SUCCESS_GREEN);
                        } catch (NumberFormatException e) {
                            setForeground(TEXT_DARK);
                        }
                    }
                    break;
                case "Status":
                    setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                    String status = (String) value;
                    if ("Full".equals(status)) {
                        setForeground(DANGER_RED);
                    } else if ("Applied".equals(status)) {
                        setForeground(SUCCESS_GREEN);
                    } else {
                        setForeground(ACCENT_BLUE);
                    }
                    break;
            }
            return this;
        }
    }

    // ===================== JOB RENDERER (Skills + Match) =====================

    private class JobRenderer extends JPanel implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            Job job = (Job) value;
            if (job == null) {
                JLabel empty = new JLabel();
                empty.setBackground(WHITE_BG);
                empty.setOpaque(true);
                return empty;
            }

            Color rowBg = !selected ? (row % 2 == 0 ? WHITE_BG : SOFT_BG) : table.getSelectionBackground();

            if (col == 4) {
                return buildSkillsPanel(job, rowBg);
            } else if (col == 5) {
                return buildMatchLabel(job, rowBg);
            }

            JLabel empty = new JLabel();
            empty.setBackground(rowBg);
            empty.setOpaque(true);
            return empty;
        }

        private JPanel buildSkillsPanel(Job job, Color bg) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panel.setBackground(bg);
            panel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

            List<String> jobSkills = job.getSkills();
            if (jobSkills == null || jobSkills.isEmpty()) {
                JLabel none = new JLabel("-");
                none.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                none.setForeground(TEXT_LIGHT);
                panel.add(none);
            } else {
                int limit = Math.min(jobSkills.size(), 4);
                for (int i = 0; i < limit; i++) {
                    panel.add(buildSkillPill(jobSkills.get(i)));
                }
                if (jobSkills.size() > 4) {
                    JLabel more = new JLabel("+" + (jobSkills.size() - 4));
                    more.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                    more.setForeground(TEXT_LIGHT);
                    panel.add(more);
                }
            }
            return panel;
        }

        private JLabel buildSkillPill(String skill) {
            JLabel tag = new JLabel(skill) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(LIGHT_ACCENT);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                    g2.setColor(new Color(199, 210, 254));
                    g2.setStroke(new BasicStroke(0.5f));
                    g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
                    g2.setColor(ACCENT_BLUE);
                    g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(getText(), 7, (getHeight() + fm.getAscent()) / 2 - 2);
                    g2.dispose();
                }
            };
            tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            tag.setForeground(ACCENT_BLUE);
            tag.setOpaque(false);
            tag.setBorder(BorderFactory.createEmptyBorder(2, 14, 2, 14));
            tag.setPreferredSize(
                    new Dimension(tag.getFontMetrics(tag.getFont()).stringWidth(skill) + 20, 22));
            return tag;
        }

        private JLabel buildMatchLabel(Job job, Color bg) {
            List<String> jobSkills = job.getSkills();
            List<String> taSkills = (taProfile != null) ? taProfile.getSkills() : null;
            int pct = UIHelper.calculateSkillMatch(taSkills, jobSkills);

            JLabel label = new JLabel(pct + "%", SwingConstants.CENTER);
            label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

            if (pct >= 80) {
                label.setForeground(SUCCESS_GREEN);
                label.setBackground(new Color(220, 252, 231));
            } else if (pct >= 50) {
                label.setForeground(new Color(217, 119, 6));
                label.setBackground(new Color(254, 243, 199));
            } else {
                label.setForeground(TEXT_LIGHT);
                label.setBackground(bg);
            }
            return label;
        }
    }

    // ===================== ACTION RENDERER =====================

    private class ActionRenderer extends JPanel implements TableCellRenderer {
        private final JPanel greenPill = new JPanel();
        private final JPanel grayPill = new JPanel();
        private final JPanel bluePill = new JPanel();

        public ActionRenderer() {
            greenPill.setBackground(new Color(220, 252, 231));
            grayPill.setBackground(SOFT_BG);
            bluePill.setBackground(ACCENT_BLUE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            Job job = (Job) value;
            int accepted = countAcceptedApplicants(job.getJobId());
            boolean applied = hasApplied(job.getJobId());
            boolean full = accepted >= job.getApplicantLimit();

            JPanel target;
            String text;
            Color fg;

            if (applied) {
                target = greenPill;
                text = "Applied";
                fg = SUCCESS_GREEN;
            } else if (full) {
                target = grayPill;
                text = "Full";
                fg = TEXT_LIGHT;
            } else {
                target = bluePill;
                text = "Apply";
                fg = WHITE_BG;
            }

            target.removeAll();
            target.setLayout(new GridBagLayout());
            JLabel lbl = new JLabel(text);
            lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            lbl.setForeground(fg);
            target.add(lbl);

            Color rowBg = !selected ? (row % 2 == 0 ? WHITE_BG : SOFT_BG) : table.getSelectionBackground();
            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setBackground(rowBg);

            JPanel pillWrapper = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(target.getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                }
            };
            pillWrapper.setOpaque(false);
            pillWrapper.setPreferredSize(new Dimension(76, 28));
            pillWrapper.setLayout(new GridBagLayout());
            pillWrapper.add(lbl);

            wrapper.add(pillWrapper);
            wrapper.setOpaque(true);
            return wrapper;
        }
    }

    // ===================== ACTION EDITOR =====================

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton applyBtn;
        private Job currentJob;

        ActionEditor() {
            applyBtn = new JButton("Apply");
            applyBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            applyBtn.setBackground(ACCENT_BLUE);
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFocusPainted(false);
            applyBtn.setBorderPainted(false);
            applyBtn.setOpaque(true);
            applyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            applyBtn.setPreferredSize(new Dimension(76, 28));

            applyBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (applyBtn.isEnabled())
                        applyBtn.setBackground(ACCENT_BLUE.darker());
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (applyBtn.isEnabled())
                        applyBtn.setBackground(ACCENT_BLUE);
                }
            });

            applyBtn.addActionListener(e -> {
                if (currentJob != null) {
                    submitApplication(currentJob);
                }
                fireEditingStopped();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean selected, int row, int col) {
            currentJob = (Job) value;
            if (currentJob == null)
                return applyBtn;

            int accepted = countAcceptedApplicants(currentJob.getJobId());
            boolean applied = hasApplied(currentJob.getJobId());
            boolean full = accepted >= currentJob.getApplicantLimit();

            if (applied) {
                applyBtn.setEnabled(false);
                applyBtn.setBackground(new Color(220, 252, 231));
                applyBtn.setForeground(SUCCESS_GREEN);
                applyBtn.setText("Applied");
            } else if (full) {
                applyBtn.setEnabled(false);
                applyBtn.setBackground(SOFT_BG);
                applyBtn.setForeground(TEXT_LIGHT);
                applyBtn.setText("Full");
            } else {
                applyBtn.setEnabled(true);
                applyBtn.setBackground(ACCENT_BLUE);
                applyBtn.setForeground(Color.WHITE);
                applyBtn.setText("Apply");
            }
            return applyBtn;
        }

        @Override
        public Object getCellEditorValue() {
            return currentJob;
        }
    }
}
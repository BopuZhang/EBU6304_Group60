package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

public class TAApplicationStatusFrame extends JFrame {
    private final User currentUser;
    private List<Application> applications;
    private List<Job> jobs;
    private JTable appTable;
    private DefaultTableModel tableModel;
    private JLabel statsLabel;

    private static final Color WHITE_BG = Color.WHITE;
    private static final Color SOFT_BG = new Color(245, 247, 250);
    private static final Color ACCENT_BLUE = new Color(79, 70, 229);
    private static final Color LIGHT_ACCENT = new Color(238, 242, 255);
    private static final Color TEXT_DARK = new Color(30, 41, 59);
    private static final Color TEXT_MEDIUM = new Color(71, 85, 105);
    private static final Color TEXT_LIGHT = new Color(148, 163, 184);
    private static final Color HEADER_BG = new Color(248, 250, 252);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color WARNING_AMBER = new Color(245, 158, 11);

    public TAApplicationStatusFrame(User user) {
        this.currentUser = user;
        this.applications = FileUtil.loadApplications();
        this.jobs = FileUtil.loadJobs();

        setTitle("My Application Status");
        setSize(880, 540);
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
        card.add(createTablePanel(), BorderLayout.CENTER);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
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
                        new Color(34, 197, 94), getWidth(), getHeight(),
                        new Color(16, 185, 129));
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

        JLabel title = new JLabel("My Application Status");
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        title.setForeground(TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(title);
        textGroup.add(Box.createRigidArea(new Dimension(0, 2)));

        JLabel subtitle = new JLabel("Track the status of your submitted applications");
        subtitle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        subtitle.setForeground(TEXT_LIGHT);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        textGroup.add(subtitle);

        leftGroup.add(textGroup);
        header.add(leftGroup, BorderLayout.WEST);

        JButton refreshBtn = new JButton("\u21BB  Refresh") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(241, 245, 249));
                } else {
                    g2.setColor(WHITE_BG);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(new Color(226, 232, 240));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 8, 8);
                g2.setColor(TEXT_MEDIUM);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
                FontMetrics fm = g2.getFontMetrics();
                String t = getText();
                g2.drawString(t, (getWidth() - fm.stringWidth(t)) / 2,
                        (getHeight() + fm.getAscent()) / 2 - 2);
                g2.dispose();
            }
        };
        refreshBtn.setPreferredSize(new Dimension(100, 34));
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshTable());
        header.add(refreshBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createTablePanel() {
        buildTable();

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(WHITE_BG);

        JScrollPane scrollPane = new JScrollPane(appTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(WHITE_BG);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(createStatsBar(), BorderLayout.SOUTH);

        return tablePanel;
    }

    private JPanel createStatsBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0));
        bar.setBackground(WHITE_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(241, 245, 249)),
                BorderFactory.createEmptyBorder(12, 28, 12, 28)
        ));

        statsLabel = new JLabel();
        statsLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        statsLabel.setForeground(TEXT_MEDIUM);
        bar.add(statsLabel, BorderLayout.WEST);
        updateStats();

        return bar;
    }

    private void updateStats() {
        int total = 0, accepted = 0, pending = 0, rejected = 0;
        for (Application app : applications) {
            if (!app.getTaEmail().equals(currentUser.getEmail())) continue;
            total++;
            String status = app.getStatus();
            if ("ACCEPTED".equalsIgnoreCase(status)) accepted++;
            else if ("REJECTED".equalsIgnoreCase(status)) rejected++;
            else pending++;
        }
        statsLabel.setText(String.format(
                "<html>Total: <b style='color:#1E293B'>%d</b> &nbsp;&nbsp;"
                        + "Accepted: <b style='color:#22C55E'>%d</b> &nbsp;&nbsp;"
                        + "Pending: <b style='color:#4F46E5'>%d</b> &nbsp;&nbsp;"
                        + "Rejected: <b style='color:#EF4444'>%d</b></html>",
                total, accepted, pending, rejected));
    }

    private void buildTable() {
        applications = FileUtil.loadApplications();
        jobs = FileUtil.loadJobs();

        List<Application> myApps = new ArrayList<>();
        for (Application app : applications) {
            if (app.getTaEmail().equals(currentUser.getEmail())) {
                myApps.add(app);
            }
        }
        myApps.sort((a1, a2) -> a2.getApplyTime().compareTo(a1.getApplyTime()));

        String[] columns = { "#", "Apply Date", "Position", "Module Code", "Weekly Hours", "Status" };
        Object[][] data = new Object[myApps.size()][6];

        for (int i = 0; i < myApps.size(); i++) {
            Application app = myApps.get(i);
            Job job = findJob(app.getJobId());

            data[i][0] = i + 1;
            data[i][1] = app.getApplyTime();
            data[i][2] = job != null ? job.getModuleName() : "N/A";
            data[i][3] = job != null ? job.getModuleCode() : "N/A";
            data[i][4] = job != null ? job.getWeeklyHours() : 0;
            data[i][5] = app.getStatus();
        }

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 0) return Integer.class;
                if (col == 4) return Integer.class;
                if (col == 5) return String.class;
                return String.class;
            }
        };

        appTable = new JTable(tableModel);
        configureTable();
    }

    private void configureTable() {
        appTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        appTable.setRowHeight(46);
        appTable.setShowGrid(false);
        appTable.setIntercellSpacing(new Dimension(0, 0));
        appTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appTable.setBackground(WHITE_BG);

        appTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = appTable.getSelectedRow();
                if (row >= 0) {
                    appTable.setSelectionBackground(new Color(224, 231, 255));
                }
            }
        });

        appTable.setDefaultRenderer(Object.class, new StatusTableRenderer());

        JTableHeader header = appTable.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        header.setForeground(TEXT_MEDIUM);
        header.setBackground(HEADER_BG);
        header.setPreferredSize(new Dimension(0, 38));

        TableCellRenderer defRenderer = header.getDefaultRenderer();
        if (defRenderer instanceof DefaultTableCellRenderer) {
            ((DefaultTableCellRenderer) defRenderer).setHorizontalAlignment(SwingConstants.CENTER);
        }

        int[] widths = { 35, 155, 210, 110, 95, 110 };
        for (int i = 0; i < widths.length; i++) {
            appTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            appTable.getColumnModel().getColumn(i).setMinWidth(
                    i == 0 ? 30 : (widths[i] - 20));
        }
    }

    private class StatusTableRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean selected, boolean focused, int row, int col) {
            super.getTableCellRendererComponent(table, value, selected, focused, row, col);
            setHorizontalAlignment(col == 0 ? SwingConstants.CENTER : (col == 4 ? SwingConstants.CENTER : SwingConstants.CENTER));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            setForeground(TEXT_DARK);

            if (!selected) {
                setBackground(row % 2 == 0 ? WHITE_BG : SOFT_BG);
            }

            if (col == 0) {
                setForeground(TEXT_LIGHT);
                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            } else if (col == 1) {
                setForeground(TEXT_MEDIUM);
            } else if (col == 2) {
                setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            } else if (col == 3) {
                setForeground(TEXT_MEDIUM);
                setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
            } else if (col == 4) {
                Integer hours = (Integer) value;
                if (hours != null && hours > 0) {
                    setText(hours + " h/w");
                } else {
                    setText("N/A");
                }
                setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                setForeground(TEXT_MEDIUM);
            } else if (col == 5) {
                return createStatusBadge((String) value, selected, row, table);
            }

            return this;
        }

        private JPanel createStatusBadge(String status, boolean selected, int row, JTable table) {
            Color rowBg = !selected ? (row % 2 == 0 ? WHITE_BG : SOFT_BG) : table.getSelectionBackground();
            Color badgeBg;
            Color badgeFg;
            String displayText;

            if ("ACCEPTED".equalsIgnoreCase(status)) {
                badgeBg = new Color(220, 252, 231);
                badgeFg = SUCCESS_GREEN;
                displayText = "Accepted";
            } else if ("REJECTED".equalsIgnoreCase(status)) {
                badgeBg = new Color(254, 226, 226);
                badgeFg = DANGER_RED;
                displayText = "Rejected";
            } else {
                badgeBg = LIGHT_ACCENT;
                badgeFg = ACCENT_BLUE;
                displayText = "Pending";
            }

            JPanel outer = new JPanel(new GridBagLayout());
            outer.setBackground(rowBg);

            JPanel badge = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(badgeBg);
                    g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                    g2.dispose();
                }
            };
            badge.setOpaque(false);
            badge.setPreferredSize(new Dimension(76, 26));
            badge.setLayout(new GridBagLayout());

            JLabel lbl = new JLabel(displayText);
            lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            lbl.setForeground(badgeFg);
            badge.add(lbl);

            outer.add(badge);
            return outer;
        }
    }

    private void refreshTable() {
        applications = FileUtil.loadApplications();
        jobs = FileUtil.loadJobs();
        buildTable();
        configureTable();
        updateStats();
        appTable.revalidate();
        appTable.repaint();
    }

    private Job findJob(String jobId) {
        for (Job job : jobs) {
            if (job.getJobId().equals(jobId)) return job;
        }
        return null;
    }
}
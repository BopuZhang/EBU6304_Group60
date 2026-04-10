package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for TA to view their application statuses.
 * Features fixed table header and color-coded status.
 */
public class TAApplicationStatusFrame extends JFrame {
    private final User currentUser;
    private List<Application> applications;
    private List<Job> jobs;
    private JTable appTable;
    private DefaultTableModel tableModel;

    public TAApplicationStatusFrame(User user) {
        this.currentUser = user;
        this.applications = FileUtil.loadApplications();
        this.jobs = FileUtil.loadJobs();

        setTitle("My Application Status");
        setSize(800, 450);
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
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Title
        JLabel title = UIHelper.createTitle("My Application Status");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(title, BorderLayout.NORTH);

        // Create table
        createApplicationTable();
        JScrollPane scrollPane = new JScrollPane(appTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scrollPane, BorderLayout.CENTER);

        // Refresh button at bottom right
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.SECONDARY_COLOR);
        refreshBtn.addActionListener(e -> refreshTable());
        bottomPanel.add(refreshBtn);
        card.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void createApplicationTable() {
        // Filter applications for current TA
        List<Application> myApps = new ArrayList<>();
        for (Application app : applications) {
            if (app.getTaEmail().equals(currentUser.getEmail())) {
                myApps.add(app);
            }
        }
        // Sort by apply time (newest first)
        myApps.sort((a1, a2) -> a2.getApplyTime().compareTo(a1.getApplyTime()));

        // Column names
        String[] columns = {"Apply Date", "Position", "Module Code", "Weekly Hours", "Status"};

        // Prepare data
        Object[][] data = new Object[myApps.size()][5];
        for (int i = 0; i < myApps.size(); i++) {
            Application app = myApps.get(i);
            Job job = findJob(app.getJobId());

            data[i][0] = app.getApplyTime();
            data[i][1] = job != null ? job.getModuleName() : "N/A";
            data[i][2] = job != null ? job.getModuleCode() : "N/A";
            data[i][3] = job != null ? String.valueOf(job.getWeeklyHours()) : "N/A";
            data[i][4] = app.getStatus();
        }

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable cells
            }
        };

        appTable = new JTable(tableModel);
        appTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        appTable.setRowHeight(40);
        appTable.setShowGrid(false);
        appTable.setIntercellSpacing(new Dimension(0, 0));
        appTable.setSelectionBackground(new Color(240, 245, 250));

        // Header renderer
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        headerRenderer.setForeground(UIHelper.PRIMARY_COLOR);
        headerRenderer.setBackground(new Color(240, 240, 240));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < appTable.getColumnCount(); i++) {
            appTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Cell renderer
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                // Status column color coding
                if (column == 4) {
                    String status = (String) value;
                    if ("ACCEPTED".equals(status)) {
                        setForeground(UIHelper.SUCCESS_COLOR);
                    } else if ("REJECTED".equals(status)) {
                        setForeground(new Color(244, 67, 54));
                    } else { // PENDING
                        setForeground(UIHelper.ACCENT_COLOR);
                    }
                } else {
                    setForeground(Color.BLACK);
                }
                return c;
            }
        };
        for (int i = 0; i < appTable.getColumnCount(); i++) {
            appTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // Set column widths
        appTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        appTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        appTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        appTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        appTable.getColumnModel().getColumn(4).setPreferredWidth(100);
    }

    private void refreshTable() {
        applications = FileUtil.loadApplications();
        jobs = FileUtil.loadJobs();
        createApplicationTable();
        appTable.setModel(tableModel);
        // Re-apply renderers
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                if (column == 4) {
                    String status = (String) value;
                    if ("ACCEPTED".equals(status)) setForeground(UIHelper.SUCCESS_COLOR);
                    else if ("REJECTED".equals(status)) setForeground(new Color(244, 67, 54));
                    else setForeground(UIHelper.ACCENT_COLOR);
                } else {
                    setForeground(Color.BLACK);
                }
                return c;
            }
        };
        for (int i = 0; i < appTable.getColumnCount(); i++) {
            appTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }
        appTable.revalidate();
        appTable.repaint();
    }

    private Job findJob(String jobId) {
        for (Job job : jobs) {
            if (job.getJobId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }
}
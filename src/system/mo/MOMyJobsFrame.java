package system.mo;

import system.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for Module Organizers to view and manage their posted job positions.
 * <p>
 * This frame displays a table of all jobs posted by the current MO with
 * options to edit, delete, and toggle job status.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class MOMyJobsFrame extends JFrame {

    /** The currently logged-in user (Module Organizer) */
    private final User currentUser;

    /** List of jobs posted by this MO */
    private List<Job> myJobs;

    /** Table displaying the jobs */
    private JTable jobTable;

    /** Table model for the jobs table */
    private DefaultTableModel tableModel;

    /**
     * Constructs the frame to view and manage posted jobs.
     *
     * @param user the logged-in Module Organizer
     */
    public MOMyJobsFrame(User user) {
        this.currentUser = user;
        this.myJobs = loadMyJobs();

        setTitle("My Posted Positions");
        setSize(1600, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    private List<Job> loadMyJobs() {
        List<Job> all = FileUtil.loadJobs();
        List<Job> mine = new ArrayList<>();
        for (Job job : all) {
            if (job.getMoEmail().equals(currentUser.getEmail())) {
                mine.add(job);
            }
        }
        mine.sort(Comparator.comparing(Job::getDeadline));
        return mine;
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIHelper.createCardPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));

        // Top panel with title and new button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel title = UIHelper.createTitle("My Posted Positions");
        topPanel.add(title, BorderLayout.WEST);

        JButton newBtn = UIHelper.createButton("+ New Position", UIHelper.SUCCESS_COLOR);
        newBtn.addActionListener(e -> {
            new MOPostJobFrame(currentUser);
            dispose();
        });
        topPanel.add(newBtn, BorderLayout.EAST);
        card.add(topPanel, BorderLayout.NORTH);

        if (myJobs.isEmpty()) {
            JLabel empty = new JLabel("No positions posted yet.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            empty.setForeground(Color.GRAY);
            card.add(empty, BorderLayout.CENTER);
        } else {
            createJobTable(card);
        }

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void createJobTable(JPanel card) {
        List<Application> allApps = FileUtil.loadApplications();

        String[] columns = { "Job ID", "Module Code", "Module Name", "Weekly Hours",
                "Deadline", "Skills", "Applicants", "Status", "Actions" };

        Object[][] data = new Object[myJobs.size()][9];
        for (int i = 0; i < myJobs.size(); i++) {
            Job job = myJobs.get(i);
            int acceptedCount = countAccepted(allApps, job.getJobId());
            String applicants = acceptedCount + " / " + job.getApplicantLimit();
            String status = "OPEN".equals(job.getStatus()) ? "Open" : "Closed";
            String skillsStr = "";
            if (job.getSkills() != null && !job.getSkills().isEmpty()) {
                skillsStr = String.join(", ", job.getSkills());
            }

            data[i][0] = job.getJobId();
            data[i][1] = job.getModuleCode();
            data[i][2] = job.getModuleName();
            data[i][3] = job.getWeeklyHours();
            data[i][4] = job.getDeadline();
            data[i][5] = skillsStr;
            data[i][6] = applicants;
            data[i][7] = status;
            data[i][8] = job; // Store Job object for actions
        }

        tableModel = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return column == 8 ? Job.class : String.class;
            }
        };

        jobTable = new JTable(tableModel);
        jobTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        jobTable.setRowHeight(45);
        jobTable.setShowGrid(false);
        jobTable.setIntercellSpacing(new Dimension(0, 0));

        // Custom renderer for color-coded cells
        jobTable.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                if (column == 6) { // Applicants column
                    String text = (String) value;
                    String[] parts = text.split(" / ");
                    int current = Integer.parseInt(parts[0]);
                    int limit = Integer.parseInt(parts[1]);
                    setForeground(current >= limit ? UIHelper.DANGER_COLOR : UIHelper.SUCCESS_COLOR);
                } else if (column == 7) { // Status column
                    setForeground("Open".equals(value) ? UIHelper.SUCCESS_COLOR : UIHelper.DISABLED_COLOR);
                } else {
                    setForeground(Color.BLACK);
                }
                return c;
            }
        });

        // Action column with buttons
        TableColumn actionCol = jobTable.getColumnModel().getColumn(8);
        actionCol.setCellRenderer(new ButtonRenderer());
        actionCol.setCellEditor(new ButtonEditor(new JCheckBox(), this, allApps));

        // Column widths
        int[] widths = { 100, 90, 160, 80, 90, 200, 80, 70, 230 };
        for (int i = 0; i < widths.length; i++) {
            jobTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            jobTable.getColumnModel().getColumn(i).setMinWidth(widths[i]);
        }

        // Header style
        JTableHeader header = jobTable.getTableHeader();
        header.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(UIHelper.PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(0, 40));

        JScrollPane scrollPane = new JScrollPane(jobTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        card.add(scrollPane, BorderLayout.CENTER);
    }

    private int countAccepted(List<Application> apps, String jobId) {
        int count = 0;
        for (Application app : apps) {
            if (app.getJobId().equals(jobId) && "ACCEPTED".equals(app.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private void editJob(Job job) {
        new MOEditJobFrame(currentUser, job);
        dispose();
    }

    private void deleteJob(Job job) {
        List<Application> apps = FileUtil.loadApplications();
        int appCount = 0;
        for (Application app : apps) {
            if (app.getJobId().equals(job.getJobId()))
                appCount++;
        }
        String msg = appCount > 0
                ? "This position has " + appCount
                        + " applicant(s). Deleting will remove all associated applications.\nContinue?"
                : "Delete this position?";
        int confirm = UIHelper.showConfirmDialog(this, msg, "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        List<Job> allJobs = FileUtil.loadJobs();
        allJobs.removeIf(j -> j.getJobId().equals(job.getJobId()));
        FileUtil.saveJobs(allJobs);

        apps.removeIf(a -> a.getJobId().equals(job.getJobId()));
        FileUtil.saveApplications(apps);

        LoggerUtil.logDelete("Job", job.getJobId());
        UIHelper.showInfoDialog(this, "Position deleted.", "Success");
        dispose();
        new MOMyJobsFrame(currentUser);
    }

    private void toggleJobStatus(Job job) {
        List<Application> apps = FileUtil.loadApplications();
        int accepted = countAccepted(apps, job.getJobId());
        String newStatus = "OPEN".equals(job.getStatus()) ? "CLOSED" : "OPEN";

        if (newStatus.equals("OPEN") && accepted >= job.getApplicantLimit()) {
            UIHelper.showWarningDialog(this, "Cannot reopen: applicant limit reached.", "Warning");
            return;
        }

        int confirm = UIHelper.showConfirmDialog(this,
                (newStatus.equals("CLOSED") ? "Close" : "Reopen") + " this position?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        List<Job> allJobs = FileUtil.loadJobs();
        for (Job j : allJobs) {
            if (j.getJobId().equals(job.getJobId())) {
                j.setStatus(newStatus);
                break;
            }
        }
        FileUtil.saveJobs(allJobs);
        dispose();
        new MOMyJobsFrame(currentUser);
    }

    // Button renderer for action column
    // Button renderer for action column
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn, deleteBtn, toggleBtn;

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 2));
            setOpaque(true);

            editBtn = new JButton("Edit");
            styleButton(editBtn, UIHelper.PRIMARY_COLOR);

            deleteBtn = new JButton("Delete");
            styleButton(deleteBtn, UIHelper.DANGER_COLOR);

            toggleBtn = new JButton();
            styleButton(toggleBtn, UIHelper.DISABLED_COLOR);

            add(editBtn);
            add(deleteBtn);
            add(toggleBtn);
        }

        private void styleButton(JButton btn, Color bg) {
            btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setPreferredSize(new Dimension(60, 28));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Job) {
                Job job = (Job) value;
                if ("OPEN".equals(job.getStatus())) {
                    toggleBtn.setText("Close");
                    toggleBtn.setBackground(UIHelper.DANGER_COLOR);
                } else {
                    toggleBtn.setText("Reopen");
                    toggleBtn.setBackground(UIHelper.SUCCESS_COLOR);
                }
            }
            setBackground(isSelected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250)));
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editBtn, deleteBtn, toggleBtn;
        private Job currentJob;
        private MOMyJobsFrame frame;
        private List<Application> allApps;

        public ButtonEditor(JCheckBox checkBox, MOMyJobsFrame frame, List<Application> allApps) {
            this.frame = frame;
            this.allApps = allApps;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
            panel.setOpaque(true);

            editBtn = new JButton("Edit");
            styleButton(editBtn, UIHelper.PRIMARY_COLOR);
            deleteBtn = new JButton("Delete");
            styleButton(deleteBtn, UIHelper.DANGER_COLOR);
            toggleBtn = new JButton();
            styleButton(toggleBtn, UIHelper.DISABLED_COLOR);

            editBtn.addActionListener(e -> {
                if (currentJob != null) {
                    frame.editJob(currentJob);
                    fireEditingStopped();
                }
            });

            deleteBtn.addActionListener(e -> {
                if (currentJob != null) {
                    frame.deleteJob(currentJob);
                    fireEditingStopped();
                }
            });

            toggleBtn.addActionListener(e -> {
                if (currentJob != null) {
                    frame.toggleJobStatus(currentJob);
                    fireEditingStopped();
                }
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
            panel.add(toggleBtn);
        }

        private void styleButton(JButton btn, Color bg) {
            btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
            btn.setBackground(bg);
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.setPreferredSize(new Dimension(60, 28));
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentJob = (Job) value;
            if (currentJob != null) {
                if ("OPEN".equals(currentJob.getStatus())) {
                    toggleBtn.setText("Close");
                    toggleBtn.setBackground(UIHelper.DANGER_COLOR);
                } else {
                    toggleBtn.setText("Reopen");
                    toggleBtn.setBackground(UIHelper.SUCCESS_COLOR);
                }
            }
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentJob;
        }
    }
}
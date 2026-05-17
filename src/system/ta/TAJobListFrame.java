package system.ta;

import system.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class TAJobListFrame extends JFrame {
    private final User currentUser;
    private List<Job> jobs;
    private List<Application> applications;
    private Profile taProfile;
    private JTable jobTable;
    private DefaultTableModel tableModel;

    public TAJobListFrame(User user) {
        this.currentUser = user;
        this.jobs = FileUtil.loadJobs();
        this.applications = FileUtil.loadApplications();
        this.taProfile = FileUtil.getProfileByEmail(user.getEmail());

        setTitle("Available Positions");
        setSize(1150, 550);
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

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel title = UIHelper.createTitle("Available Positions");
        topPanel.add(title, BorderLayout.WEST);

        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.SECONDARY_COLOR);
        refreshBtn.addActionListener(e -> refreshTable());
        topPanel.add(refreshBtn, BorderLayout.EAST);
        card.add(topPanel, BorderLayout.NORTH);

        createJobTable(card);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void createJobTable(JPanel card) {
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
            int acceptedCount = countAcceptedApplicants(job.getJobId());
            boolean applied = hasApplied(job.getJobId());
            boolean full = acceptedCount >= job.getApplicantLimit();

            data[i][0] = job.getModuleCode();
            data[i][1] = job.getModuleName();
            data[i][2] = job.getWeeklyHours();
            data[i][3] = job.getDeadline();
            data[i][4] = job;
            data[i][5] = job;
            data[i][6] = acceptedCount + " / " + job.getApplicantLimit();
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
        jobTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        jobTable.setRowHeight(50);
        jobTable.setShowGrid(false);
        jobTable.setIntercellSpacing(new Dimension(0, 0));
        jobTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jobTable.setDefaultRenderer(String.class, new TextRenderer());
        jobTable.setDefaultRenderer(Job.class, new JobRenderer());

        TableColumn actionCol = jobTable.getColumnModel().getColumn(8);
        actionCol.setCellRenderer(new ActionRenderer());
        actionCol.setCellEditor(new ActionEditor(new JCheckBox()));

        int[] widths = { 90, 140, 55, 95, 200, 60, 75, 75, 95 };
        for (int i = 0; i < widths.length; i++) {
            jobTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
            jobTable.getColumnModel().getColumn(i).setMinWidth(widths[i]);
        }

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

    private void refreshTable() {
        jobs = FileUtil.loadJobs();
        applications = FileUtil.loadApplications();
        taProfile = FileUtil.getProfileByEmail(currentUser.getEmail());

        tableModel.setRowCount(0);

        List<Job> openJobs = new ArrayList<>();
        for (Job job : jobs) {
            if ("OPEN".equals(job.getStatus())) {
                openJobs.add(job);
            }
        }
        openJobs.sort(Comparator.comparing(Job::getDeadline));

        for (Job job : openJobs) {
            int acceptedCount = countAcceptedApplicants(job.getJobId());
            boolean applied = hasApplied(job.getJobId());
            boolean full = acceptedCount >= job.getApplicantLimit();

            tableModel.addRow(new Object[] {
                    job.getModuleCode(),
                    job.getModuleName(),
                    job.getWeeklyHours(),
                    job.getDeadline(),
                    job,
                    job,
                    acceptedCount + " / " + job.getApplicantLimit(),
                    full ? "Full" : (applied ? "Applied" : "Available"),
                    job
            });
        }
    }

    private int countAcceptedApplicants(String jobId) {
        int count = 0;
        for (Application app : applications) {
            if (app.getJobId().equals(jobId) && "ACCEPTED".equals(app.getStatus())) {
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

        String appId = "APP" + System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Application newApp = new Application(appId, time, currentUser.getEmail(), job.getJobId(), "PENDING");

        List<Application> allApps = FileUtil.loadApplications();
        allApps.add(newApp);
        FileUtil.saveApplications(allApps);
        applications = allApps;

        String notificationTitle = "New Application Received";
        String notificationContent = currentUser.getName() + " has applied for your position: " +
                job.getModuleCode() + " - " + job.getModuleName() + ".";
        FileUtil.sendNotification(job.getMoEmail(), notificationTitle, notificationContent, "APPLICATION");

        LoggerUtil.logInfo("TA " + currentUser.getEmail() + " applied for job " + job.getJobId());
        UIHelper.showInfoDialog(this, "Application submitted successfully!", "Success");
        refreshTable();
    }

    private class TextRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
            }
            setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));

            String colName = table.getColumnName(column);
            if ("Applicants".equals(colName)) {
                String text = (String) value;
                String[] parts = text.split(" / ");
                int current = Integer.parseInt(parts[0]);
                int limit = Integer.parseInt(parts[1]);
                setForeground(current >= limit ? UIHelper.DANGER_COLOR : UIHelper.SUCCESS_COLOR);
            } else if ("Status".equals(colName)) {
                String text = (String) value;
                if ("Full".equals(text)) {
                    setForeground(UIHelper.DANGER_COLOR);
                } else if ("Applied".equals(text)) {
                    setForeground(UIHelper.SUCCESS_COLOR);
                } else {
                    setForeground(UIHelper.PRIMARY_COLOR);
                }
            } else {
                setForeground(Color.BLACK);
            }
            return c;
        }
    }

    private class JobRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            Job job = (Job) value;
            Color bg = !isSelected ? (row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250))
                    : table.getSelectionBackground();

            if (column == 4) {
                JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
                panel.setBackground(bg);
                panel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

                List<String> jobSkills = job.getSkills();
                if (jobSkills != null && !jobSkills.isEmpty()) {
                    for (String skill : jobSkills) {
                        JLabel tag = UIHelper.createSkillTag(skill);
                        tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                        panel.add(tag);
                    }
                } else {
                    JLabel noSkill = new JLabel("-");
                    noSkill.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                    noSkill.setForeground(Color.GRAY);
                    panel.add(noSkill);
                }
                return panel;
            } else if (column == 5) {
                List<String> jobSkills = job.getSkills();
                List<String> taSkills = (taProfile != null) ? taProfile.getSkills() : null;
                int matchPercent = UIHelper.calculateSkillMatch(taSkills, jobSkills);

                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(bg);
                JLabel label = UIHelper.createMatchLabel(matchPercent);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(label, BorderLayout.CENTER);
                return panel;
            }

            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setBackground(bg);
            label.setOpaque(true);
            return label;
        }
    }

    private class ActionRenderer extends JPanel implements TableCellRenderer {
        private JButton applyBtn;

        public ActionRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            setOpaque(true);
            applyBtn = new JButton();
            applyBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFocusPainted(false);
            applyBtn.setBorderPainted(false);
            applyBtn.setContentAreaFilled(false);
            applyBtn.setOpaque(true);
            applyBtn.setPreferredSize(new Dimension(80, 30));
            add(applyBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            Job job = (Job) value;
            int acceptedCount = countAcceptedApplicants(job.getJobId());
            boolean applied = hasApplied(job.getJobId());
            boolean full = acceptedCount >= job.getApplicantLimit();

            if (applied) {
                applyBtn.setText("Applied");
                applyBtn.setBackground(new Color(180, 180, 180));
            } else if (full) {
                applyBtn.setText("Full");
                applyBtn.setBackground(new Color(180, 180, 180));
            } else {
                applyBtn.setText("Apply");
                applyBtn.setBackground(UIHelper.PRIMARY_COLOR);
            }
            applyBtn.setEnabled(!applied && !full);

            setBackground(isSelected ? table.getSelectionBackground()
                    : (row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250)));
            return this;
        }
    }

    private class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton applyBtn;
        private Job currentJob;

        public ActionEditor(JCheckBox checkBox) {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            panel.setOpaque(true);

            applyBtn = new JButton("Apply");
            applyBtn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            applyBtn.setForeground(Color.WHITE);
            applyBtn.setFocusPainted(false);
            applyBtn.setBorderPainted(false);
            applyBtn.setContentAreaFilled(false);
            applyBtn.setOpaque(true);
            applyBtn.setBackground(UIHelper.PRIMARY_COLOR);
            applyBtn.setPreferredSize(new Dimension(80, 30));
            applyBtn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    applyBtn.setBackground(UIHelper.PRIMARY_COLOR.darker());
                }

                public void mouseExited(MouseEvent evt) {
                    applyBtn.setBackground(UIHelper.PRIMARY_COLOR);
                }
            });
            applyBtn.addActionListener(e -> {
                if (currentJob != null) {
                    submitApplication(currentJob);
                    fireEditingStopped();
                }
            });

            panel.add(applyBtn);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentJob = (Job) value;
            int acceptedCount = countAcceptedApplicants(currentJob.getJobId());
            boolean applied = hasApplied(currentJob.getJobId());
            boolean full = acceptedCount >= currentJob.getApplicantLimit();

            if (applied || full) {
                applyBtn.setEnabled(false);
                applyBtn.setBackground(new Color(180, 180, 180));
                applyBtn.setText(applied ? "Applied" : "Full");
            } else {
                applyBtn.setEnabled(true);
                applyBtn.setBackground(UIHelper.PRIMARY_COLOR);
                applyBtn.setText("Apply");
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
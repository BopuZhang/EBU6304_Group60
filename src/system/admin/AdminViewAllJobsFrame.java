package system.admin;

import system.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for administrators to view all job positions with status filtering.
 * <p>
 * This frame displays a table of all job positions in the system with
 * options to filter by status (Open/Closed).
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class AdminViewAllJobsFrame extends JFrame {

    /** List of all jobs in the system */
    private final List<Job> allJobs;

    /** Container for the table */
    private JPanel tableContainer;

    /** Status filter combo box */
    private JComboBox<String> statusFilter;

    /**
     * Constructs the frame to view all job positions.
     */
    public AdminViewAllJobsFrame() {
        this.allJobs = FileUtil.loadJobs();

        setTitle("All Positions");
        setSize(950, 500);
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

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel title = UIHelper.createTitle("All Positions");
        topPanel.add(title, BorderLayout.WEST);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(new JLabel("Filter by status:"));
        statusFilter = new JComboBox<>(new String[]{"All", "Open", "Closed"});
        statusFilter.setPreferredSize(new Dimension(100, 30));
        statusFilter.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        filterPanel.add(statusFilter);

        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> refreshTable());
        filterPanel.add(refreshBtn);

        topPanel.add(filterPanel, BorderLayout.EAST);
        card.add(topPanel, BorderLayout.NORTH);

        // Table container
        tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        card.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);

        refreshTable();
    }

    private void refreshTable() {
        tableContainer.removeAll();

        // Header
        JPanel header = new JPanel(new GridLayout(1, 7, 5, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        header.add(createHeaderLabel("Job ID"));
        header.add(createHeaderLabel("Module Code"));
        header.add(createHeaderLabel("Module Name"));
        header.add(createHeaderLabel("MO Email"));
        header.add(createHeaderLabel("Weekly Hours"));
        header.add(createHeaderLabel("Deadline"));
        header.add(createHeaderLabel("Status"));
        tableContainer.add(header);

        String filter = (String) statusFilter.getSelectedItem();
        List<Job> filtered = new ArrayList<>();
        for (Job job : allJobs) {
            if ("All".equals(filter)) {
                filtered.add(job);
            } else if ("Open".equals(filter) && "OPEN".equals(job.getStatus())) {
                filtered.add(job);
            } else if ("Closed".equals(filter) && "CLOSED".equals(job.getStatus())) {
                filtered.add(job);
            }
        }

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("No positions found.", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableContainer.add(empty);
        } else {
            for (Job job : filtered) {
                JPanel row = new JPanel(new GridLayout(1, 7, 5, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                row.add(createCellLabel(job.getJobId()));
                row.add(createCellLabel(job.getModuleCode()));
                row.add(createCellLabel(job.getModuleName()));
                row.add(createCellLabel(job.getMoEmail()));
                row.add(createCellLabel(String.valueOf(job.getWeeklyHours())));
                row.add(createCellLabel(job.getDeadline()));

                String statusText = "OPEN".equals(job.getStatus()) ? "Open" : "Closed";
                Color statusColor = "OPEN".equals(job.getStatus()) ? UIHelper.SUCCESS_COLOR : UIHelper.DISABLED_COLOR;
                JLabel statusLabel = createCellLabel(statusText);
                statusLabel.setForeground(statusColor);
                row.add(statusLabel);

                tableContainer.add(row);
            }
        }

        tableContainer.revalidate();
        tableContainer.repaint();
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
}
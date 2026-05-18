package system.admin;

import system.*;
import system.ta.TAProfileViewFrame;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Frame for administrators to view all Teaching Assistants with filtering capabilities.
 * <p>
 * This frame displays a table of all TAs in the system with options to filter
 * by major and grade. Administrators can click on a TA to view their profile.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class AdminViewAllTAsFrame extends JFrame {

    /** List of all users in the system */
    private final List<User> users;

    /** List of all TA profiles */
    private final List<Profile> allProfiles;

    /** Container for the table */
    private JPanel tableContainer;

    /** Search input field */
    private JTextField searchField;

    /** Major filter combo box */
    private JComboBox<String> majorFilter;

    /** Grade filter combo box */
    private JComboBox<String> gradeFilter;

    /** Label showing count of displayed TAs */
    private JLabel countLabel;

    /**
     * Constructs the frame to view all TAs.
     *
     * @param users the list of all users
     */
    public AdminViewAllTAsFrame(List<User> users) {
        this.users = users;
        this.allProfiles = FileUtil.loadProfiles();

        setTitle("All Teaching Assistants");
        setSize(900, 550);
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

        // North area: title and filter panel
        JPanel northArea = new JPanel();
        northArea.setLayout(new BoxLayout(northArea, BoxLayout.Y_AXIS));
        northArea.setBackground(Color.WHITE);

        JLabel title = UIHelper.createTitle("All Teaching Assistants");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        northArea.add(title);
        northArea.add(Box.createRigidArea(new Dimension(0, 15)));

        // Filter panel - left aligned with table content
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search
        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField(12);
        searchField.setPreferredSize(new Dimension(120, 28));
        filterPanel.add(searchField);

        // Major filter
        filterPanel.add(new JLabel("Major:"));
        majorFilter = new JComboBox<>(new String[]{"All", "Computer Science", "Software Engineering",
                "Information Technology", "Data Science", "Artificial Intelligence", "Other"});
        majorFilter.setPreferredSize(new Dimension(130, 28));
        filterPanel.add(majorFilter);

        // Grade filter
        filterPanel.add(new JLabel("Grade:"));
        gradeFilter = new JComboBox<>(new String[]{"All", "1st Year", "2nd Year", "3rd Year",
                "4th Year", "Graduate"});
        gradeFilter.setPreferredSize(new Dimension(100, 28));
        filterPanel.add(gradeFilter);

        // Buttons
        JButton searchBtn = new JButton("Search");
        styleFilterButton(searchBtn, UIHelper.PRIMARY_COLOR);
        filterPanel.add(searchBtn);

        JButton resetBtn = new JButton("Reset");
        styleFilterButton(resetBtn, new Color(150, 150, 150));
        filterPanel.add(resetBtn);

        northArea.add(filterPanel);
        card.add(northArea, BorderLayout.NORTH);

        // Table container
        tableContainer = new JPanel();
        tableContainer.setLayout(new BoxLayout(tableContainer, BoxLayout.Y_AXIS));
        tableContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(tableContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        card.add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with count
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBackground(new Color(250, 250, 250));
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        countLabel = new JLabel();
        countLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        countLabel.setForeground(new Color(100, 100, 100));
        bottomPanel.add(countLabel);
        card.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);

        // Setup actions
        searchBtn.addActionListener(e -> refreshTable());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            majorFilter.setSelectedIndex(0);
            gradeFilter.setSelectedIndex(0);
            refreshTable();
        });

        refreshTable();
    }

    private void styleFilterButton(JButton btn, Color bg) {
        btn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 28));
    }

    private void refreshTable() {
        tableContainer.removeAll();

        // Header
        JPanel header = new JPanel(new GridLayout(1, 5, 5, 0));
        header.setBackground(new Color(240, 240, 240));
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        header.add(createHeaderLabel("Name"));
        header.add(createHeaderLabel("Student ID"));
        header.add(createHeaderLabel("Major"));
        header.add(createHeaderLabel("Grade"));
        header.add(createHeaderLabel("Action"));
        tableContainer.add(header);

        String searchText = searchField.getText().trim().toLowerCase();
        String major = (String) majorFilter.getSelectedItem();
        String grade = (String) gradeFilter.getSelectedItem();

        Map<String, Profile> profileMap = new HashMap<>();
        for (Profile p : allProfiles) {
            profileMap.put(p.getEmail(), p);
        }

        List<User> taUsers = new ArrayList<>();
        for (User u : users) {
            if ("TA".equals(u.getRole())) {
                taUsers.add(u);
            }
        }

        List<User> filtered = new ArrayList<>();
        for (User u : taUsers) {
            Profile p = profileMap.get(u.getEmail());

            if (!searchText.isEmpty() &&
                    !u.getName().toLowerCase().contains(searchText) &&
                    !u.getEmail().toLowerCase().contains(searchText)) {
                continue;
            }
            if (!"All".equals(major) && (p == null || !major.equals(p.getMajor()))) {
                continue;
            }
            if (!"All".equals(grade) && (p == null || !grade.equals(p.getGrade()))) {
                continue;
            }
            filtered.add(u);
        }

        if (filtered.isEmpty()) {
            JLabel empty = new JLabel("No TAs found", SwingConstants.CENTER);
            empty.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            empty.setForeground(Color.GRAY);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            tableContainer.add(empty);
        } else {
            for (User u : filtered) {
                Profile p = profileMap.get(u.getEmail());

                JPanel row = new JPanel(new GridLayout(1, 5, 5, 0));
                row.setBackground(Color.WHITE);
                row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                row.add(createCellLabel(u.getName()));
                row.add(createCellLabel(p != null ? p.getStudentId() : "-"));
                row.add(createCellLabel(p != null ? p.getMajor() : "-"));
                row.add(createCellLabel(p != null ? p.getGrade() : "-"));

                JButton viewBtn = new JButton("Details");
                viewBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
                viewBtn.setBackground(UIHelper.PRIMARY_COLOR);
                viewBtn.setForeground(Color.WHITE);
                viewBtn.setFocusPainted(false);
                viewBtn.setBorderPainted(false);
                viewBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                viewBtn.addActionListener(e -> {
                    if (p != null) {
                        new TAProfileViewFrame(u);
                    } else {
                        UIHelper.showInfoDialog(this, "No profile data available.", "Info");
                    }
                });

                JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
                btnPanel.setBackground(Color.WHITE);
                btnPanel.add(viewBtn);
                row.add(btnPanel);

                tableContainer.add(row);
            }
        }

        countLabel.setText("Total: " + filtered.size() + " TA(s)");
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
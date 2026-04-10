package system.admin;

import system.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

/**
 * Frame for Admin to view system logs.
 */
public class AdminViewLogsFrame extends JFrame {

    public AdminViewLogsFrame() {
        setTitle("System Logs");
        setSize(800, 500);
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

        JLabel title = UIHelper.createTitle("System Logs");
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        card.add(title, BorderLayout.NORTH);

        JTextArea logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setEditable(false);
        loadLogs(logArea);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton refreshBtn = UIHelper.createButton("Refresh", UIHelper.PRIMARY_COLOR);
        refreshBtn.addActionListener(e -> loadLogs(logArea));
        buttonPanel.add(refreshBtn);
        card.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(card, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void loadLogs(JTextArea logArea) {
        try {
            File logFile = new File("logs/app.log");
            if (logFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(logFile));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                logArea.setText(content.toString());
            } else {
                logArea.setText("No logs available yet.");
            }
        } catch (IOException e) {
            logArea.setText("Failed to load logs: " + e.getMessage());
            LoggerUtil.logError("View Logs", "Failed to read log file: " + e.getMessage());
        }
    }
}
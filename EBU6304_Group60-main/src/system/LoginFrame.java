package system;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private List<User> users;

    public LoginFrame() {
        setTitle("TA Recruitment System");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(UIHelper.BACKGROUND_COLOR);

        users = FileUtil.loadUsers();

        // Create default admin if no users exist
        if (users.isEmpty()) {
            User admin = new User("admin@bupt.edu", "admin123", "Admin", "System Administrator");
            users.add(admin);
            FileUtil.saveUsers(users);
        }

        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Card panel
        JPanel cardPanel = UIHelper.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(10, 10, 10, 10);

        int y = 0;

        // Logo area
        JLabel logoLabel = new JLabel("📚");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        cardGbc.gridy = y++;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(logoLabel, cardGbc);

        // Title
        JLabel titleLabel = UIHelper.createTitle("TA Recruitment System");
        cardGbc.gridy = y++;
        cardPanel.add(titleLabel, cardGbc);

        cardGbc.gridwidth = 1;

        // Email
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardPanel.add(emailLabel, cardGbc);

        emailField = UIHelper.createTextField();
        emailField.setPreferredSize(new Dimension(250, 40));
        cardGbc.gridx = 1;
        cardPanel.add(emailField, cardGbc);
        y++;

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardPanel.add(passwordLabel, cardGbc);

        passwordField = UIHelper.createPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        cardGbc.gridx = 1;
        cardPanel.add(passwordField, cardGbc);
        y++;

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));

        JButton loginBtn = UIHelper.createButton("Login", UIHelper.PRIMARY_COLOR);
        JButton registerBtn = UIHelper.createButton("Register", UIHelper.SECONDARY_COLOR);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(buttonPanel, cardGbc);
        y++;

        // Login event
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame(users);
        });

        // Add card to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(cardPanel, gbc);

        add(mainPanel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new DashboardFrame(user, users);
                return;
            }
        }
        LoggerUtil.logError("Login Failed", "Invalid credentials for email: " + email);
        JOptionPane.showMessageDialog(this, "Invalid email or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new LoginFrame();
    }
}
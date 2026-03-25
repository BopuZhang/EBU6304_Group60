package system;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegisterFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nameField;
    private JComboBox<String> roleCombo;
    private List<User> users;

    public RegisterFrame(List<User> users) {
        this.users = users;

        setTitle("Register - TA Recruitment System");
        setSize(450, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        JPanel cardPanel = new JPanel();
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        cardPanel.setLayout(new GridBagLayout());

        GridBagConstraints cardGbc = new GridBagConstraints();
        cardGbc.insets = new Insets(8, 8, 8, 8);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(79, 114, 139));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardGbc.gridy = row++;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardPanel.add(titleLabel, cardGbc);

        cardGbc.gridwidth = 1;
        cardGbc.anchor = GridBagConstraints.WEST;

        // Email
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(emailLabel, cardGbc);

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setPreferredSize(new Dimension(250, 35));
        cardGbc.gridx = 1;
        cardPanel.add(emailField, cardGbc);
        row++;

        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(passwordLabel, cardGbc);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(250, 35));
        cardGbc.gridx = 1;
        cardPanel.add(passwordField, cardGbc);
        row++;

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(confirmLabel, cardGbc);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmPasswordField.setPreferredSize(new Dimension(250, 35));
        cardGbc.gridx = 1;
        cardPanel.add(confirmPasswordField, cardGbc);
        row++;

        // Full Name
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(nameLabel, cardGbc);

        nameField = new JTextField();
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameField.setPreferredSize(new Dimension(250, 35));
        cardGbc.gridx = 1;
        cardPanel.add(nameField, cardGbc);
        row++;

        // Role
        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(roleLabel, cardGbc);

        roleCombo = new JComboBox<>(new String[]{"TA", "MO", "Admin"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.setPreferredSize(new Dimension(250, 35));
        cardGbc.gridx = 1;
        cardPanel.add(roleCombo, cardGbc);
        row++;

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));

        JButton registerBtn = new JButton("Register");
        registerBtn.setBackground(new Color(76, 175, 80));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setOpaque(true);
        registerBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton backBtn = new JButton("Back to Login");
        backBtn.setBackground(new Color(96, 125, 139));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setOpaque(true);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        registerBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(56, 155, 60));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                registerBtn.setBackground(new Color(76, 175, 80));
            }
        });

        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(76, 105, 119));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backBtn.setBackground(new Color(96, 125, 139));
            }
        });

        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);

        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(buttonPanel, cardGbc);

        // Register event
        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(cardPanel, gbc);

        add(mainPanel);
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Email regex pattern: username@domain.com
        // Must contain @ and . after @
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String name = nameField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        // Validation - Empty fields
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Email format validation
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g., name@domain.com)", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Password match validation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Password length validation
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if email already exists
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                JOptionPane.showMessageDialog(this, "Email already registered", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Create new user
        User newUser = new User(email, password, role, name);
        users.add(newUser);
        FileUtil.saveUsers(users);

        // Log successful registration
        LoggerUtil.logRegistration(email, role);
        LoggerUtil.logCreate("User", email + " (" + role + ")");

        JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginFrame();
    }
}
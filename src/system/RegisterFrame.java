package system;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Registration window for the TA Recruitment System.
 * Allows a user to create a new account.
 */
public class RegisterFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JToggleButton passwordToggleBtn;
    private JToggleButton confirmToggleBtn;
    private JTextField nameField;
    private JComboBox<String> roleCombo;
    private List<User> users;

    /**
     * Constructs the registration frame.
     *
     * @param users the list of existing users (passed from the main application)
     */
    public RegisterFrame(List<User> users) {
        this.users = users;

        setTitle("Register - TA Recruitment System");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        setVisible(true);
    }

    /**
     * Creates a password field with an eye toggle button.
     *
     * @param field   the password field to be decorated
     * @param toggle  the toggle button that controls visibility
     * @return a panel containing the password field and the toggle button
     */
    private JPanel createPasswordPanel(JPasswordField field, JToggleButton toggle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 35));
        field.setEchoChar('●');  // bullet character

        // Set a font that supports emojis (adjust as needed for your OS)
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 16);
        toggle.setFont(emojiFont);
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setContentAreaFilled(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Define emoji for open and closed states
        final String OPEN_EYE = "👁️";      // open eye
        final String CLOSED_EYE = "\uD83D\uDE48";  // close eye

        // Initially set closed eye (password hidden)
        toggle.setText(CLOSED_EYE);
        toggle.setSelected(false);

        toggle.addActionListener(e -> {
            boolean show = toggle.isSelected();
            if (show) {
                toggle.setText(OPEN_EYE);   // show open eye
                field.setEchoChar((char) 0); // display plain text
            } else {
                toggle.setText(CLOSED_EYE); // show closed eye
                field.setEchoChar('●');      // mask with bullet
            }
        });

        panel.add(field, BorderLayout.CENTER);
        panel.add(toggle, BorderLayout.EAST);
        return panel;
    }

    /**
     * Initializes the user interface components.
     */
    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Card panel with white background and padding
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
        titleLabel.setForeground(UIHelper.getAccentColor());
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardGbc.gridy = row++;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardPanel.add(titleLabel, cardGbc);

        cardGbc.gridwidth = 1;
        cardGbc.anchor = GridBagConstraints.WEST;

        // Email field
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

        // Password field with eye toggle
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(passwordLabel, cardGbc);

        passwordField = new JPasswordField();
        passwordToggleBtn = new JToggleButton();
        JPanel passwordPanel = createPasswordPanel(passwordField, passwordToggleBtn);
        cardGbc.gridx = 1;
        cardPanel.add(passwordPanel, cardGbc);
        row++;

        // Confirm Password field with eye toggle
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(confirmLabel, cardGbc);

        confirmPasswordField = new JPasswordField();
        confirmToggleBtn = new JToggleButton();
        JPanel confirmPanel = createPasswordPanel(confirmPasswordField, confirmToggleBtn);
        cardGbc.gridx = 1;
        cardPanel.add(confirmPanel, cardGbc);
        row++;

        // Full Name field
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

        // Role selection
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

        // Button panel with Register and Back buttons
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

        // Hover effects
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

        // Add button panel to the card
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(buttonPanel, cardGbc);

        // Register button action
        registerBtn.addActionListener(e -> register());
        // Back button action: close this frame and open login frame
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        // Place the card panel into the main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(cardPanel, gbc);

        add(mainPanel);
    }

    /**
     * Validates the email format using a regular expression.
     *
     * @param email the email string to validate
     * @return true if the email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        // Email regex: username@domain.tld
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Handles the registration process: validates input, creates a new user,
     * saves to file, logs the action, and opens the login frame.
     */
    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String name = nameField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        // Check for empty fields
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g., name@domain.com)", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate password length
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check for duplicate email
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                JOptionPane.showMessageDialog(this, "Email already registered", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Create and add the new user
        User newUser = new User(email, password, role, name);
        users.add(newUser);
        FileUtil.saveUsers(users);

        // Log the registration
        LoggerUtil.logRegistration(email, role);
        LoggerUtil.logCreate("User", email + " (" + role + ")");

        JOptionPane.showMessageDialog(this, "Registration successful! Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new LoginFrame();
    }
}
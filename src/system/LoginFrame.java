package system;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JToggleButton passwordToggleBtn;
    private List<User> users;

    public LoginFrame() {
        setTitle("TA Recruitment System");
        setSize(600, 550);
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

    /**
     * Creates a password field with a toggle button that shows/hides the password.
     * The toggle uses emojis: open eye for visible, closed eye for hidden.
     *
     * @return a panel containing the styled password field and toggle button
     */
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        // Use UIHelper's styled password field
        passwordField = UIHelper.createPasswordField();
        passwordField.setPreferredSize(new Dimension(250, 40));
        passwordField.setEchoChar('●');

        passwordToggleBtn = new JToggleButton();

        // Use emoji font; JVM will fallback automatically if not available
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 16);
        passwordToggleBtn.setFont(emojiFont);
        passwordToggleBtn.setFocusPainted(false);
        passwordToggleBtn.setBorderPainted(false);
        passwordToggleBtn.setContentAreaFilled(false);
        passwordToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final String OPEN_EYE = "👁️";
        final String CLOSED_EYE = "\uD83D\uDE48";  // "see-no-evil" monkey

        passwordToggleBtn.setText(CLOSED_EYE);
        passwordToggleBtn.setSelected(false);

        passwordToggleBtn.addActionListener(e -> {
            if (passwordToggleBtn.isSelected()) {
                passwordToggleBtn.setText(OPEN_EYE);
                passwordField.setEchoChar((char) 0);
            } else {
                passwordToggleBtn.setText(CLOSED_EYE);
                passwordField.setEchoChar('●');
            }
        });

        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(passwordToggleBtn, BorderLayout.EAST);
        return panel;
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

        // Logo
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
        cardGbc.anchor = GridBagConstraints.WEST;

        // Email label
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardPanel.add(emailLabel, cardGbc);

        // Email field
        emailField = UIHelper.createTextField();
        emailField.setPreferredSize(new Dimension(250, 40));
        cardGbc.gridx = 1;
        cardPanel.add(emailField, cardGbc);
        y++;

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardPanel.add(passwordLabel, cardGbc);

        // Password panel with toggle
        JPanel passwordPanel = createPasswordPanel();
        cardGbc.gridx = 1;
        cardPanel.add(passwordPanel, cardGbc);
        y++;

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));

        JButton loginBtn = UIHelper.createPrimaryButton("Login");
        JButton registerBtn = UIHelper.createButton("Register", UIHelper.SECONDARY_COLOR);

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);

        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(buttonPanel, cardGbc);
        y++;

        // Forgot Password link
        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        forgotPasswordBtn.setForeground(UIHelper.PRIMARY_COLOR);
        forgotPasswordBtn.setBorderPainted(false);
        forgotPasswordBtn.setContentAreaFilled(false);
        forgotPasswordBtn.setFocusPainted(false);
        forgotPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover underline effect
        forgotPasswordBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordBtn.setText("<html><u>Forgot Password?</u></html>");
                forgotPasswordBtn.setForeground(UIHelper.PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordBtn.setText("Forgot Password?");
                forgotPasswordBtn.setForeground(UIHelper.PRIMARY_COLOR);
            }
        });

        // Show styled forgot password dialog
        forgotPasswordBtn.addActionListener(e -> showForgotPasswordDialog());

        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.anchor = GridBagConstraints.CENTER;
        cardGbc.fill = GridBagConstraints.NONE;
        cardPanel.add(forgotPasswordBtn, cardGbc);
        y++;

        // Events
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

    /**
     * Handles the "Forgot Password" flow with email and verification code.
     */
    private void showForgotPasswordDialog() {
        // Step 1: Email input panel (styled with UIHelper)
        JPanel emailPanel = new JPanel(new BorderLayout(10, 10));
        emailPanel.setBackground(UIHelper.CARD_COLOR);
        emailPanel.add(new JLabel("Enter your email address:"), BorderLayout.NORTH);
        JTextField emailInput = UIHelper.createTextField();
        emailInput.setPreferredSize(new Dimension(250, 35));
        emailPanel.add(emailInput, BorderLayout.CENTER);

        int emailResult = UIHelper.showConfirmDialog(this, emailPanel, "Forgot Password", JOptionPane.OK_CANCEL_OPTION);
        if (emailResult != JOptionPane.OK_OPTION) return;

        String email = emailInput.getText().trim();
        if (email.isEmpty()) {
            UIHelper.showWarningDialog(this, "Email cannot be empty.", "Warning");
            return;
        }

        // Find user
        User targetUser = null;
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                targetUser = u;
                break;
            }
        }
        if (targetUser == null) {
            UIHelper.showErrorDialog(this, "No account found with that email.", "Error");
            return;
        }

        // Step 2: Verification code panel (fixed to "12345")
        JPanel codePanel = new JPanel(new BorderLayout(10, 10));
        codePanel.setBackground(UIHelper.CARD_COLOR);
        codePanel.add(new JLabel("Verification code (simulation: 12345):"), BorderLayout.NORTH);
        JTextField codeInput = UIHelper.createTextField();
        codeInput.setPreferredSize(new Dimension(250, 35));
        codePanel.add(codeInput, BorderLayout.CENTER);

        int codeResult = UIHelper.showConfirmDialog(this, codePanel, "Verification Code", JOptionPane.OK_CANCEL_OPTION);
        if (codeResult != JOptionPane.OK_OPTION) return;

        if (!"12345".equals(codeInput.getText().trim())) {
            UIHelper.showErrorDialog(this, "Invalid verification code.", "Error");
            return;
        }

        // Step 3: Password reset dialog with real-time validation
        showPasswordResetDialog(targetUser);
    }

    /**
     * Custom dialog for resetting password with real-time validation (similar to RegisterFrame).
     */
    private void showPasswordResetDialog(User user) {
        JDialog dialog = new JDialog(this, "Reset Password", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setBackground(UIHelper.BACKGROUND_COLOR);

        // Main container with scroll (optional, but keeps consistency)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);

        JPanel cardPanel = UIHelper.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Title
        JLabel titleLabel = new JLabel("Set New Password");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titleLabel.setForeground(UIHelper.PRIMARY_COLOR);
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        cardPanel.add(titleLabel, gbc);
        gbc.gridwidth = 1;

        // New Password field
        JLabel newPassLabel = new JLabel("New Password:");
        newPassLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridy = row;
        gbc.gridx = 0;
        cardPanel.add(newPassLabel, gbc);

        JPasswordField newPassField = UIHelper.createPasswordField();
        newPassField.setPreferredSize(new Dimension(250, 40));
        newPassField.setEchoChar('●');
        gbc.gridx = 1;
        cardPanel.add(newPassField, gbc);
        row++;

        JLabel newPassError = new JLabel(" ");
        newPassError.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        newPassError.setForeground(new Color(220, 53, 69));
        gbc.gridy = row++;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 15, 5, 15);
        cardPanel.add(newPassError, gbc);
        gbc.insets = new Insets(10, 15, 10, 15);

        // Confirm Password field
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        gbc.gridy = row;
        gbc.gridx = 0;
        cardPanel.add(confirmLabel, gbc);

        JPasswordField confirmPassField = UIHelper.createPasswordField();
        confirmPassField.setPreferredSize(new Dimension(250, 40));
        confirmPassField.setEchoChar('●');
        gbc.gridx = 1;
        cardPanel.add(confirmPassField, gbc);
        row++;

        JLabel confirmError = new JLabel(" ");
        confirmError.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        confirmError.setForeground(new Color(220, 53, 69));
        gbc.gridy = row++;
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 15, 5, 15);
        cardPanel.add(confirmError, gbc);
        gbc.insets = new Insets(10, 15, 10, 15);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton resetBtn = UIHelper.createPrimaryButton("Reset Password");
        JButton cancelBtn = UIHelper.createButton("Cancel", UIHelper.SECONDARY_COLOR);
        resetBtn.setEnabled(false);
        buttonPanel.add(resetBtn);
        buttonPanel.add(cancelBtn);

        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 15, 15, 15);
        cardPanel.add(buttonPanel, gbc);

        mainPanel.add(cardPanel, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Real-time validation listener
        DocumentListener validationListener = new DocumentListener() {
            private void validate() {
                String newPass = new String(newPassField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());

                boolean passValid = true;
                boolean confirmValid = true;

                if (newPass.isEmpty()) {
                    newPassError.setText("Password is required");
                    passValid = false;
                } else if (newPass.length() < 8) {
                    newPassError.setText("Password must be at least 8 characters");
                    passValid = false;
                } else {
                    newPassError.setText(" ");
                }

                if (confirmPass.isEmpty()) {
                    confirmError.setText("Please confirm your password");
                    confirmValid = false;
                } else if (!newPass.equals(confirmPass)) {
                    confirmError.setText("Passwords do not match");
                    confirmValid = false;
                } else {
                    confirmError.setText(" ");
                }

                resetBtn.setEnabled(passValid && confirmValid && newPass.equals(confirmPass));
            }

            @Override public void insertUpdate(DocumentEvent e) { validate(); }
            @Override public void removeUpdate(DocumentEvent e) { validate(); }
            @Override public void changedUpdate(DocumentEvent e) { validate(); }
        };

        newPassField.getDocument().addDocumentListener(validationListener);
        confirmPassField.getDocument().addDocumentListener(validationListener);

        resetBtn.addActionListener(e -> {
            user.setPassword(new String(newPassField.getPassword()));
            FileUtil.saveUsers(users);
            UIHelper.showInfoDialog(dialog, "Password has been reset successfully! Please login with your new password.", "Success");
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            UIHelper.showWarningDialog(this, "Please enter email and password", "Warning");
            return;
        }

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                UIHelper.showInfoDialog(this, "Login successful! Welcome " + user.getName(), "Success");
                dispose();
                new DashboardFrame(user, users);
                return;
            }
        }
        LoggerUtil.logError("Login Failed", "Invalid credentials for email: " + email);
        UIHelper.showErrorDialog(this, "Invalid email or password", "Login Failed");
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        new LoginFrame();
    }
}
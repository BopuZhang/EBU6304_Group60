package system;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;

/**
 * The login frame for the TA Recruitment System.
 * <p>
 * This frame provides user authentication with email and password fields.
 * On first run, a default admin account is created.
 * After successful login, users are redirected to the appropriate dashboard
 * based on their role (TA, MO, or Admin).
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2026
 */
public class LoginFrame extends JFrame {

    /** Email input field */
    private JTextField emailField;

    /** Password input field */
    private JPasswordField passwordField;

    /** Toggle button to show/hide password */
    private JToggleButton passwordToggleBtn;

    /** List of registered users */
    private List<User> users;

    /**
     * Constructs the login frame and initializes the UI.
     * <p>
     * Creates a default admin account if no users exist.
     * </p>
     */
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
     * Creates a password field with a toggle button to show/hide the password.
     * <p>
     * The toggle uses emoji icons: open eye for visible, monkey for hidden.
     * </p>
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
        final String CLOSED_EYE = "\uD83D\uDE48"; // "see-no-evil" monkey

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

    /**
     * Initializes the user interface components.
     */
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

        forgotPasswordBtn.addActionListener(e -> {
            UIHelper.showInfoDialog(this, "Feature coming soon!", "Info");
        });

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
     * Handles the login process.
     * <p>
     * Validates the entered credentials and redirects to the appropriate
     * dashboard on success, or shows an error message on failure.
     * </p>
     */
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

    /**
     * Application entry point.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.ENGLISH);
        new LoginFrame();
    }
}
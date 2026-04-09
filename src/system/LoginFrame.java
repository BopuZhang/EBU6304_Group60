package system;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import com.formdev.flatlaf.FlatLightLaf;

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
     * @param field  the password field to be decorated
     * @param toggle the toggle button that controls visibility
     * @return a panel containing the password field and the toggle button
     */
    private JPanel createPasswordPanel(JPasswordField field, JToggleButton toggle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(250, 40));
        field.setEchoChar('●'); // bullet character for masking

        // Use a font that supports emojis (adjust for your OS if needed)
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 16);
        toggle.setFont(emojiFont);
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setContentAreaFilled(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Emoji states
        final String OPEN_EYE = "👁️";      // open eye
        final String CLOSED_EYE = "\uD83D\uDE48";  // "see-no-evil" monkey (closed eye)

        // Initial state: password hidden
        toggle.setText(CLOSED_EYE);
        toggle.setSelected(false);

        toggle.addActionListener(e -> {
            boolean show = toggle.isSelected();
            if (show) {
                toggle.setText(OPEN_EYE);   // open eye -> visible
                field.setEchoChar((char) 0); // display plain text
            } else {
                toggle.setText(CLOSED_EYE); // closed eye -> hidden
                field.setEchoChar('●');      // mask with bullet
            }
        });

        panel.add(field, BorderLayout.CENTER);
        panel.add(toggle, BorderLayout.EAST);
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
        cardGbc.anchor = GridBagConstraints.WEST;

        // Email (unchanged)
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

        // Password with toggle
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardPanel.add(passwordLabel, cardGbc);

        passwordField = new JPasswordField();
        passwordToggleBtn = new JToggleButton();
        JPanel passwordPanel = createPasswordPanel(passwordField, passwordToggleBtn);
        cardGbc.gridx = 1;
        cardPanel.add(passwordPanel, cardGbc);
        y++;

        // Button panel (Login & Register)
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

        // Forgot Password button (no functionality yet)
        JButton forgotPasswordBtn = new JButton("Forgot Password?");
        forgotPasswordBtn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        forgotPasswordBtn.setForeground(new Color(79, 114, 139));
        forgotPasswordBtn.setBorderPainted(false);
        forgotPasswordBtn.setContentAreaFilled(false);
        forgotPasswordBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordBtn.addActionListener(e -> {
            // TODO: Implement forgot password functionality later
            JOptionPane.showMessageDialog(this, "Feature coming soon!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        cardGbc.gridy = y;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.anchor = GridBagConstraints.CENTER;
        cardGbc.fill = GridBagConstraints.NONE;
        cardPanel.add(forgotPasswordBtn, cardGbc);
        y++;

        // Login event (unchanged)
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

        // 使用 FlatLaf 现代主题
        try {
            FlatLightLaf.setup();

            // 自定义 UI 属性
            UIManager.put("Button.arc", 10);           // 按钮圆角
            UIManager.put("Component.arc", 10);        // 组件圆角
            UIManager.put("TextComponent.arc", 10);    // 输入框圆角
            UIManager.put("Table.showHorizontalLines", false);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.rowHeight", 40);      // 默认行高

        } catch (Exception e) {
            e.printStackTrace();
            // 如果 FlatLaf 失败，回退到系统外观
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        new LoginFrame();
    }
}
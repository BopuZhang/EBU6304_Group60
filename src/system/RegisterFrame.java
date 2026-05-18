package system;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.*;
import java.util.List;

/**
 * The registration frame for new users.
 * <p>
 * This frame allows new users to create an account by providing
 * email, password, name, and selecting a role (TA, MO, or Admin).
 * Input validation is performed in real-time with visual feedback.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class RegisterFrame extends JFrame {

    /** Email input field */
    private JTextField emailField;

    /** Password input field */
    private JPasswordField passwordField;

    /** Confirm password input field */
    private JPasswordField confirmPasswordField;

    /** Toggle button to show/hide password */
    private JToggleButton passwordToggleBtn;

    /** Toggle button to show/hide confirm password */
    private JToggleButton confirmToggleBtn;

    /** Name input field */
    private JTextField nameField;

    /** Role selection combo box */
    private JComboBox<String> roleCombo;

    /** List of existing users */
    private List<User> users;

    /** Error label for email validation */
    private JLabel emailErrorLabel;

    /** Error label for password validation */
    private JLabel passwordErrorLabel;

    /** Error label for confirm password validation */
    private JLabel confirmErrorLabel;

    /** Error label for name validation */
    private JLabel nameErrorLabel;

    /** Flag indicating if email is valid */
    private boolean emailValid = false;

    /** Flag indicating if password is valid */
    private boolean passwordValid = false;

    /** Flag indicating if confirm password matches */
    private boolean confirmValid = false;

    /** Flag indicating if name is valid */
    private boolean nameValid = false;

    /**
     * Constructs the registration frame.
     *
     * @param users the list of existing users to check for duplicates
     */
    public RegisterFrame(List<User> users) {
        this.users = users;

        setTitle("Register - TA Recruitment System");
        setSize(700, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBackground(UIHelper.BACKGROUND_COLOR);

        initUI();
        setVisible(true);
    }

    /**
     * Creates a password panel with a toggle button to show/hide the password.
     *
     * @param isConfirm true if this is the confirm password field
     * @return a panel containing the password field and toggle button
     */
    private JPanel createPasswordPanel(boolean isConfirm) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPasswordField field = UIHelper.createPasswordField();
        field.setPreferredSize(new Dimension(280, 45));
        field.setEchoChar('●');

        JToggleButton toggle = new JToggleButton();
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 18);
        toggle.setFont(emojiFont);
        toggle.setFocusPainted(false);
        toggle.setBorderPainted(false);
        toggle.setContentAreaFilled(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final String OPEN_EYE = "👁️";
        final String CLOSED_EYE = "\uD83D\uDE48";

        toggle.setText(CLOSED_EYE);
        toggle.setSelected(false);

        toggle.addActionListener(e -> {
            if (toggle.isSelected()) {
                toggle.setText(OPEN_EYE);
                field.setEchoChar((char) 0);
            } else {
                toggle.setText(CLOSED_EYE);
                field.setEchoChar('●');
            }
        });

        panel.add(field, BorderLayout.CENTER);
        panel.add(toggle, BorderLayout.EAST);

        if (isConfirm) {
            confirmPasswordField = field;
            confirmToggleBtn = toggle;
        } else {
            passwordField = field;
            passwordToggleBtn = toggle;
        }

        return panel;
    }

    private JLabel createErrorLabel() {
        JLabel label = new JLabel(" ");
        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12)); // 增大错误提示字体
        label.setForeground(new Color(220, 53, 69));
        return label;
    }

    /**
     * Updates the register button's enabled state based on validation flags.
     *
     * @param registerBtn the register button to update
     */
    private void updateRegisterButtonState(JButton registerBtn) {
        registerBtn.setEnabled(emailValid && passwordValid && confirmValid && nameValid);
    }

    /**
     * Sets up real-time email validation.
     *
     * @param registerBtn the register button to update on validation
     */
    private void setupEmailValidation(JButton registerBtn) {
        emailField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String email = emailField.getText().trim();
                if (email.isEmpty()) {
                    emailErrorLabel.setText("Email is required");
                    emailValid = false;
                } else if (!isValidEmail(email)) {
                    emailErrorLabel.setText("Invalid email format (e.g., name@domain.com)");
                    emailValid = false;
                } else {
                    boolean duplicate = users.stream().anyMatch(u -> u.getEmail().equals(email));
                    if (duplicate) {
                        emailErrorLabel.setText("Email already registered");
                        emailValid = false;
                    } else {
                        emailErrorLabel.setText(" ");
                        emailValid = true;
                    }
                }
                updateRegisterButtonState(registerBtn);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate();
            }
        });
    }

    /**
     * Sets up real-time password validation.
     *
     * @param registerBtn the register button to update on validation
     */
    private void setupPasswordValidation(JButton registerBtn) {
        DocumentListener listener = new DocumentListener() {
            private void validate() {
                String password = new String(passwordField.getPassword());
                if (password.isEmpty()) {
                    passwordErrorLabel.setText("Password is required");
                    passwordValid = false;
                } else if (password.length() < 8) {
                    passwordErrorLabel.setText("Password must be at least 8 characters");
                    passwordValid = false;
                } else {
                    passwordErrorLabel.setText(" ");
                    passwordValid = true;
                }
                validateConfirmPassword();
                updateRegisterButtonState(registerBtn);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate();
            }
        };
        passwordField.getDocument().addDocumentListener(listener);
    }

    /**
     * Sets up real-time confirm password validation.
     *
     * @param registerBtn the register button to update on validation
     */
    private void setupConfirmPasswordValidation(JButton registerBtn) {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                validateConfirmPassword();
                updateRegisterButtonState(registerBtn);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validateConfirmPassword();
                updateRegisterButtonState(registerBtn);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validateConfirmPassword();
                updateRegisterButtonState(registerBtn);
            }
        };
        confirmPasswordField.getDocument().addDocumentListener(listener);
    }

    /**
     * Validates that the confirm password matches the password.
     */
    private void validateConfirmPassword() {
        String password = new String(passwordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());
        if (confirm.isEmpty()) {
            confirmErrorLabel.setText("Please confirm your password");
            confirmValid = false;
        } else if (!password.equals(confirm)) {
            confirmErrorLabel.setText("Passwords do not match");
            confirmValid = false;
        } else {
            confirmErrorLabel.setText(" ");
            confirmValid = true;
        }
    }

    /**
     * Sets up real-time name validation.
     *
     * @param registerBtn the register button to update on validation
     */
    private void setupNameValidation(JButton registerBtn) {
        nameField.getDocument().addDocumentListener(new DocumentListener() {
            private void validate() {
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    nameErrorLabel.setText("Full name is required");
                    nameValid = false;
                } else {
                    nameErrorLabel.setText(" ");
                    nameValid = true;
                }
                updateRegisterButtonState(registerBtn);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                validate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                validate();
            }
        });
    }

    /**
     * Validates the email format using a regular expression.
     *
     * @param email the email to validate
     * @return true if the email is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty())
            return false;
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    /**
     * Initializes the user interface components.
     */
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIHelper.BACKGROUND_COLOR);

        JPanel cardPanel = UIHelper.createCardPanel();
        cardPanel.setLayout(new GridBagLayout());

        GridBagConstraints cardGbc = new GridBagConstraints();

        cardGbc.insets = new Insets(8, 15, 8, 15);
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardGbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        JLabel titleLabel = UIHelper.createTitle("Create Account");
        cardGbc.gridy = row++;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(15, 15, 25, 15); // 标题下方加大间距
        cardPanel.add(titleLabel, cardGbc);
        cardGbc.insets = new Insets(8, 15, 8, 15);
        cardGbc.gridwidth = 1;

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14)); // 字体增大
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(emailLabel, cardGbc);

        emailField = UIHelper.createTextField();
        emailField.setPreferredSize(new Dimension(280, 45));
        cardGbc.gridx = 1;
        cardPanel.add(emailField, cardGbc);
        row++;

        emailErrorLabel = createErrorLabel();
        cardGbc.gridy = row++;
        cardGbc.gridx = 1;
        cardGbc.insets = new Insets(0, 15, 10, 15); // 错误标签与下一行间距
        cardPanel.add(emailErrorLabel, cardGbc);
        cardGbc.insets = new Insets(8, 15, 8, 15);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(passwordLabel, cardGbc);

        JPanel passwordPanel = createPasswordPanel(false);
        cardGbc.gridx = 1;
        cardPanel.add(passwordPanel, cardGbc);
        row++;

        passwordErrorLabel = createErrorLabel();
        cardGbc.gridy = row++;
        cardGbc.gridx = 1;
        cardGbc.insets = new Insets(0, 15, 10, 15);
        cardPanel.add(passwordErrorLabel, cardGbc);
        cardGbc.insets = new Insets(8, 15, 8, 15);

        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(confirmLabel, cardGbc);

        JPanel confirmPanel = createPasswordPanel(true);
        cardGbc.gridx = 1;
        cardPanel.add(confirmPanel, cardGbc);
        row++;

        confirmErrorLabel = createErrorLabel();
        cardGbc.gridy = row++;
        cardGbc.gridx = 1;
        cardGbc.insets = new Insets(0, 15, 10, 15);
        cardPanel.add(confirmErrorLabel, cardGbc);
        cardGbc.insets = new Insets(8, 15, 8, 15);

        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(nameLabel, cardGbc);

        nameField = UIHelper.createTextField();
        nameField.setPreferredSize(new Dimension(280, 45));
        cardGbc.gridx = 1;
        cardPanel.add(nameField, cardGbc);
        row++;

        nameErrorLabel = createErrorLabel();
        cardGbc.gridy = row++;
        cardGbc.gridx = 1;
        cardGbc.insets = new Insets(0, 15, 10, 15);
        cardPanel.add(nameErrorLabel, cardGbc);
        cardGbc.insets = new Insets(8, 15, 8, 15);

        JLabel roleLabel = new JLabel("Role");
        roleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardPanel.add(roleLabel, cardGbc);

        roleCombo = new JComboBox<>(new String[] { "TA", "MO", "Admin" });
        roleCombo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        roleCombo.setPreferredSize(new Dimension(280, 45));
        roleCombo.setBackground(Color.WHITE);
        cardGbc.gridx = 1;
        cardPanel.add(roleCombo, cardGbc);
        row++;

        cardGbc.gridy = row++;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.insets = new Insets(20, 15, 20, 15);
        cardPanel.add(Box.createVerticalStrut(10), cardGbc);
        cardGbc.insets = new Insets(8, 15, 8, 15);
        cardGbc.gridwidth = 1;

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(1, 2, 20, 0));

        JButton registerBtn = UIHelper.createButton("Register", UIHelper.SUCCESS_COLOR);
        JButton backBtn = UIHelper.createButton("Back to Login", UIHelper.SECONDARY_COLOR);

        registerBtn.setEnabled(false);

        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);

        cardGbc.gridy = row;
        cardGbc.gridx = 0;
        cardGbc.gridwidth = 2;
        cardGbc.fill = GridBagConstraints.HORIZONTAL;
        cardPanel.add(buttonPanel, cardGbc);

        setupEmailValidation(registerBtn);
        setupPasswordValidation(registerBtn);
        setupConfirmPasswordValidation(registerBtn);
        setupNameValidation(registerBtn);

        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JScrollPane scrollPane = UIHelper.createScrollPane(cardPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(UIHelper.BACKGROUND_COLOR);
        GridBagConstraints wgbc = new GridBagConstraints();
        wgbc.insets = new Insets(20, 20, 20, 20);
        wgbc.fill = GridBagConstraints.BOTH;
        wgbc.weightx = 1;
        wgbc.weighty = 1;
        wrapper.add(mainPanel, wgbc);

        add(wrapper);
    }

    /**
     * Handles the registration process.
     * <p>
     * Creates a new user account if all validations pass.
     * </p>
     */
    private void register() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        if (!emailValid || !passwordValid || !confirmValid || !nameValid) {
            UIHelper.showWarningDialog(this, "Please correct the errors before registering.", "Invalid Input");
            return;
        }

        User newUser = new User(email, password, role, name);
        users.add(newUser);
        FileUtil.saveUsers(users);

        LoggerUtil.logRegistration(email, role);
        LoggerUtil.logCreate("User", email + " (" + role + ")");

        UIHelper.showInfoDialog(this, "Registration successful! Please login.", "Success");
        dispose();
        new LoginFrame();
    }
}
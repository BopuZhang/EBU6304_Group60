package system;

import javax.swing.*;
import java.awt.*;

/**
 * UI helper class for consistent styling
 */
public class UIHelper {

    // Theme colors
    public static final Color PRIMARY_COLOR = new Color(79, 114, 139);
    public static final Color SECONDARY_COLOR = new Color(96, 125, 139);
    public static final Color ACCENT_COLOR = new Color(255, 152, 0);
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    public static final Color CARD_COLOR = Color.WHITE;
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);

    /**
     * Create a styled button
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Create a title label
     */
    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(PRIMARY_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Create a styled text field - SIMPLE VERSION
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Don't set any borders or anything that might break it
        return field;
    }

    /**
     * Create a styled password field - SIMPLE VERSION
     */
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    /**
     * Create a styled card panel
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        return panel;
    }
}
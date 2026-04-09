package system;

import javax.swing.*;
import java.awt.*;

/**
 * UI helper class for consistent styling
 * 使用 FlatLaf 主题颜色
 */
public class UIHelper {

    // 从 FlatLaf UIManager 获取颜色，如果没有则使用默认值
    public static Color getAccentColor() {
        Color accent = UIManager.getColor("Component.accentColor");
        return accent != null ? accent : new Color(79, 114, 139);
    }

    public static Color getSuccessColor() {
        // FlatLaf 没有专门的 success 颜色，使用绿色调
        return new Color(76, 175, 80);
    }

    public static Color getDangerColor() {
        return new Color(244, 67, 54);
    }

    public static Color getWarningColor() {
        return new Color(255, 152, 0);
    }

    public static Color getSecondaryColor() {
        Color secondary = UIManager.getColor("Component.infoColor");
        return secondary != null ? secondary : new Color(96, 125, 139);
    }

    // 主题颜色 - 使用 FlatLaf 的颜色
    public static final Color PRIMARY_COLOR = new Color(79, 114, 139);
    public static final Color SECONDARY_COLOR = new Color(96, 125, 139);
    public static final Color ACCENT_COLOR = new Color(255, 152, 0);
    public static final Color BACKGROUND_COLOR = UIManager.getColor("Panel.background") != null ?
            UIManager.getColor("Panel.background") : new Color(245, 245, 245);
    public static final Color CARD_COLOR = UIManager.getColor("Panel.background") != null ?
            UIManager.getColor("Panel.background") : Color.WHITE;
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    public static final Color DANGER_COLOR = new Color(244, 67, 54);
    public static final Color WARNING_COLOR = new Color(255, 152, 0);

    /**
     * 获取当前主题是否为深色主题
     */
    public static boolean isDarkTheme() {
        Color bg = UIManager.getColor("Panel.background");
        if (bg != null) {
            // 简单判断：如果背景亮度小于 128，认为是深色主题
            int brightness = (bg.getRed() + bg.getGreen() + bg.getBlue()) / 3;
            return brightness < 128;
        }
        return false;
    }

    /**
     * 获取适合当前主题的文本颜色
     */
    public static Color getTextColor() {
        return UIManager.getColor("Label.foreground");
    }

    /**
     * 获取适合当前主题的次要文本颜色
     */
    public static Color getSecondaryTextColor() {
        Color text = UIManager.getColor("Label.disabledForeground");
        return text != null ? text : new Color(150, 150, 150);
    }

    /**
     * 获取表格交替行颜色
     */
    public static Color getTableAlternateColor() {
        return isDarkTheme() ? new Color(60, 63, 65) : new Color(250, 250, 250);
    }

    /**
     * 获取表头背景颜色
     */
    public static Color getHeaderBackgroundColor() {
        return isDarkTheme() ? new Color(50, 53, 55) : new Color(240, 240, 240);
    }

    /**
     * 获取表头文字颜色
     */
    public static Color getHeaderForegroundColor() {
        return getAccentColor();
    }

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
     * 创建使用主题强调色的按钮
     */
    public static JButton createAccentButton(String text) {
        return createButton(text, getAccentColor());
    }

    /**
     * Create a title label
     */
    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(getAccentColor());
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Create a styled text field
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return field;
    }

    /**
     * Create a styled password field
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

    /**
     * 创建状态标签
     */
    public static JLabel createStatusLabel(String text, String type) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        switch (type.toLowerCase()) {
            case "success":
            case "accepted":
                label.setForeground(SUCCESS_COLOR);
                break;
            case "danger":
            case "error":
            case "rejected":
                label.setForeground(DANGER_COLOR);
                break;
            case "warning":
            case "pending":
                label.setForeground(WARNING_COLOR);
                break;
            default:
                label.setForeground(getSecondaryTextColor());
                break;
        }
        return label;
    }

    /**
     * 获取状态对应的颜色
     */
    public static Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "ACCEPTED":
            case "OPEN":
            case "SUCCESS":
                return SUCCESS_COLOR;
            case "REJECTED":
            case "CLOSED":
            case "ERROR":
                return DANGER_COLOR;
            case "PENDING":
            case "WARNING":
                return WARNING_COLOR;
            default:
                return getSecondaryTextColor();
        }
    }

    /**
     * 格式化状态文本为 HTML 颜色标签
     */
    public static String formatStatusHtml(String text, String status) {
        Color color = getStatusColor(status);
        String hex = String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
        return "<html><font color='" + hex + "'>" + text + "</font></html>";
    }
}
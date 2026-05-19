package system;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Utility class providing consistent modern UI styling for Swing components.
 * <p>
 * This class offers a centralized design system with:
 * </p>
 * <ul>
 * <li>A Tailwind-inspired color palette</li>
 * <li>Rounded buttons with hover/press effects</li>
 * <li>Styled text fields with focus indicators</li>
 * <li>Card panels with subtle shadows</li>
 * <li>Modern scroll bars</li>
 * <li>Styled dialog boxes</li>
 * </ul>
 * <p>
 * All components are created using pure Swing without external libraries.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2026
 */
public class UIHelper {

    /** Primary color (Indigo 600) - used for main actions */
    public static final Color PRIMARY_COLOR = new Color(79, 70, 229);

    /** Secondary color (Indigo 500) - used for secondary elements */
    public static final Color SECONDARY_COLOR = new Color(99, 102, 241);

    /** Accent color (Warm amber) - used for highlights */
    public static final Color ACCENT_COLOR = new Color(234, 138, 56);

    /** Background color (Gray 50) - main application background */
    public static final Color BACKGROUND_COLOR = new Color(249, 250, 251);

    /** Card background color */
    public static final Color CARD_COLOR = Color.WHITE;

    /** Success color (Muted teal-green) - for positive states */
    public static final Color SUCCESS_COLOR = new Color(72, 175, 130);

    /** Danger color (Muted soft red) - for errors and destructive actions */
    public static final Color DANGER_COLOR = new Color(210, 108, 108);

    /** Disabled color (Soft cool gray) - for disabled elements */
    public static final Color DISABLED_COLOR = new Color(170, 170, 180);

    /** Uniform corner radius for rounded components */
    private static final int BORDER_RADIUS = 12;

    /** Shadow offset distance for card panels */
    private static final int SHADOW_OFFSET = 3;

    // ---------- Custom rounded border for text fields ----------
    /**
     * Custom border implementation that draws rounded corners.
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color borderColor;
        private final int radius;

        /**
         * Creates a rounded border with the specified color and radius.
         *
         * @param borderColor the border color
         * @param radius      the corner radius
         */
        public RoundedBorder(Color borderColor, int radius) {
            this.borderColor = borderColor;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }

    /**
     * Paints a subtle shadow effect for card-like components.
     *
     * @param g      the Graphics context
     * @param width  the component width
     * @param height the component height
     * @param offset the shadow offset
     * @param radius the corner radius
     */
    private static void paintShadow(Graphics g, int width, int height, int offset, int radius) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (int i = 0; i < 4; i++) {
            g2.setColor(new Color(0, 0, 0, 6 - i * 2));
            g2.fillRoundRect(offset, offset + i, width - offset * 2, height - offset * 2, radius, radius);
        }
        g2.dispose();
    }

    /**
     * Creates a rounded button with hover and press effects.
     * <p>
     * The button is fully custom-painted with rounded corners and
     * interactive color changes on hover and press.
     * </p>
     *
     * @param text    the button text
     * @param bgColor the background color
     * @return the styled JButton
     */
    public static JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bg = isEnabled() ? getBackground() : bgColor.brighter();
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);

                String buttonText = getText();
                if (buttonText != null && !buttonText.isEmpty()) {
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle textRect = fm.getStringBounds(buttonText, g2).getBounds();
                    int textX = (getWidth() - textRect.width) / 2;
                    int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();
                    g2.setColor(Color.WHITE);
                    g2.drawString(buttonText, textX, textY);
                }
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };

        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker());
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(bgColor.darker().darker());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled() && button.contains(e.getPoint())) {
                    button.setBackground(bgColor.darker());
                } else {
                    button.setBackground(bgColor);
                }
            }
        });

        return button;
    }

    /**
     * Creates a title label with the primary color and bold font.
     *
     * @param text the title text
     * @return the styled JLabel
     */
    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        label.setForeground(PRIMARY_COLOR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Creates a text field with rounded corners and focus effect.
     * <p>
     * The border color changes to the primary color when focused.
     * </p>
     *
     * @return the styled JTextField
     */
    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(new Color(200, 200, 200), 8));
        field.setOpaque(false);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(PRIMARY_COLOR, 8));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(new Color(200, 200, 200), 8));
            }
        });

        return field;
    }

    /**
     * Creates a password field with rounded corners and focus effect.
     * <p>
     * The border color changes to the primary color when focused.
     * </p>
     *
     * @return the styled JPasswordField
     */
    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(new Color(200, 200, 200), 8));
        field.setOpaque(false);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(new RoundedBorder(PRIMARY_COLOR, 8));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(new RoundedBorder(new Color(200, 200, 200), 8));
            }
        });

        return field;
    }

    /**
     * Creates a card panel with a subtle shadow effect.
     * <p>
     * The panel has a white background with rounded corners and
     * a soft drop shadow for a modern card-like appearance.
     * </p>
     *
     * @return the styled JPanel
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                paintShadow(g, getWidth(), getHeight(), SHADOW_OFFSET, 16);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_COLOR);
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setBackground(new Color(0, 0, 0, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        return panel;
    }

    /**
     * Creates a primary-styled button.
     *
     * @param text the button text
     * @return the styled JButton with PRIMARY_COLOR
     */
    public static JButton createPrimaryButton(String text) {
        return createButton(text, PRIMARY_COLOR);
    }

    /**
     * Creates an accent-styled button.
     *
     * @param text the button text
     * @return the styled JButton with ACCENT_COLOR
     */
    public static JButton createAccentButton(String text) {
        return createButton(text, ACCENT_COLOR);
    }

    // ---------- Styled Option Panes (unchanged, omitted for brevity) ----------
    // ... 之前已经提供的 showMessageDialog, showWarningDialog 等方法 ...

    // ---------- Modern ScrollBarUI ----------
    private static class ModernScrollBarUI extends BasicScrollBarUI {
        private static final int THUMB_RADIUS = 8;
        private static final int TRACK_RADIUS = 8;
        private static final int SCROLL_BAR_WIDTH = 10;

        private boolean isThumbHovered = false;
        private boolean isThumbPressed = false;

        @Override
        protected void configureScrollBarColors() {
            thumbColor = PRIMARY_COLOR;
            trackColor = new Color(230, 230, 230);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) {
            return createZeroSizeButton();
        }

        @Override
        protected JButton createIncreaseButton(int orientation) {
            return createZeroSizeButton();
        }

        private JButton createZeroSizeButton() {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(0, 0));
            button.setMinimumSize(new Dimension(0, 0));
            button.setMaximumSize(new Dimension(0, 0));
            return button;
        }

        @Override
        public Dimension getPreferredSize(JComponent c) {
            if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
                return new Dimension(SCROLL_BAR_WIDTH, super.getPreferredSize(c).height);
            } else {
                return new Dimension(super.getPreferredSize(c).width, SCROLL_BAR_WIDTH);
            }
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color thumbColor = PRIMARY_COLOR;
            if (isThumbPressed) {
                thumbColor = PRIMARY_COLOR.darker().darker();
            } else if (isThumbHovered) {
                thumbColor = PRIMARY_COLOR.darker();
            }
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, THUMB_RADIUS,
                    THUMB_RADIUS);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(trackColor);
            g2.fillRoundRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height, TRACK_RADIUS,
                    TRACK_RADIUS);
            g2.dispose();
        }

        @Override
        protected TrackListener createTrackListener() {
            return new TrackListener() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    if (getThumbBounds().contains(e.getPoint())) {
                        isThumbHovered = true;
                        scrollbar.repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    isThumbHovered = false;
                    scrollbar.repaint();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (getThumbBounds().contains(e.getPoint())) {
                        isThumbPressed = true;
                        scrollbar.repaint();
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseReleased(e);
                    isThumbPressed = false;
                    scrollbar.repaint();
                }
            };
        }
    }

    /**
     * Creates a JScrollPane with modern, styled scroll bars.
     *
     * @param view the component to be displayed in the scroll pane
     * @return a styled JScrollPane
     */
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane scrollPane = new JScrollPane(view);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        return scrollPane;
    }

    // ---------- Styled Option Panes ----------

    /**
     * Show a styled message dialog.
     */
    public static void showMessageDialog(Component parent, String message, String title, int messageType) {
        JOptionPane optionPane = new JOptionPane(message, messageType, JOptionPane.DEFAULT_OPTION);
        JDialog dialog = optionPane.createDialog(parent, title);
        styleDialogButtons(optionPane);
        dialog.setVisible(true);
    }

    /**
     * Show a styled warning dialog.
     */
    public static void showWarningDialog(Component parent, String message, String title) {
        showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show a styled error dialog.
     */
    public static void showErrorDialog(Component parent, String message, String title) {
        showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a styled information dialog.
     */
    public static void showInfoDialog(Component parent, String message, String title) {
        showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a styled confirmation dialog.
     * 
     * @return JOptionPane.YES_OPTION or JOptionPane.NO_OPTION
     */
    public static int showConfirmDialog(Component parent, String message, String title, int optionType) {
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, optionType);
        JDialog dialog = optionPane.createDialog(parent, title);
        styleDialogButtons(optionPane);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (selectedValue instanceof Integer) {
            return (Integer) selectedValue;
        }
        return JOptionPane.CLOSED_OPTION;
    }

    /**
     * Apply custom button styling to a JOptionPane's buttons.
     */
    private static void styleDialogButtons(JOptionPane optionPane) {
        // Find all buttons in the option pane and replace them with styled versions
        for (Component comp : optionPane.getComponents()) {
            if (comp instanceof JPanel) {
                for (Component inner : ((JPanel) comp).getComponents()) {
                    if (inner instanceof JButton) {
                        JButton original = (JButton) inner;
                        JButton styled = createDialogButton(original.getText());
                        // Copy action listeners
                        for (ActionListener al : original.getActionListeners()) {
                            styled.addActionListener(al);
                        }
                        // Replace in parent
                        Container parent = original.getParent();
                        if (parent != null) {
                            Component[] siblings = parent.getComponents();
                            for (int i = 0; i < siblings.length; i++) {
                                if (siblings[i] == original) {
                                    parent.remove(i);
                                    parent.add(styled, i);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Create a button specifically for dialogs (smaller padding).
     */
    private static JButton createDialogButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // Manual text drawing
                String buttonText = getText();
                if (buttonText != null && !buttonText.isEmpty()) {
                    FontMetrics fm = g2.getFontMetrics();
                    Rectangle textRect = fm.getStringBounds(buttonText, g2).getBounds();
                    int textX = (getWidth() - textRect.width) / 2;
                    int textY = (getHeight() - textRect.height) / 2 + fm.getAscent();
                    g2.setColor(Color.WHITE);
                    g2.drawString(buttonText, textX, textY);
                }
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                // No border
            }
        };
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR.darker().darker());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    button.setBackground(PRIMARY_COLOR.darker());
                } else {
                    button.setBackground(PRIMARY_COLOR);
                }
            }
        });
        return button;
    }

    /**
     * Calculates the skill match percentage between a TA and a job.
     *
     * @param taSkills  the TA's skills
     * @param jobSkills the job's required skills
     * @return the match percentage (0-100), or -1 if job has no skills
     */
    public static int calculateSkillMatch(List<String> taSkills, List<String> jobSkills) {
        if (jobSkills == null || jobSkills.isEmpty())
            return -1;
        if (taSkills == null || taSkills.isEmpty())
            return 0;
        int matched = 0;
        for (String req : jobSkills) {
            for (String skill : taSkills) {
                if (skill.trim().equalsIgnoreCase(req.trim())) {
                    matched++;
                    break;
                }
            }
        }
        return (int) ((matched / (double) jobSkills.size()) * 100);
    }

    /**
     * Creates a styled skill tag label.
     *
     * @param text the skill text
     * @return the styled JLabel
     */
    public static JLabel createSkillTag(String text) {
        JLabel tag = new JLabel(text);
        tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        tag.setForeground(PRIMARY_COLOR);
        tag.setBackground(new Color(238, 236, 255));
        tag.setOpaque(true);
        tag.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return tag;
    }

    public static JLabel createSkillTag(String text, boolean matched) {
        JLabel tag = new JLabel(text);
        tag.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));
        if (matched) {
            tag.setForeground(SUCCESS_COLOR);
            tag.setBackground(new Color(232, 245, 238));
        } else {
            tag.setForeground(DANGER_COLOR);
            tag.setBackground(new Color(252, 235, 235));
        }
        tag.setOpaque(true);
        tag.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        return tag;
    }

    public static JLabel createMatchLabel(int percentage) {
        String text = percentage >= 0 ? percentage + "%" : "N/A";
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        if (percentage < 0) {
            label.setForeground(DISABLED_COLOR);
        } else if (percentage >= 80) {
            label.setForeground(SUCCESS_COLOR);
        } else if (percentage >= 50) {
            label.setForeground(ACCENT_COLOR);
        } else {
            label.setForeground(DANGER_COLOR);
        }
        return label;
    }

    /**
     * Show a styled confirmation dialog with a custom panel as message.
     * @param parent parent component
     * @param panel custom panel to display
     * @param title dialog title
     * @param optionType option type (e.g., JOptionPane.OK_CANCEL_OPTION)
     * @return user's option
     */
    public static int showConfirmDialog(Component parent, JPanel panel, String title, int optionType) {
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, optionType);
        JDialog dialog = optionPane.createDialog(parent, title);
        styleDialogButtons(optionPane);
        dialog.setVisible(true);

        Object selectedValue = optionPane.getValue();
        if (selectedValue == null) {
            return JOptionPane.CLOSED_OPTION;
        }
        if (selectedValue instanceof Integer) {
            return (Integer) selectedValue;
        }
        return JOptionPane.CLOSED_OPTION;
    }
}
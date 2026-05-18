package system.ui;

import java.awt.*;

/**
 * A flow layout that wraps components to the next line when they exceed
 * the container width.
 * <p>
 * This layout manager extends FlowLayout to support automatic line wrapping,
 * making it ideal for displaying tags, buttons, or other components that
 * should flow and wrap naturally.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class WrapLayout extends FlowLayout {

    /** Cached preferred layout size */
    private Dimension preferredLayoutSize;

    /**
     * Constructs a new WrapLayout with left alignment.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Constructs a new WrapLayout with the specified alignment.
     *
     * @param align the alignment (LEFT, CENTER, RIGHT)
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Constructs a new WrapLayout with the specified alignment and gaps.
     *
     * @param align the alignment (LEFT, CENTER, RIGHT)
     * @param hgap  the horizontal gap between components
     * @param vgap  the vertical gap between rows
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Returns the preferred dimensions for this layout.
     *
     * @param target the target container
     * @return the preferred dimensions
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Returns the minimum dimensions for this layout.
     *
     * @param target the target container
     * @return the minimum dimensions
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    /**
     * Calculates the layout size based on preferred or minimum component sizes.
     *
     * @param target    the target container
     * @param preferred true for preferred size, false for minimum size
     * @return the calculated dimensions
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            int width = 0;
            int height = 0;
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth) {
                        width = Math.max(width, rowWidth);
                        height += rowHeight + vgap;
                        rowWidth = d.width;
                        rowHeight = d.height;
                    } else {
                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
            }

            width = Math.max(width, rowWidth);
            height += rowHeight;

            width += horizontalInsetsAndGap;
            height += insets.top + insets.bottom + vgap * 2;

            preferredLayoutSize = new Dimension(width, height);
            return preferredLayoutSize;
        }
    }
}
package michalwa.auditorium;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

class SpatialSlider extends JComponent {
    private static final Stroke DASH_STROKE = new BasicStroke(
        1.0f,
        BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER,
        1.0f,
        new float[] { 4.0f, 8.0f },
        0.0f
    );

    private int padding = 20;
    private int roundSize = 6;
    private int handleRadius = 4;

    private PopupFactory popupFactory;

    private Point2D value = new Point2D.Double();
    private List<? extends SpatialRegion<?>> regions;

    SpatialSlider(List<? extends SpatialRegion<?>> regions, PopupFactory popupFactory) {
        this.regions = regions;
        this.popupFactory = popupFactory;

        setBackground(Color.BLACK);
        setForeground(Color.LIGHT_GRAY);
        setOpaque(true);

        var mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) setValue(getNewValue(e));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
                if (SwingUtilities.isLeftMouseButton(e)) setValue(getNewValue(e));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void drawHorizontalLine(Graphics g, int x, int y, int width) {
        g.drawLine(x, y, x + width, y);
    }

    private void drawVerticalLine(Graphics g, int x, int y, int height) {
        g.drawLine(x, y, x, y + height);
    }

    private int getAreaHeight() {
        return getHeight() - padding * 2;
    }

    private int getAreaWidth() {
        return getWidth() - padding * 2;
    }

    private int getHandleXOffset() {
        return (int)(value.getX() * getAreaWidth());
    }

    private int getHandleYOffset() {
        return (int)(value.getY() * getAreaHeight());
    }

    private Point2D getNewValue(MouseEvent e) {
        return new Point2D.Float(
            Math.clamp((e.getX() - padding) / (float)getAreaWidth(), 0.0f, 1.0f),
            Math.clamp((e.getY() - padding) / (float)getAreaHeight(), 0.0f, 1.0f)
        );
    }

    public Point2D getValue() {
        return value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setClip(padding, padding, getAreaWidth(), getAreaHeight());

        g2d.setColor(getForeground());
        g2d.setStroke(DASH_STROKE);

        drawVerticalLine(g, padding + getAreaWidth() / 2, padding, getAreaHeight());
        drawHorizontalLine(g, padding, padding + getAreaHeight() / 2, getAreaWidth());

        g2d.setStroke(new BasicStroke());

        for (var pass = 0; pass < 2; pass++) {
            for (SpatialRegion<?> region : regions) {
                var cx = padding + region.getCenter().getX() * getAreaWidth();
                var cy = padding + region.getCenter().getY() * getAreaHeight();
                var r = region.getRadius() * Math.min(getAreaWidth(), getAreaHeight());

                if (pass == 0) {
                    g2d.setColor(region.getColor());
                    g2d.drawOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));
                } else if (pass == 1) {
                    g2d.setPaint(
                        new RadialGradientPaint(
                            (float)cx,
                            (float)cy,
                            (float)r,
                            new float[] { 0.0f, 1.0f },
                            new Color[] { region.getColor(), new Color(0, 0, 0, 0) }
                        )
                    );
                    g2d.fillOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));
                }
            }
        }

        g2d.setClip(null);

        g2d.setColor(getForeground());
        g2d.fillOval(
            padding + getHandleXOffset() - handleRadius,
            padding + getHandleYOffset() - handleRadius,
            handleRadius * 2,
            handleRadius * 2
        );

        g2d.setColor(getForeground());
        g2d.drawRoundRect(padding, padding, getAreaWidth(), getAreaHeight(), roundSize, roundSize);
    }

    public void setValue(Point2D value) {
        var oldValue = this.value;
        this.value = value;

        firePropertyChange("value", oldValue, value);
        repaint();
    }

    private void showContextMenu(MouseEvent e) {
        popupFactory.createPopup(getNewValue(e)).show(e.getComponent(), e.getX(), e.getY());
    }

    interface PopupFactory {
        JPopupMenu createPopup(Point2D value);
    }
}

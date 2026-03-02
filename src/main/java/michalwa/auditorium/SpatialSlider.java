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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

class SpatialSlider<TData> extends JComponent {
    private static Stroke DASH_STROKE = new BasicStroke(
        1.0f,
        BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER,
        1.0f,
        new float[] { 4.0f, 8.0f },
        0.0f
    );
    private float valueX = 0.0f, valueY = 0.0f;
    private int padding = 20;
    private int roundSize = 6;
    private int handleRadius = 4;
    private List<SpatialRegion<TData>> regions = new ArrayList<>();

    private List<Listener<TData>> listenerList = new ArrayList<>();

    private PopupFactory popupFactory;

    SpatialSlider(PopupFactory popupFactory) {
        this.popupFactory = popupFactory;

        setBackground(Color.BLACK);
        setForeground(Color.LIGHT_GRAY);
        setOpaque(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) updateValue(e);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
                if (SwingUtilities.isLeftMouseButton(e)) updateValue(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public void addListener(Listener<TData> listener) {
        listenerList.add(listener);
    }

    public void addRegion(SpatialRegion<TData> region) {
        regions.add(region);

        for (Listener<TData> listener : listenerList) listener.regionAdded(region);

        repaint();
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
        return (int)(valueX * getAreaWidth());
    }

    private int getHandleYOffset() {
        return (int)(valueY * getAreaHeight());
    }

    private float getNewValueX(MouseEvent e) {
        return Math.clamp((e.getX() - padding) / (float)getAreaWidth(), 0.0f, 1.0f);
    }

    private float getNewValueY(MouseEvent e) {
        return Math.clamp((e.getY() - padding) / (float)getAreaHeight(), 0.0f, 1.0f);
    }

    public List<SpatialRegion<TData>> getRegions() {
        return regions;
    }

    public float getValueX() {
        return valueX;
    }

    public float getValueY() {
        return valueY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setClip(padding, padding, getAreaWidth(), getAreaHeight());

        g2d.setColor(getForeground());
        g2d.setStroke(DASH_STROKE);

        drawVerticalLine(g, padding + getAreaWidth() / 2, padding, getAreaHeight());
        drawHorizontalLine(g, padding, padding + getAreaHeight() / 2, getAreaWidth());

        g2d.setStroke(new BasicStroke());

        for (int pass = 0; pass < 2; pass++) {
            for (SpatialRegion<TData> region : regions) {
                float cx = padding + region.centerX * getAreaWidth();
                float cy = padding + region.centerY * getAreaHeight();
                float r = region.radius * Math.min(getAreaWidth(), getAreaHeight());

                if (pass == 0) {
                    g2d.setColor(region.color);
                    g2d.drawOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));
                } else if (pass == 1) {
                    g2d.setPaint(
                        new RadialGradientPaint(
                            cx,
                            cy,
                            r,
                            new float[] { 0.0f, 1.0f },
                            new Color[] { region.color, new Color(0, 0, 0, 0) }
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

    private void showContextMenu(MouseEvent e) {
        popupFactory.createPopup(getNewValueX(e), getNewValueY(e))
            .show(e.getComponent(), e.getX(), e.getY());
    }

    private void updateValue(MouseEvent e) {
        valueX = getNewValueX(e);
        valueY = getNewValueY(e);

        for (Listener<TData> listener : listenerList) listener.valueChanged(valueX, valueY);

        repaint();
    }

    interface Listener<TData> {
        void regionAdded(SpatialRegion<TData> region);

        void valueChanged(float x, float y);
    }

    interface PopupFactory {
        JPopupMenu createPopup(float x, float y);
    }
}

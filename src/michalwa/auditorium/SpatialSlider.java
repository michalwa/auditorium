package michalwa.auditorium;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

class SpatialSlider<TData> extends JComponent {
    private float valueX = 0.0f, valueY = 0.0f;
    private int padding = 20;
    private int roundSize = 6;
    private int handleRadius = 4;
    private List<SpatialRegion<TData>> regions = new ArrayList<>();
    private DataFactory<TData> dataFactory;
    private List<Listener<TData>> listenerList = new ArrayList<>();
    private Color[] regionColors = new Color[] {
        Color.BLUE,
        Color.RED,
        Color.GREEN,
    };

    private static Stroke DASH_STROKE = new BasicStroke(
        1.0f,
        BasicStroke.CAP_SQUARE,
        BasicStroke.JOIN_MITER,
        1.0f,
        new float[] { 4.0f, 8.0f },
        0.0f
    );

    SpatialSlider(DataFactory<TData> dataFactory) {
        this.dataFactory = dataFactory;

        setBackground(Color.BLACK);
        setForeground(Color.LIGHT_GRAY);
        setOpaque(true);

        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
                if (SwingUtilities.isLeftMouseButton(e)) updateValue(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) showContextMenu(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) updateValue(e);
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    public float getValueX() {
        return valueX;
    }

    public float getValueY() {
        return valueY;
    }

    public List<SpatialRegion<TData>> getRegions() {
        return regions;
    }

    public void addRegion(float x, float y) {
        Optional<TData> data = dataFactory.getData();

        if (data.isPresent()) {
            SpatialRegion<TData> region = new SpatialRegion<>(x, y, 0.5f, data.get());
            regions.add(region);

            for (Listener<TData> listener : listenerList)
                listener.regionAdded(region);

            repaint();
        }
    }

    public void addListener(Listener<TData> listener) {
        listenerList.add(listener);
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
            for (int i = 0; i < regions.size(); i++) {
                SpatialRegion<TData> region = regions.get(i);
                Color regionColor = regionColors[i % regionColors.length];

                float cx = padding + region.centerX * getAreaWidth();
                float cy = padding + region.centerY * getAreaHeight();
                float r = region.radius * Math.min(getAreaWidth(), getAreaHeight());

                if (pass == 0) {
                    g2d.setPaint(new RadialGradientPaint(
                        cx,
                        cy,
                        r,
                        new float[] { 0.0f, 1.0f },
                        new Color[] { regionColor, new Color(0, 0, 0, 0) }
                    ));
                    g2d.fillOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));
                } else if (pass == 1) {
                    g2d.setColor(regionColor);
                    g2d.drawOval((int)(cx - r), (int)(cy - r), (int)(r * 2), (int)(r * 2));
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
        g2d.drawRoundRect(
            padding,
            padding,
            getAreaWidth(),
            getAreaHeight(),
            roundSize,
            roundSize
        );
    }

    private void updateValue(MouseEvent e) {
        valueX = getNewValueX(e);
        valueY = getNewValueY(e);

        for (Listener<TData> listener : listenerList)
            listener.valueChanged(valueX, valueY);

        repaint();
    }

    private float getNewValueX(MouseEvent e) {
        return Math.clamp((e.getX() - padding) / (float)getAreaWidth(), 0.0f, 1.0f);
    }

    private float getNewValueY(MouseEvent e) {
        return Math.clamp((e.getY() - padding) / (float)getAreaHeight(), 0.0f, 1.0f);
    }

    private int getAreaWidth() {
        return getWidth() - padding * 2;
    }

    private int getAreaHeight() {
        return getHeight() - padding * 2;
    }

    private int getHandleXOffset() {
        return (int)(valueX * getAreaWidth());
    }

    private int getHandleYOffset() {
        return (int)(valueY * getAreaHeight());
    }

    private void drawVerticalLine(Graphics g, int x, int y, int height) {
        g.drawLine(x, y, x, y + height);
    }

    private void drawHorizontalLine(Graphics g, int x, int y, int width) {
        g.drawLine(x, y, x + width, y);
    }

    private void showContextMenu(MouseEvent e) {
        new ContextMenu(getNewValueX(e), getNewValueY(e))
            .show(e.getComponent(), e.getX(), e.getY());
    }

    interface DataFactory<TData> {
        Optional<TData> getData();
    }

    interface Listener<TData> {
        void valueChanged(float x, float y);
        void regionAdded(SpatialRegion<TData> region);
    }

    private class ContextMenu extends JPopupMenu {
        private float valueX, valueY;

        ContextMenu(float valueX, float valueY) {
            this.valueX = valueX;
            this.valueY = valueY;

            add(new JMenuItem("Add region"))
                .addActionListener((e) -> addRegion(valueX, valueY));
        }
    }
}

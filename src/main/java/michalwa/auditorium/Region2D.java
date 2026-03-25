package michalwa.auditorium;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * A radial region/circle in 2D space, used with {@link Slider2D}
 */
public class Region2D<TData extends Region2D.Data> implements Serializable {
    private static final long serialVersionUID = 2026_03_06_001L;
    private static final double DEFAULT_RADIUS = 0.4;

    private transient boolean selected = false;
    private boolean visible = true;
    private Point2D center;
    private double radius;
    private Color color = Color.GREEN;
    private TData data;

    Region2D(Point2D center, TData data) {
        this(center, DEFAULT_RADIUS, data);
    }

    Region2D(Point2D center, double radius, TData data) {
        this.center = center;
        this.radius = radius;
        this.data = data;
    }

    public Point2D getCenter() {
        return center;
    }

    public double getCenterX() {
        return center.getX();
    }

    public double getCenterY() {
        return center.getY();
    }

    public Color getColor() {
        return color;
    }

    public TData getData() {
        return data;
    }

    public double getRadius() {
        return radius;
    }

    public double getSquareRadius() {
        return radius * radius;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setCenterX(double x) {
        center.setLocation(x, center.getY());
    }

    public void setCenterY(double y) {
        center.setLocation(center.getX(), y);
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public interface Data {
        /**
         * @return an intensity value for visualization that may change over
         *         time
         */
        float getDynamicIntensity();

        /**
         * @return an intensity value for visualization that should not change
         *         over time
         */
        float getStaticIntensity();
    }
}

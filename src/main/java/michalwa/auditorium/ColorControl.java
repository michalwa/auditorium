package michalwa.auditorium;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JColorChooser;

class ColorControl extends JButton {
    private Color value;
    private List<Runnable> cancelListeners = new ArrayList<>();

    ColorControl(Color value) {
        this.value = value;

        addActionListener(e -> {
            var newValue = JColorChooser.showDialog(this, "Pick a color", value);
            if (newValue != null) {
                setValue(newValue);
            } else {
                for (Runnable listener : cancelListeners) {
                    listener.run();
                }
            }
        });
    }

    public void addCancelListener(Runnable listener) {
        cancelListeners.add(listener);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 10);
    }

    public Color getValue() {
        return value;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(value);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    public void setValue(Color value) {
        var oldValue = this.value;
        this.value = value;
        firePropertyChange("value", oldValue, value);
        repaint();
    }
}

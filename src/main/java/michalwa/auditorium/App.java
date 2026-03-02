package michalwa.auditorium;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import michalwa.auditorium.playback.AudioChirp;
import michalwa.auditorium.playback.AudioLoop;
import michalwa.auditorium.playback.SpatialAudio;

class App extends JFrame implements Runnable {
    SpatialSlider<SpatialAudio> spatialSlider;

    @Override
    public void run() {
        setTitle("auditorium");

        spatialSlider = new SpatialSlider<SpatialAudio>(SliderPopupMenu::new);
        spatialSlider.setMinimumSize(new Dimension(400, 400));
        spatialSlider.setPreferredSize(new Dimension(400, 400));

        SpatialRegionTable table = new SpatialRegionTable();

        spatialSlider.addListener(new SpatialSlider.Listener<SpatialAudio>() {
            @Override
            public void regionAdded(SpatialRegion<SpatialAudio> region) {
                updateAudio();
                table.addRegion(region);
            }

            @Override
            public void valueChanged(float x, float y) {
                updateAudio();
                table.repaint();
            }
        });

        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateAudio();
                    spatialSlider.repaint();
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(400, 100));

        add(spatialSlider, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);

        pack();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void updateAudio() {
        for (SpatialRegion<SpatialAudio> region : spatialSlider.getRegions()) {
            float dx = spatialSlider.getValueX() - region.centerX;
            float dy = spatialSlider.getValueY() - region.centerY;
            float squareDist = dx * dx + dy * dy;
            float squareRadius = region.radius * region.radius;

            region.getData().setVolume(1.0f - squareDist / squareRadius);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    class SliderPopupMenu extends JPopupMenu {
        SliderPopupMenu(float x, float y) {
            add(new JMenuItem("Add loop")).addActionListener(e -> {
                SpatialAudio data = FilePicker.loadAudio(AudioLoop::new);
                if (data != null)
                    spatialSlider.addRegion(new SpatialRegion<SpatialAudio>(x, y, 0.4f, data));
            });

            add(new JMenuItem("Add chirp")).addActionListener(e -> {
                SpatialAudio data = FilePicker.loadAudio(AudioChirp::new);
                if (data != null)
                    spatialSlider.addRegion(new SpatialRegion<SpatialAudio>(x, y, 0.4f, data));
            });
        }
    }
}

package michalwa.auditorium;

import java.util.Optional;

import javax.sound.sampled.Clip;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

class App extends JFrame implements Runnable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new App());
    }

    @Override
    public void run() {
        setTitle("auditorium");
        setSize(480, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        SpatialSlider<Clip> spatialSlider = new SpatialSlider<Clip>(
            new SpatialSlider.DataFactory<>() {
                @Override
                public Optional<Clip> getData() {
                    return Optional.ofNullable(FilePicker.loadAudioClip());
                }
            }
        );
        spatialSlider.addListener(new SpatialSlider.Listener<Clip>() {
            @Override
            public void valueChanged(float x, float y) {
                for (SpatialRegion<Clip> region : spatialSlider.getRegions()) {
                    float dx = x - region.centerX;
                    float dy = y - region.centerY;
                    float squareDist = dx * dx + dy * dy;

                    if (squareDist <= region.radius * region.radius) {
                        region.getData().loop(Clip.LOOP_CONTINUOUSLY);
                    } else {
                        region.getData().stop();
                    }
                }
            }

            @Override
            public void regionAdded(SpatialRegion<Clip> region) {}
        });

        add(spatialSlider);

        setVisible(true);
    }
}

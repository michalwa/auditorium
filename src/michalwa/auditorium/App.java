package michalwa.auditorium;

import java.util.Optional;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
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

        SpatialSlider<Audio> spatialSlider = new SpatialSlider<Audio>(
            new SpatialSlider.DataFactory<>() {
                @Override
                public Optional<Audio> getData() {
                    return Optional.ofNullable(FilePicker.loadAudio());
                }
            }
        );
        spatialSlider.addListener(new SpatialSlider.Listener<Audio>() {
            @Override
            public void valueChanged(float x, float y) {
                for (SpatialRegion<Audio> region : spatialSlider.getRegions()) {
                    float dx = x - region.centerX;
                    float dy = y - region.centerY;
                    float squareDist = dx * dx + dy * dy;
                    float squareRadius = region.radius * region.radius;

                    region.getData().setVolume(1.0f - squareDist / squareRadius);
                }
            }

            @Override
            public void regionAdded(SpatialRegion<Audio> region) {}
        });

        add(spatialSlider);

        setVisible(true);
    }
}

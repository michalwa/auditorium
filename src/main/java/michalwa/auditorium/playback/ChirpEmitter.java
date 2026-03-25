package michalwa.auditorium.playback;

import javax.swing.Timer;
import michalwa.auditorium.Region2D;

/**
 * Plays a random clip from a pool periodically as long as it's in range
 */
public class ChirpEmitter extends Emitter {
    private static final long serialVersionUID = 2026_03_26_001L;

    private transient Timer timer;
    private double minDelaySeconds = 1.0;
    private double maxDelaySeconds = 5.0;

    public ChirpEmitter(String name, AudioClip[] clips) {
        super(name, clips, false);
    }

    @Override
    protected void finished() {
        restartTimer();
    }

    @Override
    public String getTypeName() {
        return "chirp";
    }

    @Override
    public void initialize() {
        super.initialize();

        timer = new Timer(0, e -> play());
        timer.setRepeats(false);
        restartTimer();
    }

    private int randomDelayMillis() {
        return (int)((minDelaySeconds + Math.random() * (maxDelaySeconds - minDelaySeconds))
            * 1000.0);
    }

    private void restartTimer() {
        timer.setInitialDelay(randomDelayMillis());
        timer.start();
    }

    public static Double getRegionMaxDelaySeconds(Region2D<? super ChirpEmitter> region) {
        var data = region.getData();
        return (data instanceof ChirpEmitter) ? ((ChirpEmitter)data).maxDelaySeconds : null;
    }

    public static Double getRegionMinDelaySeconds(Region2D<? super ChirpEmitter> region) {
        var data = region.getData();
        return (data instanceof ChirpEmitter) ? ((ChirpEmitter)data).minDelaySeconds : null;
    }

    public static void setRegionMaxDelaySeconds(
        Region2D<? super ChirpEmitter> region,
        double value
    ) {
        var data = region.getData();
        if (data instanceof ChirpEmitter) ((ChirpEmitter)data).maxDelaySeconds = value;
    }

    public static void setRegionMinDelaySeconds(
        Region2D<? super ChirpEmitter> region,
        double value
    ) {
        var data = region.getData();
        if (data instanceof ChirpEmitter) ((ChirpEmitter)data).minDelaySeconds = value;
    }
}

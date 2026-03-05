package michalwa.auditorium.playback;

import javax.swing.Timer;
import michalwa.auditorium.SpatialRegion;

/**
 * Plays a random clip from a pool periodically as long as it's in range
 */
public class AudioChirp extends SpatialAudio {
    private final Timer timer = new Timer(0, e -> play());
    private double minDelaySeconds = 1.0;
    private double maxDelaySeconds = 5.0;

    public AudioChirp(String name, AudioClip[] clips) {
        super(name, clips, false);

        timer.setRepeats(false);
        restartTimer();
    }

    @Override
    protected void finished() {
        if (getEffectiveVolume() > 0.0) restartTimer();
    }

    @Override
    public String getTypeName() {
        return "chirp";
    }

    private int randomDelayMillis() {
        return (int)((minDelaySeconds + Math.random() * (maxDelaySeconds - minDelaySeconds))
            * 1000.0);
    }

    private void restartTimer() {
        timer.setInitialDelay(randomDelayMillis());
        timer.start();
    }

    public static Double getRegionMaxDelaySeconds(SpatialRegion<? super AudioChirp> region) {
        var data = region.getData();
        return (data instanceof AudioChirp) ? ((AudioChirp)data).maxDelaySeconds : null;
    }

    public static Double getRegionMinDelaySeconds(SpatialRegion<? super AudioChirp> region) {
        var data = region.getData();
        return (data instanceof AudioChirp) ? ((AudioChirp)data).minDelaySeconds : null;
    }

    public static void setRegionMaxDelaySeconds(
        SpatialRegion<? super AudioChirp> region,
        double value
    ) {
        var data = region.getData();
        if (data instanceof AudioChirp) ((AudioChirp)data).maxDelaySeconds = value;
    }

    public static void setRegionMinDelaySeconds(
        SpatialRegion<? super AudioChirp> region,
        double value
    ) {
        var data = region.getData();
        if (data instanceof AudioChirp) ((AudioChirp)data).minDelaySeconds = value;
    }
}

package michalwa.auditorium.playback;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueListener;
import java.util.Random;
import javax.swing.Timer;
import michalwa.auditorium.SpatialRegion;

public class AudioChirp extends SpatialAudio implements AudioCueListener {
    Timer timer = new Timer(0, e -> trigger());
    public double minDelaySeconds = 1.0, maxDelaySeconds = 5.0;

    public AudioChirp(String name, AudioCue[] audioCues) {
        super(name, audioCues);

        for (int i = 0; i < audioCues.length; i++) {
            audioCues[i].addAudioCueListener(this);
        }

        timer.setRepeats(false);
        restartTimer();
    }

    @Override
    public void audioCueClosed(long now, AudioCue source) {}

    @Override
    public void audioCueOpened(long now, int threadPriority, int bufferSize, AudioCue source) {}

    @Override
    public String getTypeName() {
        return "chirp";
    }

    @Override
    public void instanceEventOccurred(AudioCueInstanceEvent event) {
        if (event.type == AudioCueInstanceEvent.Type.STOP_INSTANCE) restartTimer();
    }

    private int randomCueIndex() {
        return new Random().nextInt(audioCues.length);
    }

    private int randomDelayMillis() {
        return (int)((minDelaySeconds + Math.random() * (maxDelaySeconds - minDelaySeconds))
            * 1000.0);
    }

    private void restartTimer() {
        timer.setInitialDelay(randomDelayMillis());
        timer.start();
    }

    private void trigger() {
        int i = randomCueIndex();
        audioCues[i].setFramePosition(instanceIds[i], 0.0);
        audioCues[i].start(instanceIds[i]);
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

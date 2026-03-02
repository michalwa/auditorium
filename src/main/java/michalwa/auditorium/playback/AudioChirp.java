package michalwa.auditorium.playback;

import java.util.Random;

import javax.swing.Timer;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueListener;

public class AudioChirp extends SpatialAudio implements AudioCueListener {
    Timer timer = new Timer(0, e -> trigger()) {{ setRepeats(false); }};
    public float minDelaySeconds = 1.0f, maxDelaySeconds = 5.0f;

	public AudioChirp(String name, AudioCue[] audioCues) {
        super(name, audioCues);

        for (int i = 0; i < audioCues.length; i++) {
            audioCues[i].addAudioCueListener(this);
        }

        restartTimer();
    }

    @Override
    public String getTypeName() {
        return "chirp";
    }

    @Override
    public void instanceEventOccurred(AudioCueInstanceEvent event) {
        if (event.type == AudioCueInstanceEvent.Type.STOP_INSTANCE)
            restartTimer();
    }

    @Override
    public void audioCueOpened(long now, int threadPriority, int bufferSize, AudioCue source) {}

    @Override
    public void audioCueClosed(long now, AudioCue source) {}

    private void restartTimer() {
        timer.setInitialDelay(randomDelayMillis());
        timer.start();
    }

    private void trigger() {
        int i = randomCueIndex();
        audioCues[i].setFramePosition(instanceIds[i], 0.0);
        audioCues[i].start(instanceIds[i]);
    }

    private int randomCueIndex() {
        return new Random().nextInt(audioCues.length);
    }

    private int randomDelayMillis() {
        return (int)((
            minDelaySeconds
                + (float)Math.random()
                * (maxDelaySeconds - minDelaySeconds)
            ) * 1000.0f);
    }
}

package michalwa.auditorium.playback;

import javax.swing.Timer;

import com.adonax.audiocue.AudioCue;
import com.adonax.audiocue.AudioCueInstanceEvent;
import com.adonax.audiocue.AudioCueListener;

public class AudioChirp extends SpatialAudio {
    Timer timer = new Timer(0, e -> trigger()) {{ setRepeats(false); }};
    public float minDelaySeconds = 1.0f, maxDelaySeconds = 5.0f;

	public AudioChirp(String name, AudioCue audioCue) {
        super(name, audioCue);

        audioCue.addAudioCueListener(new AudioCueListener() {
            @Override
            public void instanceEventOccurred(AudioCueInstanceEvent event) {
                if (event.type == AudioCueInstanceEvent.Type.STOP_INSTANCE)
                    restartTimer();
            }

            @Override
            public void audioCueOpened(long now, int threadPriority, int bufferSize, AudioCue source) {}

            @Override
            public void audioCueClosed(long now, AudioCue source) {}
        });

        restartTimer();
    }

    @Override
    public String getTypeName() {
        return "chirp";
    }

    private void restartTimer() {
        timer.setInitialDelay(randomDelayMillis());
        timer.start();
    }

    private void trigger() {
        audioCue.setFramePosition(instanceId, 0.0);
        audioCue.start(instanceId);
    }

    private int randomDelayMillis() {
        return (int)((
            minDelaySeconds
                + (float)Math.random()
                * (maxDelaySeconds - minDelaySeconds)
            ) * 1000.0f);
    }
}

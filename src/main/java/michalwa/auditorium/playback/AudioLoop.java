package michalwa.auditorium.playback;

import michalwa.auditorium.playback.v2.AudioClip;

/**
 * Plays an audio clip continuously as long as it's in range
 */
public class AudioLoop extends SpatialAudio {
    public AudioLoop(String name, AudioClip[] clips) {
        super(name, clips, true);
    }

    @Override
    public String getTypeName() {
        return "loop";
    }

    @Override
    public void setVolume(double volume) {
        super.setVolume(volume);

        if (getEffectiveVolume() > 0.0) play();
        else stop();
    }
}

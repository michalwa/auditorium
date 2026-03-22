package michalwa.auditorium.playback;

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
    public void setVolume(float volume) {
        super.setVolume(volume);

        if (volume > 0.0) play();
        else stop();
    }
}

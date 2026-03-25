package michalwa.auditorium.playback;

/**
 * Plays an audio clip continuously as long as it's in range
 */
public class LoopEmitter extends Emitter {
    private static final long serialVersionUID = 2026_03_26_002L;

    public LoopEmitter(String name, AudioClip[] clips) {
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

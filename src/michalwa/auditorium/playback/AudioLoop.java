package michalwa.auditorium.playback;

import com.adonax.audiocue.AudioCue;

public class AudioLoop extends SpatialAudio {
    public AudioLoop(String name, AudioCue audioCue) {
        super(name, audioCue);
        setLooping(true);
    }

    @Override
    public String getTypeName() {
        return "loop";
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);

        if (volume > 0.0f) {
            if (!audioCue.getIsPlaying(instanceId))
                audioCue.start(instanceId);
        } else {
            if (audioCue.getIsPlaying(instanceId))
                audioCue.stop(instanceId);
        }
    }
}

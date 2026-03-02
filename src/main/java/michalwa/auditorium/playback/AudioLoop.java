package michalwa.auditorium.playback;

import com.adonax.audiocue.AudioCue;

public class AudioLoop extends SpatialAudio {
    public AudioLoop(String name, AudioCue[] audioCues) {
        super(name, audioCues);
        setLooping(true);
    }

    @Override
    public String getTypeName() {
        return "loop";
    }

    @Override
    public void setVolume(float volume) {
        super.setVolume(volume);

        for (int i = 0; i < audioCues.length; i++) {
            if (volume > 0.0f) {
                if (!audioCues[i].getIsPlaying(instanceIds[i]))
                    audioCues[i].start(instanceIds[i]);
            } else {
                if (audioCues[i].getIsPlaying(instanceIds[i]))
                    audioCues[i].stop(instanceIds[i]);
            }
        }
    }
}

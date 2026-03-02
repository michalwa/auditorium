package michalwa.auditorium.playback;

import java.util.stream.Stream;

import com.adonax.audiocue.AudioCue;

public abstract class SpatialAudio {
    private String name;
    protected AudioCue[] audioCues;
    protected int[] instanceIds;
    protected float effectiveVolume = 1.0f;

    SpatialAudio(String name, AudioCue[] audioCues) {
        this.name = name;
        this.audioCues = audioCues;

        instanceIds = Stream.of(audioCues).mapToInt(AudioCue::obtainInstance).toArray();
    }

    public abstract String getTypeName();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVolume(float volume) {
        effectiveVolume = Math.clamp(volume, 0.0f, 1.0f);

        for (int i = 0; i < audioCues.length; i++) {
            if (audioCues[i].getIsActive(instanceIds[i]))
                audioCues[i].setVolume(instanceIds[i], volume);
        }
    }

    public float getEffectiveVolume() {
        return effectiveVolume;
    }

    protected void setLooping(boolean looping) {
        for (int i = 0; i < audioCues.length; i++) {
            audioCues[i].setLooping(instanceIds[i], looping ? -1 : 0);
        }
    }
}

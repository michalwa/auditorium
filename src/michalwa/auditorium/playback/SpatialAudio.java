package michalwa.auditorium.playback;

import com.adonax.audiocue.AudioCue;

public abstract class SpatialAudio {
    private String name;
    protected AudioCue audioCue;
    protected int instanceId;
    protected float effectiveVolume = 1.0f;

    SpatialAudio(String name, AudioCue audioCue) {
        this.name = name;
        this.audioCue = audioCue;

        instanceId = audioCue.obtainInstance();
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

        if (audioCue.getIsActive(instanceId))
            audioCue.setVolume(instanceId, volume);
    }

    public float getEffectiveVolume() {
        return effectiveVolume;
    }

    protected void setLooping(boolean looping) {
        audioCue.setLooping(instanceId, looping ? -1 : 0);
    }
}

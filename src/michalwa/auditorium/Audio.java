package michalwa.auditorium;

import com.adonax.audiocue.AudioCue;

class Audio {
    private String name;
    private AudioCue audioCue;
    private int instanceId;
    private float effectiveVolume = 1.0f;

    public Audio(String name, AudioCue audioCue) {
        this.name = name;
        this.audioCue = audioCue;

        instanceId = audioCue.obtainInstance();
        audioCue.setLooping(instanceId, -1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVolume(float volume) {
        effectiveVolume = Math.clamp(volume, 0.0f, 1.0f);

        if (volume > 0.0f) {
            if (!audioCue.getIsPlaying(instanceId))
                audioCue.start(instanceId);

            audioCue.setVolume(instanceId, volume);
        } else {
            if (audioCue.getIsPlaying(instanceId))
                audioCue.stop(instanceId);
        }
    }

    public float getEffectiveVolume() {
        return effectiveVolume;
    }
}

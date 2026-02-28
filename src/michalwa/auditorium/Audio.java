package michalwa.auditorium;

import com.adonax.audiocue.AudioCue;

class Audio {
    private AudioCue audioCue;
    private int instanceId;

    public Audio(AudioCue audioCue) {
        this.audioCue = audioCue;
        instanceId = audioCue.obtainInstance();
        audioCue.setLooping(instanceId, -1);
    }

    public void setVolume(float volume) {
        if (volume > 0.0f) {
            if (!audioCue.getIsPlaying(instanceId))
                audioCue.start(instanceId);

            audioCue.setVolume(instanceId, volume);
        } else {
            if (audioCue.getIsPlaying(instanceId))
                audioCue.stop(instanceId);
        }
    }
}

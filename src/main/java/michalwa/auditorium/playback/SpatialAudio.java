package michalwa.auditorium.playback;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import michalwa.auditorium.SpatialRegion;
import michalwa.auditorium.playback.v2.AudioClip;
import michalwa.auditorium.playback.v2.AudioPlayer;

/**
 * Abstract base class for an audio clip played from a point in space
 */
public abstract class SpatialAudio {
    private static final Logger logger = Logger.getLogger(SpatialAudio.class.getName());

    protected final AudioClip[] clips;
    private String name;
    private double effectiveVolume = 1.0f;
    private boolean looping = false;
    private AudioPlayer player;

    SpatialAudio(String name, AudioClip[] clips, boolean looping) {
        this.name = name;
        this.clips = clips;
        this.looping = looping;
    }

    /**
     * Called when the currently playing clip is finished playing
     */
    protected void finished() {}

    public double getEffectiveVolume() {
        return effectiveVolume;
    }

    public String getName() {
        return name;
    }

    private AudioClip getRandomClip() {
        return clips[new Random().nextInt(clips.length)];
    }

    /**
     * @return Human-readable string describing the type of this
     *         {@link SpatialAudio} object
     */
    public abstract String getTypeName();

    /**
     * Starts playing a random clip if no clip is already playing
     */
    protected void play() {
        if (player != null && player.isPlaying()) return;

        try {
            player = new AudioPlayer(getRandomClip(), looping);
            player.addFinishListener(this::finished);
            player.start();
        } catch (LineUnavailableException e) {
            logger.log(Level.WARNING, "Unable to open audio data line", e);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the playback volume level of the audio (0..1)
     */
    public void setVolume(double volume) {
        effectiveVolume = Math.clamp(volume, 0.0, 1.0);

        // TODO: Update gain controls on all clips
    }

    /**
     * Stops the currently playing clip
     */
    protected void stop() {
        if (player == null) return;

        player.stop();
        player = null;
    }

    public static String getRegionName(SpatialRegion<? extends SpatialAudio> region) {
        return region.getData().getName();
    }

    public static void setRegionName(SpatialRegion<? extends SpatialAudio> region, String name) {
        region.getData().setName(name);
    }
}

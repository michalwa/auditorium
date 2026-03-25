package michalwa.auditorium.playback;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import michalwa.auditorium.Randomizer;
import michalwa.auditorium.SpatialRegion;

/**
 * Abstract base class for an audio clip played from a point in space
 */
public abstract class SpatialAudio implements Serializable, SpatialRegion.Data {
    private static final long serialVersionUID = 2026_03_22_002L;
    private static final Logger logger = Logger.getLogger(SpatialAudio.class.getName());

    protected final AudioClip[] clips;
    private String name;
    private boolean looping = false;
    private boolean killed = false;
    private float baseVolume = 1.0f;
    private transient Randomizer randomizer;
    private transient AudioPlayer player;
    private transient VolumeOperator volumeOperator;
    private transient LevelReader levelReader;

    SpatialAudio(String name, AudioClip[] clips, boolean looping) {
        this.name = name;
        this.clips = clips;
        this.looping = looping;
    }

    /**
     * Called when the currently playing clip is finished playing
     */
    protected void finished() {}

    public float getBaseVolume() {
        return baseVolume;
    }

    @Override
    public float getDynamicIntensity() {
        return baseVolume + 3.0f * levelReader.nextSmoothLevel();
    }

    public String getName() {
        return name;
    }

    private AudioClip getRandomClip() {
        return clips[randomizer.next()];
    }

    @Override
    public float getStaticIntensity() {
        return baseVolume;
    }

    /**
     * @return Human-readable string describing the type of this
     *         {@link SpatialAudio} object
     */
    public abstract String getTypeName();

    public float getVolume() {
        return volumeOperator.getTargetVolume();
    }

    /**
     * Initializes any dynamic logic required to make this object work. This is
     * separate from the constructor to account for initializing deserialized
     * objects.
     */
    public void initialize() {
        randomizer = new Randomizer(clips.length);
        volumeOperator = new VolumeOperator();
        levelReader = new LevelReader();
    }

    /**
     * Stops the currently playing clip and prevents any future playback
     */
    public void kill() {
        killed = true;
        stop();
    }

    /**
     * Starts playing a random clip if no clip is already playing
     */
    protected void play() {
        if (killed || player != null && player.isPlaying()) return;

        try {
            player = new AudioPlayer(getRandomClip(), looping);
            player.addOperator(levelReader);
            player.addOperator(volumeOperator);
            player.addFinishListener(this::finished);
            player.start();
        } catch (LineUnavailableException e) {
            logger.log(Level.WARNING, "Unable to open audio data line", e);
        }
    }

    public void setBaseVolume(float baseVolume) {
        this.baseVolume = baseVolume;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the playback volume level of the audio
     */
    public void setVolume(float volume) {
        volumeOperator.setTargetVolume(Math.clamp(baseVolume * volume, 0.0f, 1.0f));
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

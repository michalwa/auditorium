package michalwa.auditorium.playback;

/**
 * A no-op {@link AudioOperator} used to read the amplitude of samples
 */
public class LevelReader implements AudioOperator {
    private static final int DEFAULT_WINDOW_SIZE = 256;
    private static final float SMOOTH_LEVEL_SPEED = 0.3f;

    private final int windowSize;
    /**
     * Collected samples (channels * windowSize)
     */
    private float[] samples = new float[0];
    private int sampleIndex = 0;
    private float smoothLevel = 0.0f;

    public LevelReader() {
        this(DEFAULT_WINDOW_SIZE);
    }

    /**
     * @param windowSize the number of consecutive frames to take into account
     *                   when calculating the level
     */
    public LevelReader(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public float apply(float sample) {
        samples[sampleIndex++] = sample;
        return sample;
    }

    @Override
    public void finished() {
        samples = new float[0];
    }

    public float getLevel() {
        float min = 0.0f, max = 0.0f;

        for (var sample : samples) {
            min = Math.min(min, sample);
            max = Math.max(max, sample);
        }

        return (max - min) / 2.0f;
    }

    @Override
    public void nextFrame(int channels) {
        if (samples.length != channels * windowSize) {
            samples = new float[channels * windowSize];
            sampleIndex = 0;
        } else if (sampleIndex >= samples.length) {
            sampleIndex = 0;
        }
    }

    public float nextSmoothLevel() {
        smoothLevel += (getLevel() - smoothLevel) * SMOOTH_LEVEL_SPEED;
        return smoothLevel;
    }
}

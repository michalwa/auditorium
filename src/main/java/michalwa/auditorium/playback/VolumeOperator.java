package michalwa.auditorium.playback;

/**
 * Scales audio samples by a linear volume factor
 */
public class VolumeOperator implements AudioOperator {
    private float maxVolumeStep = 0.001f;
    private float targetVolume = 1.0f;
    private float volume = 0.0f;
    private float exponent = 4.0f;

    @Override
    public float apply(float sample) {
        return sample * (float)Math.pow(volume, exponent);
    }

    public float getExponent() {
        return exponent;
    }

    public float getMaxVolumeStep() {
        return maxVolumeStep;
    }

    public float getTargetVolume() {
        return targetVolume;
    }

    public float getVolume() {
        return volume;
    }

    @Override
    public void nextFrame(int channels) {
        var diff = targetVolume - volume;
        volume += Math.signum(diff) * Math.min(maxVolumeStep, Math.abs(diff));
    }

    /**
     * Sets the exponent of the volume function, where the samples are
     * multiplied by <code>volume ^ exponent</code>. Set to <code>1</code> for
     * linear volume scaling. Higher values result in more natural sounding
     * curves.
     */
    public void setExponent(float exponent) {
        this.exponent = exponent;
    }

    /**
     * Sets the maximum allowed change in volume between frames
     */
    public void setMaxVolumeStep(float maxVolumeStep) {
        this.maxVolumeStep = maxVolumeStep;
    }

    /**
     * Sets the target volume level. Depending on the maximum volume step, the
     * effective volume may take several frames to reach this level.
     *
     * @see #setMaxVolumeStep
     */
    public void setTargetVolume(float volume) {
        targetVolume = volume;
    }
}

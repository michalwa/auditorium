package michalwa.auditorium.playback;

/**
 * Abstract object which modifies audio samples
 */
public interface AudioOperator {
    /**
     * Applies the operator to the given sample
     *
     * @param sample the audio sample in the range <code>-1.0f</code> to
     *               <code>1.0f</code>
     * @return the modified sample in the range <code>-1.0f</code> to
     *         <code>1.0f</code>
     */
    float apply(float sample);

    /**
     * Called for each frame before processing samples. Implementations may
     * define update logic which needs to happen periodically, like gradual
     * value changes.
     */
    void nextFrame();
}

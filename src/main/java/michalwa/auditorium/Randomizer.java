package michalwa.auditorium;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Provides an algorithm for shuffling the order of a fixed number of elements
 * in a way that avoids repeats
 */
public class Randomizer {
    private final List<Integer> indices;
    private final Random random = new Random();
    private double exponent = 3.0f;

    public Randomizer(int size) {
        indices = new ArrayList<>(IntStream.range(0, size).boxed().toList());
    }

    /**
     * Returns a random index from <code>0</code> (inclusive) to
     * <code>size</code> (exclusive), with a decreasing chance to return indices
     * that were recently returned.
     */
    public int next() {
        // Decreasing chance to pick an item further towards the back of the
        // list
        var t = Math.pow(random.nextDouble(), exponent);
        var index = (int)(t * (indices.size() - 1));
        var item = indices.remove(index);
        indices.add(item);
        return item;
    }

    /**
     * Sets the exponent used in the distribution function, which ensures that
     * items that were recently picked (higher values) have less of a chance to
     * be picked again. Values less than or equal to <code>1.0</code> will break
     * this behavior, but are not illegal.
     */
    public void setDistributionExponent(double exponent) {
        this.exponent = exponent;
    }
}

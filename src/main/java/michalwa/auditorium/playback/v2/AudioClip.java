package michalwa.auditorium.playback.v2;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 * A preloaded, serializable stream of audio data
 */
public class AudioClip implements Serializable {
    private static final long serialVersionUID = 2026_03_05_001L;
    private static final Logger logger = Logger.getLogger(AudioClip.class.getName());

    /**
     * An identifying name for the clip for debugging purposes
     */
    private final String name;
    private final float sampleRate;
    private final int channels;
    private final byte[] bytes;
    private transient AudioFormat format = null;

    public AudioClip(AudioInputStream ais) throws IOException {
        this("", ais);
    }

    public AudioClip(String name, AudioInputStream ais) throws IOException {
        this.name = name;
        this.sampleRate = ais.getFormat().getSampleRate();
        this.channels = ais.getFormat().getChannels();
        bytes = AudioSystem.getAudioInputStream(getFormat(), ais).readAllBytes();

        logger.info("Loaded " + this);
    }

    public byte[] getBytes() {
        return bytes;
    }

    public AudioFormat getFormat() {
        if (format == null) {
            format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                16,
                channels,
                channels * 2, // 2 bytes per sample
                sampleRate,
                true,
                Map.of()
            );
        }

        return format;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        logger.info("Deserialized " + this);
    }

    @Override
    public String toString() {
        return String.format(
            "%s (%s, %s @ %d Hz)",
            name,
            humanByteSize(bytes.length),
            humanChannels(channels),
            (int)sampleRate
        );
    }

    private static String humanByteSize(int bytes) {
        var count = (float)bytes;
        var units = Stream.of("kb", "Mb", "Gb").iterator();
        var unit = "bytes";

        while (units.hasNext() && count >= 1024) {
            unit = units.next();
            count /= 1024;
        }

        return String.format(Locale.of("en"), "%.1f %s", count, unit);
    }

    private static String humanChannels(int channels) {
        return switch (channels) {
            case 1 -> "mono";
            case 2 -> "stereo";
            default -> channels + " channels";
        };
    }
}

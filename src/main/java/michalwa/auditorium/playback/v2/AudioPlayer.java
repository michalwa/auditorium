package michalwa.auditorium.playback.v2;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Streams data from an {@link AudioClip} into a {@link SourceDataLine}
 */
public class AudioPlayer implements Runnable {
    private static final int BUFFER_FRAMES = 512;
    private static final Logger logger = Logger.getLogger(AudioPlayer.class.getName());

    private final AudioClip clip;
    private final SourceDataLine dataLine;
    private Thread thread;
    private boolean playing = false;
    private boolean looping;
    private int readFrame = 0;

    private final List<Runnable> finishListeners = new ArrayList<>();

    public AudioPlayer(AudioClip clip, boolean looping) throws LineUnavailableException {
        this.clip = clip;
        this.looping = looping;

        dataLine = AudioSystem.getSourceDataLine(clip.getFormat());
    }

    /**
     * Register the given listener to be called when the end of a playing clip
     * is reached. If this player is looping, it will only be called after it's
     * stopped.
     */
    public void addFinishListener(Runnable listener) {
        finishListeners.add(listener);
    }

    @Override
    public void run() {
        var format = clip.getFormat();
        var frameSize = format.getFrameSize();
        var sampleSize = format.getSampleSizeInBits() >> 3;
        var channels = format.getChannels();
        var byteOrder = format.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;

        assert sampleSize == 2 : "Expected 16-bit signed PCM";

        var readBuffer = ByteBuffer.wrap(clip.getBytes()).order(byteOrder);
        var readBufferFrames = readBuffer.capacity() / frameSize;

        var writeBuffer = ByteBuffer.allocate(BUFFER_FRAMES * frameSize).order(byteOrder);

        logger.info("Playing " + clip);

        try {
            dataLine.open(format, writeBuffer.capacity());
        } catch (LineUnavailableException e) {
            logger.log(Level.WARNING, "Could not open audio data line", e);
            playing = false;
            return;
        }

        dataLine.start();

        while (playing) {
            writeBuffer.clear();

            while (writeBuffer.hasRemaining()) {
                if (readFrame >= readBufferFrames) {
                    readFrame = 0;

                    if (looping) {
                        logger.info("Looping " + clip);
                    } else {
                        playing = false;
                        break;
                    }
                }

                for (var channel = 0; channel < channels; channel++) {
                    var sample = readBuffer.getShort(readFrame * frameSize + channel * sampleSize);
                    writeBuffer.putShort(sample);
                }

                readFrame++;
            }

            dataLine.write(writeBuffer.array(), 0, writeBuffer.position());
        }

        dataLine.stop();
        dataLine.close();

        logger.info("Finished " + clip);

        for (var listener : finishListeners) listener.run();
    }

    public boolean isPlaying() {
        return playing;
    }

    public void start() {
        if (playing) return;
        playing = true;

        thread = new Thread(this);
        thread.start();
    }

    public void stop() {
        playing = false;
    }
}

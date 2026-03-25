package michalwa.auditorium;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import michalwa.auditorium.playback.AudioClip;
import michalwa.auditorium.playback.ChirpEmitter;
import michalwa.auditorium.playback.Emitter;
import michalwa.auditorium.playback.LoopEmitter;

class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    public static void exportData(Object data) {
        var fileDialog = new FileDialog((Dialog)null, "Export project", FileDialog.SAVE);
        fileDialog.setVisible(true);

        if (fileDialog.getFiles().length != 1) return;

        var file = fileDialog.getFiles()[0];

        try (var oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
            logger.info("Exported " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object importData() {
        var fileDialog = new FileDialog((Dialog)null, "Import project", FileDialog.LOAD);
        fileDialog.setMultipleMode(false);
        fileDialog.setVisible(true);

        if (fileDialog.getFiles().length != 1) return null;

        var file = fileDialog.getFiles()[0];

        try (var ois = new BackCompatObjectInputStream(new FileInputStream(file))) {
            var data = ois.readObject();
            logger.info("Imported " + file.getAbsolutePath());
            return data;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Emitter loadAudio(AudioFactory factory) {
        var fileDialog = new FileDialog((Dialog)null, "Open audio clip", FileDialog.LOAD);
        fileDialog.setMultipleMode(true);
        fileDialog.setVisible(true);

        var clips = Stream.of(fileDialog.getFiles()).map(file -> {
            try {
                var clip = new AudioClip(file.getName(), AudioSystem.getAudioInputStream(file));
                return clip;
            } catch (IOException | UnsupportedAudioFileException e) {
                e.printStackTrace();
                return null;
            }
        }).toArray(AudioClip[]::new);

        if (clips.length == 0) return null;

        var name = fileDialog.getFiles()[0].getName();

        return factory.createAudio(name, clips);
    }

    interface AudioFactory {
        Emitter createAudio(String name, AudioClip[] clips);
    }

    private static class BackCompatObjectInputStream extends ObjectInputStream {
        BackCompatObjectInputStream(InputStream is) throws IOException {
            super(is);
        }

        @Override
        protected ObjectStreamClass readClassDescriptor()
            throws IOException,
                ClassNotFoundException {
            var desc = super.readClassDescriptor();

            return switch (desc.getName()) {
            case "michalwa.auditorium.playback.AudioChirp" -> ObjectStreamClass
                .lookup(ChirpEmitter.class);
            case "michalwa.auditorium.playback.AudioLoop" -> ObjectStreamClass
                .lookup(LoopEmitter.class);
            case "michalwa.auditorium.playback.SpatialAudio" -> ObjectStreamClass
                .lookup(Emitter.class);
            case "michalwa.auditorium.SpatialRegion" -> ObjectStreamClass.lookup(Region2D.class);
            default -> desc;
            };
        }
    }
}

package michalwa.auditorium;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.IOException;
import java.util.stream.Stream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import michalwa.auditorium.playback.AudioClip;
import michalwa.auditorium.playback.SpatialAudio;

class FilePicker {
    public static SpatialAudio loadAudio(AudioFactory factory) {
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
        SpatialAudio createAudio(String name, AudioClip[] clips);
    }
}

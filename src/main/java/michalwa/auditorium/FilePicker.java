package michalwa.auditorium;

import com.adonax.audiocue.AudioCue;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.IOException;
import java.util.stream.Stream;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import michalwa.auditorium.playback.SpatialAudio;

class FilePicker {
    public static SpatialAudio loadAudio(AudioFactory factory) {
        FileDialog fileDialog = new FileDialog((Dialog)null, "Open audio clip", FileDialog.LOAD);
        fileDialog.setMultipleMode(true);
        fileDialog.setVisible(true);

        AudioCue[] audioCues = Stream.of(fileDialog.getFiles()).map(file -> {
            try {
                AudioCue cue = AudioCue.makeStereoCue(file.toURI().toURL(), 1);
                cue.open();
                return cue;
            } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                System.out.println("Could not open file: " + e.getMessage());
                return null;
            }
        }).toArray(AudioCue[]::new);

        if (audioCues.length == 0) return null;

        String name = fileDialog.getFiles()[0].getName();

        return factory.createAudio(name, audioCues);
    }

    interface AudioFactory {
        SpatialAudio createAudio(String name, AudioCue[] audioCues);
    }
}

package michalwa.auditorium;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.adonax.audiocue.AudioCue;

import michalwa.auditorium.playback.SpatialAudio;

class FilePicker {
    interface AudioFactory {
        SpatialAudio createAudio(String name, AudioCue cue);
    }

    public static SpatialAudio loadAudio(AudioFactory factory) {
        FileDialog fileDialog = new FileDialog((Dialog)null, "Open audio clip", FileDialog.LOAD);
        fileDialog.setVisible(true);

        if (fileDialog.getFiles().length == 0) return null;
        File file = fileDialog.getFiles()[0];

        try {
            AudioCue cue = AudioCue.makeStereoCue(file.toURI().toURL(), 1);
            cue.open();
            return factory.createAudio(file.getName(), cue);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            System.out.println("Could not open file: " + e.getMessage());
            return null;
        }
    }
}

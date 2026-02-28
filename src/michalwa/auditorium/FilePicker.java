package michalwa.auditorium;

import java.awt.Dialog;
import java.awt.FileDialog;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

class FilePicker {
    public static Clip loadAudioClip() {
        FileDialog fileDialog = new FileDialog((Dialog)null, "Open audio clip", FileDialog.LOAD);
        fileDialog.setVisible(true);

        if (fileDialog.getFiles().length == 0) return null;

        try {
            AudioInputStream audioInputStream =
                AudioSystem.getAudioInputStream(fileDialog.getFiles()[0]);

            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            return clip;
        } catch (
            IOException
            | UnsupportedAudioFileException
            | LineUnavailableException e
        ) {
            System.out.println("Could not open file: " + e.getMessage());
            return null;
        }
    }
}

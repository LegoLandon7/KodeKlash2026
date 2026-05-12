package Game.Audio;

import Game.Gameplay.Weapon;
import Game.Util.ResourceLoader;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SFX {
    public static void playSound(String filePath) {
        try {
            // get audio data
            File file = ResourceLoader.getFile(filePath);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();
            DataLine.Info info =  new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);

            // play audio
            clip.open(stream);
            clip.start();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

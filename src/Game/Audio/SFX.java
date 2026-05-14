// Landon Lego
// 5/12/26
// This sfx class can be used to play looped music or sound effects

package Game.Audio;

import Game.Util.ResourceLoader;

import javax.sound.sampled.*;
import java.net.URL;

public class SFX {
    private static Clip loopClip;

    private static Clip getClip(String filePath) {
        try {
            // get audio data
            URL url = ResourceLoader.getUrl(filePath);
            AudioInputStream stream = AudioSystem.getAudioInputStream(url);
            AudioFormat format = stream.getFormat();
            DataLine.Info info =  new DataLine.Info(Clip.class, format);
            Clip clip = (Clip) AudioSystem.getLine(info);

            // play audio
            clip.open(stream);

            // clean audio
            clip.addLineListener(e -> {
                if (e.getType() == LineEvent.Type.STOP && clip.getMicrosecondPosition() >= clip.getMicrosecondLength())
                    clip.close();
            });

            return clip;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void setVolume(Clip clip, float volume) {
        FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        volumeControl.setValue(min + (max - min) * volume);
    }

    public static void playSound(String filePath) {
        Clip clip = getClip(filePath);
        if (clip == null) return;

        setVolume(clip, 0.85f);
        clip.start();
    }

    public static void loopSound(String filePath) {
        stopLoop();
        loopClip = getClip(filePath);
        if (loopClip == null) return;

        setVolume(loopClip, 0.8f);
        loopClip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void stopLoop() {
        if (loopClip == null) return;
        loopClip.stop();
        loopClip.close();
        loopClip = null;
    }
}

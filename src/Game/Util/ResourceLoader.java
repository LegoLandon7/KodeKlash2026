// Landon Lego
// 5/13/26
// this file is used to load resources from the resource folder

package Game.Util;

import Game.Audio.SFX;
import Game.Entities.Entity;
import Game.Entities.EntityWave;
import Game.Entities.Path;
import Game.Gameplay.Weapon;
import Game.Instance.GameInstance;
import Game.User.Camera;
import Menu.MainMenuApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceLoader {
    public static BufferedImage loadImage(String filePath) throws IOException {
        return ImageIO.read(getResourceUrl(filePath));
    }
    public static Image loadIcon(String filePath) throws IOException {
        return new Image(getResourceUrl(filePath).toExternalForm());
    }

    public static FXMLLoader loadFXML(String filePath) throws IOException {
        return new FXMLLoader(getResourceUrl(filePath));
    }

    public static List<String> readFile(String filePath) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ResourceLoader.class.getResourceAsStream(filePath))));
        return reader.lines().collect(Collectors.toList());
    }

    public static URL getResourceUrl(String filePath) {
        return ResourceLoader.class.getResource(filePath);
    }

    public static Entity[] loadEntities(String filePath, Path path, Camera camera, GameInstance gameInstance) {
        List<String> lines = readFile(filePath);
        List<Entity> entities = new ArrayList<>();

        // loop through file
        for (String line : lines) {
            String[] object = getObject(line);
            if (object == null) continue;

            // add entity
            entities.add(new Entity(0, 0, object[0], path, camera, gameInstance,
                    toFloat(object[1]), toFloat(object[2]), toFloat(object[3]), toFloat(object[4]),
                    toInt(object[5])));
        }

        return entities.toArray(new Entity[0]);
    }

    public static Weapon[] loadWeapons(String filePath, EntityWave entityWave, int[][] map, Camera camera) {
        List<String> lines = readFile(filePath);
        List<Weapon> weapons = new ArrayList<>();

        // loop through file
        for (String line : lines) {
            String[] object = getObject(line);
            if (object == null) continue;

            // add weapon
            weapons.add(new Weapon(object[0], object[1], object[2],
                    toInt(object[3]), toLong(object[4]), toFloat(object[5]),
                    toFloat(object[6]), toInt(object[7]), entityWave, map, camera));
        }

        return weapons.toArray(new Weapon[0]);
    }

    // helpers
    private static int toInt(String input)       {return Integer.parseInt(input);}
    private static long toLong(String input)     {return Long.parseLong(input);}
    private static float toFloat(String input)   {return Float.parseFloat(input);}
    private static double toDouble(String input) {return Double.parseDouble(input);}

    private static String[] getObject(String line) {
        if (line.startsWith("#") || line.isBlank()) return null;
        return line.replace(" ", "").split(",");
    }
}

package Game.Util;

import Menu.MainMenuApplication;
import javafx.fxml.FXMLLoader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ResourceLoader {
    public static BufferedImage loadImage(String filePath) throws IOException {
        return ImageIO.read(Objects.requireNonNull(ResourceLoader.class.getClassLoader().getResourceAsStream(filePath)));
    }
    public static FXMLLoader loadFXML(String filePath) throws IOException {
        return new FXMLLoader(ResourceLoader.class.getResource(filePath));
    }

}

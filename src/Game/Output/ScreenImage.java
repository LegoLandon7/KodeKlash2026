package Game.Output;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;

public class ScreenImage {
    private final Image image;

    private final int x;
    private final int y;

    private final int width;
    private final int height;

    public ScreenImage(String filePath, int x, int y, int width, int height) {
        try {
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    // getters
    public Image getImage() {return image;}
    public int getX() {return x;}
    public int getY() {return y;}
    public int getWidth() {return width;}
    public int getHeight() {return height;}
}

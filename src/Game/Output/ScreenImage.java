// Landon Lego
// 5/12/26
// this file stores data used for HUD images on the window

package Game.Output;

import java.awt.image.BufferedImage;

// a record is essentially a class but for only storing data
// this makes it a lot more simple
public record ScreenImage(BufferedImage image, int x, int y, int width, int height) {}

//public class ScreenImage {
//    private final Image image;
//
//    private final int x;
//    private final int y;
//
//    private final int width;
//    private final int height;
//
//    public ScreenImage(BufferedImage image, int x, int y, int width, int height) {
//        this.image = image;
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//    }
//
//    // getters
//    public Image getImage() {return image;}
//    public int getX() {return x;}
//    public int getY() {return y;}
//    public int getWidth() {return width;}
//    public int getHeight() {return height;}
//}

// Landon Lego
// 5/12/26
// this file renders a window to the screen and allows the raycaster render the 3d scene

package Game.Output;

import Game.Util.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Window extends Canvas implements KeyListener {
    private final String title;
    private final int width;
    private final int height;

    private final BufferedImage mainImage;
    private final int[] pixels;

    private final ScreenImage[] hudImages;
    private final Set<Integer> pressedKeys = new HashSet<>();

    private JFrame frame;

    private BufferStrategy bufferStrategy;

    private String currentFps = "";
    private final Font font = new Font("Arial", Font.BOLD, 30);

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        // initialize the image data
        mainImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) mainImage.getRaster().getDataBuffer()).getData();
        hudImages = new ScreenImage[50];

        addKeyListener(this);
        initWindow();
    }

    private void initWindow() {
        // initialize frame / window settings and buffer
        frame = new JFrame(title);
        setPreferredSize(new Dimension(width, height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        setFocusable(true);
        requestFocus();

        try {
            frame.setIconImage(ResourceLoader.loadImage("assets/icons/icon.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

    public void setPixel(int x, int y, int color) {
        // out of bounds
        if (x < 0 || x >= width || y < 0 || y >= height) return;

        // set pixel
        pixels[y * width + x] = color;
    }

    public void fillRows(int start, int end, int color) {
        Arrays.fill(pixels, start * width, end * width, color);
    }

    public void fillRow(int x, int y, int length, int color) {
        int start = y * width + x;
        Arrays.fill(pixels, start, start + length, color);
    }

    public void showFps(String fps) {
        this.currentFps = fps;
    }

    public void addImage(ScreenImage image) {
        // add an image to next empty spot in array
        for (int i = 0; i < hudImages.length; i++) {
            if (hudImages[i] == null) {
                hudImages[i] = image;
                return;
            }
        }
    }

    public void clear(int color) {
        // fill whole array with one color
        Arrays.fill(pixels, color);
    }

    public void render() {
        // get graphics object from Canvas
        Graphics g = bufferStrategy.getDrawGraphics();

        // draw image to graphics
        g.drawImage(mainImage, 0, 0, width, height, null);

        // render all HUD images
        for (ScreenImage image : hudImages) {
            if (image == null) continue;
            g.drawImage(image.image(), image.x(), image.y(), image.width(), image.height(), null);
        }

        Arrays.fill(hudImages, null);

        // render fps
        frame.setTitle(title + " - " + currentFps);

        g.dispose();

        // show finished render
        bufferStrategy.show();
    }

    public void close() {
        frame.setVisible(false);
        frame.dispose();
    }

    // key input
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) { pressedKeys.remove(e.getKeyCode()); }
    @Override public void keyPressed(KeyEvent e) { pressedKeys.add(e.getKeyCode()); }

    public Set<Integer> getPressedKeys() { return pressedKeys; }
}